package pl.joboffers.JobOffers.infrastructure.http.error;

import org.springframework.http.HttpStatus;

public record OfferControllerErrorResponse(
        String message,
        HttpStatus httpStatus
) {
}
