package pl.joboffers.JobOffers.domain.offer;

import java.util.List;
import java.util.Optional;

public interface OfferRepository {

    Offer save(Offer offer);

    List<Offer> saveAll(List<Offer> offers);

    List<Offer> findAll();

    Optional<Offer> findById(String id);

    boolean existsByOfferUrl(String url);
}
