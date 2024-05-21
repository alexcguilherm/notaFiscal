package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.exception.EstoqueException;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EstoqueService {
    public void enviarNotaFiscalParaBaixaEstoque(NotaFiscal notaFiscal) {
        try {
            log.info("Enviando nota fiscal para baixa de estoque {}", notaFiscal.getIdNotaFiscal());
            Thread.sleep(380);
            log.info("Nota fiscal enviada para baixa de estoque com sucesso {}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            String mensagemErro = "Erro ao enviar nota fiscal para baixa de estoque  " + notaFiscal.getIdNotaFiscal();
            log.error(mensagemErro, e);
            throw new EstoqueException(mensagemErro, e);
        }
    }
}
