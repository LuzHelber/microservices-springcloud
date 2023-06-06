package br.com.helber.msclientes.application;

import br.com.helber.msclientes.application.representation.ClienteSaveRequest;
import br.com.helber.msclientes.domain.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
@Slf4j
@Schema
public class ClientesResource {

    private final ClienteService service;

    @GetMapping
    @Operation(
            summary = "Obter lista de clientes",
            description = "Retorna uma lista contendo todos os clientes cadastrados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes encontrada",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"cpf\": \"***456789\",\n    \"nome\": \"Marcos da Silva\",\n    \"idade\": 30\n  },\n  {\n    \"id\": 2,\n    \"cpf\": \"***654321\",\n    \"nome\": \"Silvio Santos\",\n    \"idade\": 25\n  }\n]")
                    })
            ),
            @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"Nenhum cliente cadastrado\"}")
                    })
            )
    })
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = service.getAllClientes();
        if (clientes.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }
        return ResponseEntity.ok(clientes);
    }
    @PostMapping
    @Operation(
            summary = "Cadastrar um novo cliente",
            description = "Cadastra um novo cliente com base nos dados fornecidos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteSaveRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de solicitação",
                                    value = "{\"cpf\": \"***456789\", \"nome\": \"Marcos da Silva\", \"idade\": 30}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente cadastrado com sucesso"
                    )
            }
    )
    public ResponseEntity<?> save(@RequestBody ClienteSaveRequest request) {
        var cliente = request.toModel();
        log.info("Cliente recebido para salvar: id={}, cpf={}, nome={}, idade={}",
                cliente.getId(),
                maskCpf(cliente.getCpf()), // Mascarar o CPF antes de registrar no log
                cliente.getNome(),
                cliente.getIdade()
        );
        service.save(cliente);
        log.info("Cliente salvo: id={}, cpf={}, nome={}, idade={}",
                cliente.getId(),
                maskCpf(cliente.getCpf()), // Mascarar o CPF antes de registrar no log
                cliente.getNome(),
                cliente.getIdade()
        );
        URI headerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(maskCpf(cliente.getCpf())) // Mascarar o CPF antes de construir o headerLocation
                .toUri();

        return ResponseEntity
                .created(headerLocation)
                .body(new ErrorResponse("Cliente cadastrado com sucesso!"));
    }


    @GetMapping(params = "cpf")
    @Operation(
            summary = "Obter dados de um cliente",
            description = "Retorna os dados de um cliente com base no CPF fornecido.",
            parameters = {
                    @Parameter(
                            name = "cpf",
                            description = "CPF do cliente",
                            required = true,
                            example = "***456789"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Cliente.class),
                                    examples = @ExampleObject(
                                            name = "Exemplo de resposta",
                                            value = "{\"id\": 1, \"cpf\": \"***456789\", \"nome\": \"Marcos da Silva\", \"idade\": 30}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado"
                    )
            }
    )
    public ResponseEntity<?> dadosCliente(@RequestParam("cpf") String cpf) {
        Optional<Cliente> clienteOptional = service.getByCPF(cpf);
        if (clienteOptional.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        Cliente cliente = clienteOptional.get();
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados de um cliente",
            description = "Atualiza os dados de um cliente com base no ID fornecido.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID do cliente",
                            required = true,
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteSaveRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de solicitação",
                                    value = "{\"cpf\": \"***456789\", \"nome\": \"Marcos da Silva\", \"idade\": 30}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente atualizado com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado"
                    )
            }
    )
    public ResponseEntity<SuccessResponse> updateCliente(
            @PathVariable("id") Long id,
            @RequestBody ClienteSaveRequest request
    ) {
        Optional<Cliente> clienteOptional = service.getById(id);
        if (clienteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cliente cliente = clienteOptional.get();
        cliente.setCpf(request.getCpf());
        cliente.setNome(request.getNome());
        cliente.setIdade(request.getIdade());

        service.save(cliente);

        SuccessResponse response = new SuccessResponse("Cliente atualizado com sucesso");
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir um cliente",
            description = "Exclui um cliente com base no ID fornecido.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID do cliente",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente excluído com sucesso",
                            content = @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado"
                    )
            }
    )
    public ResponseEntity<SuccessResponse> deleteCliente(@PathVariable("id") Long id) {
        Optional<Cliente> clienteOptional = service.getById(id);
        if (clienteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(id);

        SuccessResponse response = new SuccessResponse("Cliente excluído com sucesso");
        return ResponseEntity.ok(response);
    }


    // Método para mascarar o CPF
    private String maskCpf(String cpf) {
        if (cpf.length() != 11) {
            return cpf;
        }
        return "***" + cpf.substring(3);
    }
}
