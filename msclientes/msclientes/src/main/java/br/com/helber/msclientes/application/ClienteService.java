package br.com.helber.msclientes.application;

import br.com.helber.msclientes.domain.Cliente;
import br.com.helber.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional
    public Cliente save(Cliente cliente){
        log.info("Antes de salvar o cliente: {}", cliente);
        Cliente savedCliente = repository.save(cliente);
        log.info("Cliente salvo: {}", savedCliente);
        return savedCliente;
    }

    public Optional<Cliente> getByCPF(String cpf){
        return repository.findByCpf(cpf);
    }

    public Optional<Cliente> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Cliente> getAllClientes() {
        return repository.findAll();
    }
}
