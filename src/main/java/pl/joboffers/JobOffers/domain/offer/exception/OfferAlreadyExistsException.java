package pl.joboffers.JobOffers.domain.offer.exception;

public class OfferAlreadyExistsException extends RuntimeException {

    public OfferAlreadyExistsException(String message) {
        super(message);
    }
}
