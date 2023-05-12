package br.com.helber.msclientes.infra.repository;

import br.com.helber.msclientes.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository <Cliente, Long> {
}
