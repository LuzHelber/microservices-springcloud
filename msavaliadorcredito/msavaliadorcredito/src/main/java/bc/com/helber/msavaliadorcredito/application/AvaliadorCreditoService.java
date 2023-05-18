package bc.com.helber.msavaliadorcredito.application;

import bc.com.helber.msavaliadorcredito.domain.model.CartaoCliente;
import bc.com.helber.msavaliadorcredito.domain.model.DadosCliente;
import bc.com.helber.msavaliadorcredito.domain.model.SituacaoCliente;
import bc.com.helber.msavaliadorcredito.infra.clients.CartoesResourceClient;
import bc.com.helber.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;


    public SituacaoCliente obterSituacaoCliente(String cpf){
        ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
        ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

        return SituacaoCliente
                .builder()
                .cliente(dadosClienteResponse.getBody())
                .cartoes(cartoesResponse.getBody())
                .build();


    }
}
