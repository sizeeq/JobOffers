package pl.joboffers.JobOffers.domain.offer.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OfferRequestDto(
        @NotNull(message = "{company.not.null.message}")
        @NotEmpty(message = "{company.not.empty.message}")
        @Size(min = 1, max = 50, message = "{company.validation.size.message}")
        String company,

        @NotNull(message = "{position.not.null.message}")
        @NotEmpty(message = "{position.not.empty.message}")
        @Size(min = 1, max = 100, message = "{position.validation.size.message}")
        String position,

        @NotNull(message = "{salary.not.null.message}")
        @NotEmpty(message = "{salary.not.empty.message}")
        String salary,

        @NotNull(message = "{offerUrl.not.null.message}")
        @NotEmpty(message = "{offerUrl.not.empty.message}")
        String offerUrl
) {
}
