package pl.joboffers.JobOffers.infrastructure.offer.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OfferFetcherClientConfig {

    @Bean
    public RestClient offerFetcherRestClient(HttpOfferFetcherClientProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectionTimeout());
        factory.setReadTimeout(properties.getReadTimeout());

        String baseUrl = getBaseUrl(properties);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Bean
    public OfferFetcherClient offerFetcherClient(RestClient restClient, HttpOfferFetcherClientProperties properties) {
        return new OfferFetcherClient(restClient, properties);
    }

    private static @NotNull String getBaseUrl(HttpOfferFetcherClientProperties properties) {
        return properties.getUri() + ":" + properties.getPort();
    }
}
