package pl.joboffers.JobOffers.domain.offer;

import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;

import java.util.List;

public interface OfferFetcher {

    List<OfferDto> fetchOffers();
}
