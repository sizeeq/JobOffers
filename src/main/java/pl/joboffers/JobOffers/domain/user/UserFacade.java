package pl.joboffers.JobOffers.domain.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.joboffers.JobOffers.domain.user.dto.UserDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResultDto;
import pl.joboffers.JobOffers.domain.user.exception.UserAlreadyExistsException;

import java.util.Set;

@Component
public class UserFacade {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserFacade(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResultDto register(UserRegisterRequestDto requestDto) {
        if (repository.existsByUsername(requestDto.username())) {
            throw new UserAlreadyExistsException("User is already registered");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User user = User.builder()
                .username(requestDto.username())
                .password(encodedPassword)
                .roles(Set.of(UserRole.USER))
                .build();

        User savedUser = repository.save(user);

        return UserRegisterResultDto.builder()
                .id(savedUser.id())
                .username(savedUser.username())
                .isCreated(true)
                .roles(savedUser.roles())
                .build();
    }

    public UserDto findByUsername(String username) {
        return repository.findByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new BadCredentialsException("User was not found"));
    }
}