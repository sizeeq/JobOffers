package pl.joboffers.JobOffers.domain.offer;

import org.springframework.dao.DuplicateKeyException;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;
import pl.joboffers.JobOffers.domain.offer.exception.OfferAlreadyExistsException;
import pl.joboffers.JobOffers.domain.offer.exception.OfferNotFoundException;

import java.util.List;
import java.util.UUID;

public class OfferService {

    private final OfferRepository repository;
    private final OfferFetcher offerFetcher;

    public OfferService(OfferRepository repository, OfferFetcher offerFetcher) {
        this.repository = repository;
        this.offerFetcher = offerFetcher;
    }

    public List<Offer> fetchAndSaveNewOffers() {
        List<OfferDto> externalOffers = offerFetcher.fetchOffers();

        return externalOffers.stream()
                .filter(offerDto -> !repository.existsByOfferUrl(offerDto.offerUrl()))
                .filter(offerDto -> !offerDto.offerUrl().isEmpty())
                .map(OfferMapper::toEntity)
                .map(repository::save)
                .toList();
    }

    public List<Offer> findAllOffers() {
        return repository.findAll();
    }

    public Offer findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException(String.format("Offer with id: %s was not found", id)));
    }

    public Offer save(OfferRequestDto requestDto) {
        if (repository.existsByOfferUrl(requestDto.offerUrl())) {
            throw new DuplicateKeyException(String.format("Offer with url: %s already exists", requestDto.offerUrl()));
        }

        Offer offer = Offer.builder()
                .id(UUID.randomUUID().toString())
                .company(requestDto.company())
                .position(requestDto.position())
                .salary(requestDto.salary())
                .offerUrl(requestDto.offerUrl())
                .build();

        return repository.save(offer);
    }

    public List<Offer> saveAll(List<OfferRequestDto> requestDtos) {
        List<Offer> filteredOffers = requestDtos.stream()
                .filter(requestDto -> !repository.existsByOfferUrl(requestDto.offerUrl()))
                .map(OfferMapper::toEntity)
                .toList();

        return repository.saveAll(filteredOffers);
    }
}
