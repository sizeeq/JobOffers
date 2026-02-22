package pl.joboffers.JobOffers.infrastructure.offer.error;

import org.springframework.http.HttpStatus;

public record OfferControllerErrorResponse(
        String message,
        HttpStatus httpStatus
) {
}
