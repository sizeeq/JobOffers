package pl.joboffers.JobOffers.infrastructure.offer.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;

import java.util.List;

@Component
@Log4j2
public class OfferScheduler {

    private final OfferFacade offerFacade;

    public OfferScheduler(OfferFacade offerFacade) {
        this.offerFacade = offerFacade;
    }

    @Scheduled(fixedDelayString = "${offers.http.client.config.fixedDelay}")
    public List<OfferDto> fetchAndSaveNewOffers() {
        log.info("Starting fetching and saving new offers...");
        List<OfferDto> offerDtos = offerFacade.fetchAndSaveNewOffers();
        log.info("Successfully fetched {} offers", offerDtos.size());
        return offerDtos;
    }
}
