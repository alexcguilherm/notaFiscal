package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.exception.RegistroException;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RegistroService {
    public void registrarNotaFiscal(NotaFiscal notaFiscal) {

        try {
            //Simula o registro da nota fiscal
            log.info("Registrando nota fiscal");
            Thread.sleep(500);
            log.info("Nota fiscal registrada com sucesso");
        } catch (InterruptedException e) {
            String mensagemErro = "Erro inesperado ao registrar nota fiscal";
            log.error(mensagemErro, e);
            throw new RegistroException(mensagemErro, e);
        }
    }
}
