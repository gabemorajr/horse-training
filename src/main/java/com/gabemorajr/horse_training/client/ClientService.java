package com.gabemorajr.horse_training.client;

import com.gabemorajr.horse_training.client.dto.CreateClientRequest;
import com.gabemorajr.horse_training.client.dto.ClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> listActiveClients() {
        return clientRepository.findByArchivedAtIsNullOrderByLastNameAscFirstNameAsc()
                .stream()
                .map(ClientResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse getClient(Long id) {
        return ClientResponse.from(findClient(id));
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        Client client = new Client(
                request.firstName().trim(),
                request.lastName().trim(),
                blankToNull(request.email()),
                blankToNull(request.phone()),
                blankToNull(request.notes())
        );
        Client saved = clientRepository.save(client); // Hibernate runs the INSERT
        return ClientResponse.from(saved);
    }

    @Transactional
    public void archiveClient(Long id) {
        Client client = findClient(id);
        client.archive();
    }

    private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client " + id + " not found"));
    }

    // Turns null, "", or "   " into null, and trims everything else.
    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}