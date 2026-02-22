package pl.joboffers.http.error;

import org.springframework.web.client.RestClient;
import pl.joboffers.JobOffers.infrastructure.offer.client.HttpOfferFetcherClientProperties;
import pl.joboffers.JobOffers.infrastructure.offer.client.OfferFetcherClient;
import pl.joboffers.JobOffers.infrastructure.offer.client.OfferFetcherClientConfig;

public class OfferFetcherClientIntegrationTestConfig extends OfferFetcherClientConfig {

    public OfferFetcherClient offerFetcherClient(int wireMockPort) {
        HttpOfferFetcherClientProperties properties = new HttpOfferFetcherClientProperties();
        properties.setUri("http://localhost");
        properties.setPort(wireMockPort);
        properties.setPath("/offers");
        properties.setReadTimeout(1000);
        properties.setConnectionTimeout(1000);

        RestClient restClient = offerFetcherRestClient(properties);

        return offerFetcherClient(restClient, properties);
    }
}
