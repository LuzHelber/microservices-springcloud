package br.com.helber.mscartoes.application;

import br.com.helber.mscartoes.application.representation.CartoesPorClienteResponse;
import br.com.helber.mscartoes.application.representation.ErrorResponse;
import br.com.helber.mscartoes.domain.Cartao;
import br.com.helber.mscartoes.domain.ClienteCartao;
import br.com.helber.mscartoes.representation.CartaoSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
public class CartoesResource {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    @Operation(
            summary = "Obter todos os cartões",
            description = "Retorna uma lista contendo todos os cartões cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de cartões encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))
    )
    public String status() {
        return "ok";
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo cartão",
            description = "Cadastra um novo cartão com base nos dados fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ErrorResponse> cadastra(@RequestBody CartaoSaveRequest request) {
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);

        ErrorResponse response = new ErrorResponse("Cartão cadastrado com sucesso!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(params = "renda")
    @Operation(
            summary = "Obter cartões por renda",
            description = "Retorna uma lista de cartões cuja renda é menor ou igual ao valor fornecido."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de cartões encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))
    )
    public ResponseEntity<List<Cartao>> getCartoesRendaAteh(
            @Parameter(description = "Valor máximo da renda") @RequestParam("renda") Long renda){
        List<Cartao> list = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "cpf")
    @Operation(
            summary = "Obter cartões por CPF",
            description = "Retorna uma lista de cartões associados a um cliente com base no CPF fornecido."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de cartões encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartoesPorClienteResponse.class))
    )
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(
            @Parameter(description = "CPF do cliente") @RequestParam("cpf") String cpf) {
        List<ClienteCartao> lista = clienteCartaoService.listCartoesByCpf(cpf);
        List<CartoesPorClienteResponse> resultList = lista.stream()
                .map(CartoesPorClienteResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultList);
    }
}
