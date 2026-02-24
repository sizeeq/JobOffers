package pl.joboffers.JobOffers.domain.offer;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class InMemoryOfferRepository implements OfferRepository {

    private final Map<String, Offer> offers = new ConcurrentHashMap<>();

    @Override
    public Offer save(Offer offer) {
        String id = offer.id() == null ? UUID.randomUUID().toString() : offer.id();

        Offer savedOffer = new Offer(
                id,
                offer.company(),
                offer.position(),
                offer.salary(),
                offer.offerUrl()
        );

        offers.put(id, savedOffer);
        return savedOffer;
    }

    @Override
    public <S extends Offer> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::save)
                .map(entity -> (S) entity)
                .toList();
    }

    @Override
    public Optional<Offer> findById(String id) {
        return Optional.ofNullable(offers.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return offers.containsKey(id);
    }

    @Override
    public List<Offer> findAll() {
        return new ArrayList<>(offers.values());
    }

    @Override
    public List<Offer> findAllById(Iterable<String> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(offers::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public long count() {
        return offers.size();
    }

    @Override
    public void deleteById(String id) {
        offers.remove(id);
    }

    @Override
    public void delete(Offer entity) {
        offers.remove(entity.id());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(offers::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends Offer> entities) {
        entities.forEach(entity -> offers.remove(entity.id()));
    }

    @Override
    public void deleteAll() {
        offers.clear();
    }

    @Override
    public boolean existsByOfferUrl(String url) {
        return offers.values().stream()
                .anyMatch(offer -> offer.offerUrl().equals(url));
    }

    @Override
    public <S extends Offer> S insert(S entity) {
        return (S) save(entity);
    }

    @Override
    public <S extends Offer> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public <S extends Offer> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Example queries are not supported in memory");
    }

    @Override
    public <S extends Offer> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Example queries are not supported in memory");
    }

    @Override
    public Page<Offer> findAll(Pageable pageable) {
        List<Offer> allOffers = findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allOffers.size());
        return new org.springframework.data.domain.PageImpl<>(allOffers.subList(start, end), pageable, allOffers.size());
    }

    @Override
    public <S extends Offer> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Offer> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Offer> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Offer> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Offer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Offer> findAll(Sort sort) {
        return List.of();
    }
}