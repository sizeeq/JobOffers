package pl.joboffers.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import pl.joboffers.BaseIntegrationTest;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.JwtResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.joboffers.OfferResponseStubJson.*;

public class HappyPathScenarioTest extends BaseIntegrationTest {

    @Autowired
    OfferFacade offerFacade;

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offers.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("offers.http.client.config.port", () -> wireMockServer.getPort());
    }

    @Test
    @DisplayName("User should be able to view and add job offers while system fetched offers from external server")
    public void user_should_be_able_to_view_and_add_job_offers_while_system_syncs_with_external_server() throws Exception {

        // Initial state:
        // - Database: no offers
        // - External server: no offers
        // - No registered users

        // given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithZeroOffers())
                )
        );


        // step 1: Scheduler runs for the first time and fetches offers → 0 offers
        // System adds 0 offers to the database

        //given && when
        List<OfferDto> offerDtos = offerFacade.fetchAndSaveNewOffers();

        //then
        assertThat(offerDtos.isEmpty());


        // step 2: User attempts to obtain a token via POST /token (username=someUser, password=somePassword)
        // System returns 401 UNAUTHORIZED

        //given
        String postTokenPath = "/token";
        String postTokenContent = """
                {
                "username": "someUsername",
                "password": "somePassword"
                }
                """.trim();

        //when && then
        mockMvc.perform(post(postTokenPath)
                        .content(postTokenContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Bad Credentials"))
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));


        // step 3: User calls GET /offers without a token
        // System returns 403 FORBIDDEN

        //given
        String getOffersPath = "/offers";

        //when && then
        mockMvc.perform(get(getOffersPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());


        // step 4: User registers via POST /register (username=someUsername, password=somePassword)
        // System creates the user (role: USER) and returns 201 CREATED

        //given
        String postRegisterPath = "/register";
        String postRegisterContent = """
                {
                "username": "someUsername",
                "password": "somePassword"
                }
                """.trim();

        //when && then
        mockMvc.perform(post(postRegisterPath)
                        .content(postRegisterContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("someUsername"))
                .andExpect(jsonPath("$.isCreated").value(true));


        // step 5: User logs in via POST /token and receives token JWT=AAAA.BBBB.CCC
        // System returns 200 OK

        //given && when && then
        MvcResult postTokenResult = mockMvc.perform(post(postTokenPath)
                        .content(postTokenContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("someUsername"))
                .andExpect(jsonPath("$.token", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")))
                .andReturn();

        String contentAsString = postTokenResult.getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(contentAsString, JwtResponseDto.class);
        String jwtToken = jwtResponseDto.token();


        // step 6: User calls GET /offers with JWT token
        // System returns 200 OK with an empty list (0 offers)

        //given && when && then
        mockMvc.perform(get(getOffersPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));


        // step 7: Two new offers appear on the external server

        //given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithTwoOffers())
                )
        );


        // step 8: Scheduler runs again and fetches 2 offers
        // System adds offers with id=1 and id=2 to the database

        //given && when
        List<OfferDto> offerDtoWithTwoOffers = offerFacade.fetchAndSaveNewOffers();

        //then
        assertThat(offerDtoWithTwoOffers).hasSize(2);


        // step 9: User calls GET /offers
        // System returns 200 OK with offers 1, 2

        //given && when && then
        mockMvc.perform(get(getOffersPath)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));


        // step 10: User calls GET /offers/notExistingId
        // System returns 404 NOT_FOUND (Offer with id notExistingId was not found)

        //given
        String notExistingId = "notExistingId";

        //when && then
        mockMvc.perform(get(getOffersPath + "/" + notExistingId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Offer with id: notExistingId was not found"))
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"));


        // step 11: User calls GET /offers/1
        // System returns 200 OK with details of offer 1

        //given && when && then
        mockMvc.perform(get(getOffersPath + "/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.company").value("Acme Corp"))
                .andExpect(jsonPath("$.position").value("Junior Java Developer"))
                .andExpect(jsonPath("$.salary").value("7000–9000 PLN"))
                .andExpect(jsonPath("$.offerUrl").value("https://example.com/offers/1"));


        // step 12: Two more offers appear on the external server

        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithFourOffers())
                )
        );


        // step 13: Scheduler runs for the third time and fetches 2 new offers
        // System adds offers with id=3 and id=4 to the database

        //given && when
        List<OfferDto> offerDtoTwoNewOffers = offerFacade.fetchAndSaveNewOffers();

        //then
        assertThat(offerDtoTwoNewOffers).hasSize(2);


        // step 14: User calls GET /offers
        // System returns 200 OK with offers 1, 2, 3, 4

        //given && when && then
        mockMvc.perform(get(getOffersPath)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[2].company").value("NextGen Software"))
                .andExpect(jsonPath("$[3].company").value("CloudForge"));


        // step 15: User adds their own offer via POST /offers with JWT
        // System validates data, saves offer id=5000 (source=USER)
        // System returns 201 CREATED with header Location: /offers/5

        //given
        String postOfferContent = """
                {
                "company": "Nikmet",
                "position": "Informatyk",
                "salary": "6000PLN",
                "offerUrl": "https://nikmet.pl/"
                }
                """.trim();

        //when && then
        mockMvc.perform(
                        post("/offers")
                                .content(postOfferContent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwtToken)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.company").value("Nikmet"))
                .andExpect(jsonPath("$.position").value("Informatyk"))
                .andExpect(jsonPath("$.salary").value("6000PLN"))
                .andExpect(jsonPath("$.offerUrl").value("https://nikmet.pl/"));


        // step 16: User calls GET /offers
        // System returns 200 OK with offers 1, 2, 3, 4, generatedId*

        //given && when && then
        mockMvc.perform(get(getOffersPath)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(5))
                .andExpect(jsonPath("$[4].company").value("Nikmet"));
    }
}
