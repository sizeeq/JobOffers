package pl.joboffers.JobOffers.infrastructure.offer.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "offers.http.client.config")
@Setter
@Getter
public class HttpOfferFetcherClientProperties {

    private String uri;
    private int port;
    private String path;
    private int connectionTimeout;
    private int readTimeout;
    private int writeTimeout;
}

