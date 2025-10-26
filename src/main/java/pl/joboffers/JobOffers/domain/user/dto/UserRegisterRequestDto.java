package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;

@Builder
public record UserRegisterRequestDto(
        String username,
        String password
) {
}
