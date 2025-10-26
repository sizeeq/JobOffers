package pl.joboffers.JobOffers.domain.offer;

import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;

public class OfferMapper {

    public static Offer toEntity(OfferDto offerDto) {
        return Offer.builder()
                .id(offerDto.id())
                .company(offerDto.company())
                .position(offerDto.position())
                .salary(offerDto.salary())
                .offerUrl(offerDto.offerUrl())
                .build();
    }

    public static OfferDto toDto(Offer offer) {
        return OfferDto.builder()
                .id(offer.id())
                .company(offer.company())
                .position(offer.position())
                .salary(offer.salary())
                .offerUrl(offer.offerUrl())
                .build();
    }

    public static Offer toEntity(OfferRequestDto requestDto) {
        return Offer.builder()
                .company(requestDto.company())
                .position(requestDto.position())
                .salary(requestDto.salary())
                .offerUrl(requestDto.offerUrl())
                .build();
    }
}
