package pl.joboffers.JobOffers.domain.offer;

import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferUpdateRequestDto;

import java.util.List;

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

    public static Offer toEntity(OfferRequestDto requestDto) {
        return Offer.builder()
                .company(requestDto.company())
                .position(requestDto.position())
                .salary(requestDto.salary())
                .offerUrl(requestDto.offerUrl())
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

    public static List<OfferDto> toDto(List<Offer> offers) {
        return offers.stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    public static Offer mapUpdateDtoToEntity(Offer offer, OfferUpdateRequestDto updateRequestDto) {
        return Offer.builder()
                .id(offer.id())
                .company(updateRequestDto.company())
                .position(updateRequestDto.position())
                .salary(updateRequestDto.salary())
                .offerUrl(updateRequestDto.offerUrl())
                .build();
    }
}
