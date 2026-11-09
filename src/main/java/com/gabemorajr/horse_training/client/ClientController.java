package com.gabemorajr.horse_training.client;

import com.gabemorajr.horse_training.client.dto.CreateClientRequest;
import com.gabemorajr.horse_training.client.dto.ClientResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponse> listClients() {
        return clientService.listActiveClients();
    }

    @GetMapping("/{id}")
    public ClientResponse getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created instead of 200 OK
    public ClientResponse createClient(@Valid @RequestBody CreateClientRequest request) {
        return clientService.createClient(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: success, nothing to return
    public void archiveClient(@PathVariable Long id) {
        clientService.archiveClient(id);
    }
}
