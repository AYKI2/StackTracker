package stocktracker.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest (
    @NotBlank
    String refreshToken
){
}
