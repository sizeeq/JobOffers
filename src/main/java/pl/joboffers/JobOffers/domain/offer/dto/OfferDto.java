package pl.joboffers.JobOffers.domain.offer.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record OfferDto(
        String id,
        String company,
        String position,
        String salary,
        String offerUrl
) implements Serializable {
}
