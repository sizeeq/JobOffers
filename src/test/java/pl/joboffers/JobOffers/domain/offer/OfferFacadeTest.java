package pl.joboffers.JobOffers.domain.offer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.joboffers.JobOffers.domain.offer.dto.OfferDto;
import pl.joboffers.JobOffers.domain.offer.dto.OfferRequestDto;
import pl.joboffers.JobOffers.domain.offer.exception.OfferAlreadyExistsException;
import pl.joboffers.JobOffers.domain.offer.exception.OfferNotFoundException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferFacadeTest {

    private OfferFacade offerFacade;
    private OfferRepository offerRepository;

    @Mock
    private OfferFetcher offerFetcher;

    private final List<OfferDto> sampleOfferDtos = List.of(
            OfferDto.builder()
                    .id("1000")
                    .position("Junior Java Developer")
                    .company("CodeCraft")
                    .salary("8k–12k PLN")
                    .offerUrl("https://jobs.example.com/java-junior")
                    .build(),

            OfferDto.builder()
                    .id("2000")
                    .position("Backend Engineer (Java/Kotlin)")
                    .company("SoftFlow")
                    .salary("10k–15k PLN")
                    .offerUrl("https://jobs.example.com/backend-engineer")
                    .build(),

            OfferDto.builder()
                    .id("3000")
                    .position("Java Developer")
                    .company("NextGen Systems")
                    .salary("12k–18k PLN")
                    .offerUrl("https://jobs.example.com/java-developer")
                    .build()
    );

    private final List<Offer> sampleOffers = List.of(
            Offer.builder()
                    .id("1000")
                    .position("Junior Java Developer")
                    .company("CodeCraft")
                    .salary("8k–12k PLN")
                    .offerUrl("https://jobs.example.com/java-junior")
                    .build(),

            Offer.builder()
                    .id("2000")
                    .position("Backend Engineer (Java/Kotlin)")
                    .company("SoftFlow")
                    .salary("10k–15k PLN")
                    .offerUrl("https://jobs.example.com/backend-engineer")
                    .build()
    );


    @BeforeEach
    public void setUp() {
        offerRepository = new InMemoryOfferRepository();
        offerFacade = new OfferFacade(offerRepository, offerFetcher);
    }

    @Test
    @DisplayName("Should fetch and save offer when repository is empty")
    void should_fetch_and_save_new_offers_when_repository_is_empty() {
        // given
        when(offerFetcher.fetchOffers()).thenReturn(sampleOfferDtos);

        //when
        List<OfferDto> savedOffers = offerFacade.fetchAndSaveNewOffers();

        //then
        assertThat(savedOffers.size()).isEqualTo(3);
        assertThat(savedOffers).containsExactlyInAnyOrder(sampleOfferDtos.get(0), sampleOfferDtos.get(1), sampleOfferDtos.get(2));
        assertThat(offerRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should not duplicate offers when fetched offers already exist in repository")
    void should_not_duplicate_offers_when_fetched_offers_already_exist_in_repository() {
        // given
        offerRepository.saveAll(sampleOffers);
        when(offerFetcher.fetchOffers()).thenReturn(sampleOfferDtos);

        //when
        List<OfferDto> savedOffers = offerFacade.fetchAndSaveNewOffers();

        //then
        assertThat(offerRepository.findAll().size()).isEqualTo(3);
        assertThat(savedOffers.size()).isEqualTo(1);
        assertThat(savedOffers).containsExactly(sampleOfferDtos.get(2));
    }

    @Test
    @DisplayName("Should find offer by id when offer exists")
    void should_find_offer_by_id_when_offer_exists() {
        // given
        String id = "1000";
        offerRepository.saveAll(sampleOffers);

        //when
        OfferDto result = offerFacade.findById(id);

        //then
        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should throw exception when offer not found by id")
    void should_throw_exception_when_offer_not_found_by_id() {
        // given + when + then
        assertThrows(OfferNotFoundException.class, () -> offerFacade.findById("nonExistingId"));
    }

    @Test
    @DisplayName("Should return all offers when repository contains offers")
    void should_return_all_offers_when_repository_contains_offers() {
        // given
        offerRepository.saveAll(sampleOffers);

        //when
        List<OfferDto> offers = offerFacade.findAllOffers();

        //then
        assertThat(offers.size()).isEqualTo(2);
        assertThat(offers).containsExactlyInAnyOrder(sampleOfferDtos.get(0), sampleOfferDtos.get(1));
    }

    @Test
    @DisplayName("Should save new offer from request dto")
    void should_save_new_offer_from_request_dto() {
        //given
        OfferRequestDto request = OfferRequestDto.builder()
                .position("Junior Java Developer")
                .company("CodeCraft")
                .salary("8k–12k PLN")
                .offerUrl("https://jobs.example.com/java-junior")
                .build();

        //when
        offerFacade.save(request);

        //then
        assertThat(offerRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception when trying to save offer with url that already exists")
    void should_throw_exception_when_trying_to_save_offer_with_url_that_already_exists() {
        //given
        offerRepository.saveAll(sampleOffers);

        OfferRequestDto request = OfferRequestDto.builder()
                .position("Junior Java Developer")
                .company("CodeCraft")
                .salary("8k–12k PLN")
                .offerUrl("https://jobs.example.com/java-junior")
                .build();

        //when && then
        assertThrows(OfferAlreadyExistsException.class, () -> offerFacade.save(request));
    }


    @Test
    @DisplayName("Should return empty list when fetcher return no new offers")
    void should_return_empty_list_when_fetcher_returns_no_new_offers() {
        //given
        when(offerFetcher.fetchOffers()).thenReturn(Collections.emptyList());

        //then
        List<OfferDto> result = offerFacade.fetchAndSaveNewOffers();

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw offerNotFoundException when trying to delete not existing offer")
    void should_throw_offer_not_found_exception_when_trying_to_delete_not_existing_offer() {
        //given
        String notExistingId = "notExistingId";

        //when
        //then
        assertThrows(OfferNotFoundException.class, () -> offerFacade.deleteOfferById(notExistingId));
    }

    @Test
    @DisplayName("Should delete offer from database")
    void should_delete_offer_from_database() {
        //given
        offerRepository.saveAll(sampleOffers);
        assertAll(() -> {
                    assertThat(offerRepository.findAll()).hasSize(2);
                    assertThat(offerRepository.existsById("1000")).isTrue();
                    assertThat(offerRepository.existsById("2000")).isTrue();
                }
        );

        //when
        offerFacade.deleteOfferById("1000");

        //then
        assertAll(() -> {
                    assertThat(offerRepository.findAll()).hasSize(1);
                    assertThat(offerRepository.existsById("1000")).isFalse();
                    assertThat(offerRepository.existsById("2000")).isTrue();
                }
        );
    }
}