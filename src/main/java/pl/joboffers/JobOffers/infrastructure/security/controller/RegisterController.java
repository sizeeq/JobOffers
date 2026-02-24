package pl.joboffers.JobOffers.infrastructure.security.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.joboffers.JobOffers.domain.user.UserFacade;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterRequestDto;
import pl.joboffers.JobOffers.domain.user.dto.UserRegisterResultDto;

import java.net.URI;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserFacade userFacade;


    public RegisterController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping()
    public ResponseEntity<UserRegisterResultDto> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
        UserRegisterResultDto registerResultDto = userFacade.register(
                new UserRegisterRequestDto(
                        requestDto.username(),
                        requestDto.password()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registerResultDto.id())
                .toUri();

        return ResponseEntity.created(location).body(registerResultDto);
    }
}
