package br.com.helber.msclientes.infra.repository;

import br.com.helber.msclientes.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository <Cliente, Long> {
    Optional<Cliente> finByCpf(String cpf);
}
