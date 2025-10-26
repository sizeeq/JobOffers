package pl.joboffers.JobOffers.domain.offer.dto;

import lombok.Builder;

@Builder
public record OfferDto(
        String id,
        String company,
        String position,
        String salary,
        String offerUrl
) {
}
