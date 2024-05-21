package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.exception.FinanceiroException;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class FinanceiroService {
    public void enviarNotaFiscalParaContasReceber(NotaFiscal notaFiscal) {

        try {
            //Simula o envio da nota fiscal para o contas a receber
            log.info("Enviando nota fiscal ao contas a receber {}", notaFiscal.getIdNotaFiscal());
            Thread.sleep(250);
            log.info("Nota fiscal enviada ao contas a pagar com sucesso {}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            String mensagemErro = "Erro ao enviar nota fiscal ao contas a receber " + notaFiscal.getIdNotaFiscal();
            log.error(mensagemErro, e);
            throw new FinanceiroException(mensagemErro, e);

        }
    }
}
