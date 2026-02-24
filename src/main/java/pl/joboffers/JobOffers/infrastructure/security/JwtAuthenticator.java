package pl.joboffers.JobOffers.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.JwtResponseDto;
import pl.joboffers.JobOffers.infrastructure.security.controller.dto.TokenRequestDto;

import java.time.*;
import java.util.List;

@Component
public class JwtAuthenticator {

    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final JwtConfigurationProperties properties;

    public JwtAuthenticator(AuthenticationManager authenticationManager, Clock clock, JwtConfigurationProperties properties) {
        this.authenticationManager = authenticationManager;
        this.clock = clock;
        this.properties = properties;
    }

    public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = createToken(userDetails);
        String username = userDetails.getUsername();

        return JwtResponseDto.builder()
                .token(token)
                .username(username)
                .build();
    }

    private String createToken(UserDetails user) {
        String secretKey = properties.getSecret();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = now.plus(Duration.ofDays(properties.getExpirationDays()));

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(properties.getIssuer())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("roles", roles)
                .sign(algorithm);
    }
}
