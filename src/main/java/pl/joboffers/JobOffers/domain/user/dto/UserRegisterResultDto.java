package pl.joboffers.JobOffers.domain.user.dto;

import lombok.Builder;
import pl.joboffers.JobOffers.domain.user.UserRole;

import java.util.Set;

@Builder
public record UserRegisterResultDto(
        String id,
        String username,
        boolean isCreated,
        Set<UserRole> roles
) {
}
