package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
        String id,
        String username,
        String password
) {
}
