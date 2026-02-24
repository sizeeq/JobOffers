package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;
import pl.joboffers.JobOffers.domain.user.UserRole;

import java.util.Set;

@Builder
public record UserDto(
        String id,
        String username,
        String password,
        Set<UserRole> roles
) {
}
