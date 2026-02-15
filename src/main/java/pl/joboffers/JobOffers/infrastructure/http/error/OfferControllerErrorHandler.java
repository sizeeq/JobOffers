package pl.joboffers.JobOffers.infrastructure.http.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.joboffers.JobOffers.domain.offer.exception.OfferNotFoundException;

@ControllerAdvice
@Log4j2
public class OfferControllerErrorHandler {

    @ExceptionHandler(OfferNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public OfferControllerErrorResponse handleOfferNotFoundException(OfferNotFoundException exception) {
        String exceptionMessage = exception.getMessage();
        log.error(exceptionMessage);
        return new OfferControllerErrorResponse(exceptionMessage, HttpStatus.NOT_FOUND);
    }
}
