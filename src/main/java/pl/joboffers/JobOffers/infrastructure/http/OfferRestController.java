package pl.joboffers.JobOffers.infrastructure.http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferRestController {

    private final OfferFacade offerFacade;

    public OfferRestController(OfferFacade offerFacade) {
        this.offerFacade = offerFacade;
    }

    @GetMapping()
    public ResponseEntity<List<OfferDto>> fetchOffers() {
        List<OfferDto> allOffers = offerFacade.findAllOffers();
        return ResponseEntity.ok(allOffers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> fetchOfferById(@PathVariable String id) {
        OfferDto offerById = offerFacade.findById(id);
        return ResponseEntity.ok(offerById);
    }
}
