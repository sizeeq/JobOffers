package pl.joboffers.JobOffers.infrastructure.http;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<OfferDto> saveOffer(@RequestBody @Valid OfferRequestDto offerRequestDto) {
        OfferDto offerDto = offerFacade.save(offerRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(offerDto.id())
                .toUri();

        return ResponseEntity.created(location).body(offerDto);
    }
}
