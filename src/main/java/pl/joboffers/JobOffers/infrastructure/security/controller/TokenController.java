package pl.joboffers.JobOffers.infrastructure.security.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.joboffers.JobOffers.infrastructure.security.JwtAuthenticator;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.JwtResponseDto;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.TokenRequestDto;

@RestController
public class TokenController {

    private final JwtAuthenticator jwtAuthenticator;

    public TokenController(JwtAuthenticator jwtAuthenticator) {
        this.jwtAuthenticator = jwtAuthenticator;
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@Valid @RequestBody TokenRequestDto requestDto) {
        JwtResponseDto jwtResponseDto = jwtAuthenticator.authenticateAndGenerateToken(requestDto);
        return ResponseEntity.ok(jwtResponseDto);
    }
}
