package pl.joboffers.JobOffers.infrastructure.offer.error;

import org.springframework.http.HttpStatus;

public record OfferPostErrorResponse(
        String message,
        HttpStatus httpStatus
) {
}
