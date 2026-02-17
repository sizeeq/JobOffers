package pl.joboffers.JobOffers.domain.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Document
public record Offer(
        @Id
        String id,

        @Field("company")
        String company,

        @Field("position")
        @JsonProperty("title")
        String position,

        @Field("salary")
        String salary,

        @Field("offerUrl")
        @Indexed(unique = true)
        String offerUrl
) {
}
