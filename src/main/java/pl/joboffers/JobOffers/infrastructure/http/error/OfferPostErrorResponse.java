package pl.joboffers.JobOffers.infrastructure.http.error;

import org.springframework.http.HttpStatus;

public record OfferPostErrorResponse(
        String message,
        HttpStatus httpStatus
) {
}
