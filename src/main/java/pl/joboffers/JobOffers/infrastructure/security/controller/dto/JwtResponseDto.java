package pl.joboffers.JobOffers.infrastructure.security.controller.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String username,
        String token
) {
}
