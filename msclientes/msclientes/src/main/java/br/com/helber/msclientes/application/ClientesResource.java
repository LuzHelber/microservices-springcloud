package br.com.helber.msclientes.application;

import br.com.helber.msclientes.application.representation.ClienteSaveRequest;
import br.com.helber.msclientes.domain.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesResource {

    private final ClienteService service;

    @GetMapping
    public String status(){
        log.info("Obtendo o status do microservice de clientes");
        return "ok";
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody ClienteSaveRequest request) {
        var cliente = request.toModel();
        service.save(cliente);
        URI headerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();
        return ResponseEntity
                .created(headerLocation)
                .body("Cliente cadastrado com sucesso!");
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<?> dadosCliente(@RequestParam("cpf") String cpf) {
        Optional<Cliente> clienteOptional = service.getByCPF(cpf);
        if (clienteOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        Cliente cliente = clienteOptional.get();
        return ResponseEntity.ok(cliente);
    }

}
