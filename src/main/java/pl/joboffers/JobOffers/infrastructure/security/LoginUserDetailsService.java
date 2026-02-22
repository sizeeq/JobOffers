package pl.joboffers.JobOffers.infrastructure.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.joboffers.JobOffers.domain.user.UserFacade;
import pl.joboffers.JobOffers.domain.user.dto.UserDto;

import java.util.Collections;

public class LoginUserDetailsService implements UserDetailsService {

    private final UserFacade userFacade;

    public LoginUserDetailsService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        UserDto userDto = userFacade.findByUsername(username);
        return getUser(userDto);
    }

    private User getUser(UserDto userDto) {
        return new User(
                userDto.username(),
                userDto.password(),
                Collections.emptyList()
        );
    }
}
