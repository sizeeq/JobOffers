package pl.joboffers.JobOffers.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import pl.joboffers.JobOffers.domain.user.dto.UserDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResultDto;
import pl.joboffers.JobOffers.domain.user.exception.UserAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserFacadeTest {

    private UserRepository repository;
    private UserFacade userFacade;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        userFacade = new UserFacade(repository);
    }

    @Test
    @DisplayName("Should register new user")
    public void should_register_new_user() {
        //given
        String username = "username123";
        UserRegisterRequestDto request = UserRegisterRequestDto.builder()
                .username(username)
                .password("password123")
                .build();

        //when
        UserRegisterResultDto response = userFacade.register(request);

        //then
        assertTrue(response.isCreated());
        assertThat(response.username()).isEqualTo(username);
        assertTrue(repository.existsByUsername(username));
    }

    @Test
    @DisplayName("Should throw exception when user is already registered")
    void should_throw_exception_when_user_already_registered() {
        //given
        String username = "username123";
        User user = User.builder()
                .username(username)
                .password("password123")
                .build();
        repository.save(user);

        UserRegisterRequestDto request = UserRegisterRequestDto.builder()
                .username(username)
                .password("password123")
                .build();

        //when && then
        assertThrows(UserAlreadyExistsException.class, () -> userFacade.register(request));
    }

    @Test
    @DisplayName("Should find existing user by username")
    void should_find_existing_user_by_username() {
        //given
        String username = "username123";
        User user = User.builder()
                .username(username)
                .password("password123")
                .build();
        repository.save(user);

        //when
        UserDto dtoByUsername = userFacade.findByUsername(username);

        //then
        assertThat(dtoByUsername.username()).isEqualTo(username);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void should_throw_exception_when_user_not_found() {
        //given when then
        assertThrows(BadCredentialsException.class, () -> userFacade.findByUsername("notExistingUsername"));
    }
}