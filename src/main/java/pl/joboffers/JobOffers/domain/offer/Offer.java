package pl.joboffers.JobOffers.domain.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document
public record Offer(
        @Id
        String id,
        String company,
        @JsonProperty("title")
        String position,
        String salary,
        @Indexed(unique = true)
        String offerUrl
) {
}
