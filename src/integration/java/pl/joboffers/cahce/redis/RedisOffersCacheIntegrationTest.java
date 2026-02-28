package pl.joboffers.cahce.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import pl.joboffers.BaseIntegrationTest;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.JwtResponseDto;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedisOffersCacheIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS;

    @MockitoSpyBean
    OfferFacade offerFacade;

    @Autowired
    CacheManager cacheManager;

    static {
        REDIS = new GenericContainer<>("redis").withExposedPorts(6379);
        REDIS.start();
    }

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort().toString());
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.cache.redis.time-to-live", () -> "PT1S");
    }

    @Test
    @DisplayName("Should save offers to cache and then invalidate it when time to live passes")
    public void should_save_offers_to_cache_and_then_invalidate_by_time_to_live() throws Exception {
        //step 1: someUser with somePassword was registered

        //given
        String registerContent = """
                {
                    "username": "someUser",
                    "password": "somePassword"
                }
                """.trim();

        //when && then
        mockMvc.perform(post("/register")
                        .content(registerContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //step 2: login

        //given
        String loginContent = """
                {
                    "username": "someUser",
                    "password": "somePassword"
                }
                """.trim();

        //when && then
        MvcResult mvcTokenResult = mockMvc.perform(post("/token")
                        .content(loginContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String tokenJson = mvcTokenResult.getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(tokenJson, JwtResponseDto.class);

        String jwtToken = jwtResponseDto.token();

        //step 3: should save offers request to cache

        //given && when
        mockMvc.perform(get("/offers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        verify(offerFacade, times(1)).findAllOffers(any(Pageable.class));
        assertThat(cacheManager.getCacheNames().contains("jobOffers")).isTrue();

        //step 4: cache should be invalidated

        // given && when && then
        await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    mockMvc.perform(get("/offers")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());

                    verify(offerFacade, atLeast(2)).findAllOffers(any(Pageable.class));
                });
    }
}
