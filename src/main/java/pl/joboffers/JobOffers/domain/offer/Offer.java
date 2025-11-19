package pl.joboffers.JobOffers.domain.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Offer(
        String id,
        String company,
        @JsonProperty("title") String position,
        String salary,
        String offerUrl
) {
}
