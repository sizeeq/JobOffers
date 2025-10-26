package pl.joboffers.JobOffers.domain.user;

import pl.joboffers.JobOffers.domain.user.dto.UserDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResponseDto;
import pl.joboffers.JobOffers.domain.user.exception.UserAlreadyExistsException;
import pl.joboffers.JobOffers.domain.user.exception.UserNotFoundException;

public class UserFacade {

    private final UserRepository repository;

    public UserFacade(UserRepository repository) {
        this.repository = repository;
    }

    public UserRegisterResponseDto register(UserRegisterRequestDto requestDto) {
        if (repository.existsByUsername(requestDto.username())) {
            throw new UserAlreadyExistsException("User is already registered");
        }

        User user = User.builder()
                .username(requestDto.username())
                .password(requestDto.password())
                .build();

        User savedUser = repository.save(user);

        return UserRegisterResponseDto.builder()
                .username(savedUser.username())
                .isCreated(true)
                .build();
    }

    public UserDto findByUsername(String username) {
        return repository.findByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User was not found"));
    }

}
