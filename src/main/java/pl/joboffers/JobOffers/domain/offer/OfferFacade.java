package pl.joboffers.JobOffers.domain.offer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferUpdateRequestDto;

import java.util.List;

@Component
public class OfferFacade {

    private final OfferService offerService;

    public OfferFacade(OfferRepository offerRepository, OfferFetcher offerFetcher) {
        this.offerService = new OfferService(offerRepository, offerFetcher);
    }

    public List<OfferDto> fetchAndSaveNewOffers() {
        return offerService.fetchAndSaveNewOffers().stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    public List<OfferDto> findAllOffers() {
        return offerService.findAllOffers().stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    public OfferDto findById(String id) {
        Offer offerById = offerService.findById(id);
        return OfferMapper.toDto(offerById);
    }

    public Page<OfferDto> findAllOffers(Pageable pageable) {
        return offerService.findAllOffers(pageable)
                .map(OfferMapper::toDto);
    }

    public OfferDto save(OfferRequestDto requestDto) {
        Offer savedOffer = offerService.save(requestDto);

        return OfferMapper.toDto(savedOffer);
    }

    public List<OfferDto> saveAll(List<OfferRequestDto> requestDtos) {
        return offerService.saveAll(requestDtos).stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    public void deleteOfferById(String id) {
        offerService.deleteOfferById(id);
    }

    public OfferDto updateOfferById(String id, OfferUpdateRequestDto updateRequestDto) {
        return offerService.updateOffer(id, updateRequestDto);
    }
}
