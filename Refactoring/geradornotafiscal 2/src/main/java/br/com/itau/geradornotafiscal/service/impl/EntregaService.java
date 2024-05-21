package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.exception.EntregaException;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EntregaService {
    private final EntregaIntegrationPort entregaIntegrationPort;

    @Autowired
    public EntregaService(EntregaIntegrationPort entregaIntegrationPort) {
        this.entregaIntegrationPort = entregaIntegrationPort;
    }

    public void agendarEntrega(NotaFiscal notaFiscal) {
        try {
            log.info("Iniciando agendamento de entrega para a nota fiscal {}", notaFiscal.getIdNotaFiscal());
            realizarAgendamentoEntrega(notaFiscal);
            log.info("Agendamento de entrega concluído para a nota fiscal {}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            String mensagemErro = "Erro durante o agendamento de entrega para a nota fiscal " + notaFiscal.getIdNotaFiscal();
            log.error(mensagemErro, e);
            throw new EntregaException(mensagemErro, e);
        }
    }

    private void realizarAgendamentoEntrega(NotaFiscal notaFiscal) throws InterruptedException {
        // Simula o agendamento da entrega
        Thread.sleep(150);
        entregaIntegrationPort.criarAgendamentoEntrega(notaFiscal);
    }
}
