package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;

@Builder
public record UserRegisterResultDto(
        String id,
        String username,
        boolean isCreated
) {
}
