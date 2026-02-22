package pl.joboffers.JobOffers.infrastructure.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.joboffers.JobOffers.domain.user.UserFacade;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResultDto;

import java.net.URI;

@RestController
public class RegisterController {

    private final UserFacade userFacade;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserFacade userFacade, PasswordEncoder passwordEncoder) {
        this.userFacade = userFacade;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResultDto> registerUser(@RequestBody UserRegisterRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.password());

        UserRegisterResultDto registerResultDto = userFacade.register(
                new UserRegisterRequestDto(
                        requestDto.username(),
                        encodedPassword
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registerResultDto.id())
                .toUri();

        return ResponseEntity.created(location).body(registerResultDto);
    }
}
