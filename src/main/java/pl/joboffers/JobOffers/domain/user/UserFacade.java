package pl.joboffers.JobOffers.domain.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import pl.joboffers.JobOffers.domain.user.dto.UserDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResultDto;
import pl.joboffers.JobOffers.domain.user.exception.UserAlreadyExistsException;

@Component
public class UserFacade {

    private final UserRepository repository;

    public UserFacade(UserRepository repository) {
        this.repository = repository;
    }

    public UserRegisterResultDto register(UserRegisterRequestDto requestDto) {
        if (repository.existsByUsername(requestDto.username())) {
            throw new UserAlreadyExistsException("User is already registered");
        }

        User user = User.builder()
                .username(requestDto.username())
                .password(requestDto.password())
                .build();

        User savedUser = repository.save(user);

        return UserRegisterResultDto.builder()
                .id(savedUser.id())
                .username(savedUser.username())
                .isCreated(true)
                .build();
    }

    public UserDto findByUsername(String username) {
        return repository.findByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new BadCredentialsException("User was not found"));
    }
}