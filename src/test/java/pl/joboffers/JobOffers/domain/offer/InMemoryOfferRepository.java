package pl.joboffers.JobOffers.domain.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOfferRepository implements OfferRepository{

    private final Map<String, Offer> offers = new ConcurrentHashMap<>();

    @Override
    public Offer save(Offer offer) {
        offers.put(offer.id(), offer);
        return offer;
    }

    @Override
    public List<Offer> saveAll(List<Offer> offers) {
        return offers.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public List<Offer> findAll() {
        return new ArrayList<>(offers.values());
    }

    @Override
    public Optional<Offer> findById(String id) {
        return Optional.ofNullable(offers.get(id));
    }

    @Override
    public boolean existsByOfferUrl(String url) {
        return offers.values().stream()
                .anyMatch(offer -> offer.offerUrl().equals(url));
    }
}
