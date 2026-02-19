package pl.joboffers.JobOffers.infrastructure.offer.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import pl.joboffers.JobOffers.domain.offer.Offer;
import pl.joboffers.JobOffers.domain.offer.OfferFetcher;
import pl.joboffers.JobOffers.domain.offer.OfferMapper;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;

import java.util.Collections;
import java.util.List;

@Log4j2
public class OfferFetcherClient implements OfferFetcher {

    private final RestClient restClient;
    private final HttpOfferFetcherClientProperties properties;

    public OfferFetcherClient(RestClient restClient, HttpOfferFetcherClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public List<OfferDto> fetchOffers() {
        log.info("Fetching job offers from external service...");

        try {
            List<Offer> offers = makeGetRequest();
            log.info("Successfully fetched {} offers", offers.size());
            return OfferMapper.toDto(offers);
        } catch (RestClientResponseException e) {
            log.error("External service responded with error: {} {}", e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Failed to fetch job offers due to: {}", e.getMessage());
        }

        return Collections.emptyList();
    }

    private List<Offer> makeGetRequest() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getPath())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "Client error while fetching offers");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error while fetching offers");
                }))
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
