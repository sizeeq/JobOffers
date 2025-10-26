package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;

@Builder
public record UserRegisterResponseDto(
        String id,
        String username,
        boolean isCreated
) {
}
