package pl.joboffers.JobOffers.domain.user;

import pl.joboffers.JobOffers.domain.user.dto.UserDto;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.id())
                .username(user.username())
                .password(user.password())
                .build();
    }
}
