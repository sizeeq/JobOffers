package pl.joboffers.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.joboffers.BaseIntegrationTest;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.joboffers.OfferResponseStubJson.bodyWithZeroOffers;

public class HappyPathScenarioTest extends BaseIntegrationTest {

    @Autowired
    OfferFacade offerFacade;

    @Test
    public void f() throws Exception {

        // Initial state:
        // - Database: no offers
        // - External server: no offers
        // - No registered users
        // given & when & then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithZeroOffers())
                )
        );

        // step 1: Scheduler runs for the first time and fetches offers → 0 offers
        // System adds 0 offers to the database
        //given && when && then
        await().atMost(Duration.ofSeconds(20))
                .until(() -> {
                    List<OfferDto> offerDtos = offerFacade.fetchAndSaveNewOffers();
                    return offerDtos.isEmpty();
                });


        // step 2: User attempts to obtain a token via POST /token (username=someUser, password=somePassword)
        // System returns 401 UNAUTHORIZED

        // step 3: User calls GET /offers without a token
        // System returns 401 UNAUTHORIZED

        // step 4: User registers via POST /register (username=someUser, password=somePassword)
        // System creates the user (role: USER) and returns 200 OK

        // step 5: User logs in via POST /token and receives JWT=AAAA.BBBB.CCC
        // System returns 200 OK

        // step 6: User calls GET /offers with JWT
        // System returns 200 OK with an empty list (0 offers)

        //given
        String path = "/offers";

        //when
        ResultActions performResult = mockMvc.perform(get(path)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = performResult.andExpect(status().isOk()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        List<OfferDto> offerDtos = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertThat(offerDtos).isEmpty();

        // step 7: Two new offers appear on the external server

        // step 8: Scheduler runs again and fetches 2 offers
        // System adds offers with id=1000 and id=2000 (source=EXTERNAL) to the database

        // step 9: User calls GET /offers?page=0&size=10
        // System returns 200 OK with offers 1000, 2000

        // step 10: User calls GET /offers/notExistingId
        // System returns 404 NOT_FOUND (Offer with id notExistingId was not found)

        //given
        String notExistingId = "notExistingId";

        //when
        ResultActions performGetNotExistingId = mockMvc.perform(get("/offers/" + notExistingId));

        //then
        performGetNotExistingId.andExpect(status().isNotFound())
                .andExpect(content().json(
                        """
                                {
                                "message": "Offer with id: notExistingId was not found",
                                "httpStatus": "NOT_FOUND"
                                }
                                """.trim()));

        // step 11: User calls GET /offers/1000
        // System returns 200 OK with details of offer 1000

        // step 12: Two more offers appear on the external server

        // step 13: Scheduler runs for the third time and fetches 2 new offers
        // System adds offers with id=3000 and id=4000 (source=EXTERNAL) to the database

        // step 14: User calls GET /offers?page=0&size=10&keyword=java
        // System returns 200 OK with offers 1000, 2000, 3000, 4000 (filtered by keyword)

        // step 15: User adds their own offer via POST /offers with JWT
        // System validates data, saves offer id=5000 (source=USER)
        // System returns 201 CREATED with header Location: /offers/5000

        // step 16: User calls GET /offers
        // System returns 200 OK with offers 1000, 2000, 3000, 4000, 5000

        // step 17: User calls GET /offers?mine=true
        // System returns 200 OK with only their own offers (id=5000)

        // step 18: User attempts DELETE /offers/1000
        // System returns 403 FORBIDDEN (only ADMIN can delete)

        // step 19: Administrator logs in via POST /token (username=admin, password=adminPass)
        // System returns 200 OK and token DDD.EEE.FFF

        // step 20: Administrator executes DELETE /offers/1000 with JWT
        // System deletes offer 1000 and returns 204 NO_CONTENT

        // step 21: User calls GET /offers again
        // System returns 200 OK with offers 2000, 3000, 4000, 5000
    }
}
