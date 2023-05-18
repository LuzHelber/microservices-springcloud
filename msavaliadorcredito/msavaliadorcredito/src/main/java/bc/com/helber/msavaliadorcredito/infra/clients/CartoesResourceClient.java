package bc.com.helber.msavaliadorcredito.infra.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesResourceClient {
}
