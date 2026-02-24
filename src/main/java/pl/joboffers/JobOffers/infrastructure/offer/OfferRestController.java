package pl.joboffers.JobOffers.infrastructure.offer;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.joboffers.JobOffers.domain.offer.OfferFacade;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferUpdateRequestDto;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferRestController {

    private final OfferFacade offerFacade;

    public OfferRestController(OfferFacade offerFacade) {
        this.offerFacade = offerFacade;
    }

    @GetMapping
    public ResponseEntity<Page<OfferDto>> getOffers(Pageable pageable) {
        Page<OfferDto> allOffers = offerFacade.findAllOffers(pageable);
        return ResponseEntity.ok(allOffers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable String id) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id) {
        offerFacade.deleteOfferById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable String id, @RequestBody @Valid OfferUpdateRequestDto updateRequestDto) {
        OfferDto updatedOfferDto = offerFacade.updateOfferById(id, updateRequestDto);
        return ResponseEntity.ok(updatedOfferDto);
    }
}
