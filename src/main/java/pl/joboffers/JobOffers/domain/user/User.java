package pl.joboffers.JobOffers.domain.user;

import lombok.Builder;

@Builder
public record User(
        String id,
        String username,
        String password
) {
}
