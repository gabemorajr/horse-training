package com.gabemorajr.horse_training.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record CreateClientRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Email @Size(max = 255) String email,
        @Size(max = 30) String phone,
        @Size(max = 5000) String notes
) {
}
