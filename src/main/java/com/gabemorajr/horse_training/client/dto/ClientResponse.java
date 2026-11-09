package com.gabemorajr.horse_training.client.dto;

import com.gabemorajr.horse_training.client.Client;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String notes
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getPhone(),
                client.getNotes()
        );
    }
}
