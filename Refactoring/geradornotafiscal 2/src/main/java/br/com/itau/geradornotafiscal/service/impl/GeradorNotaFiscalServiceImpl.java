package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.GeradorItensNotaFiscal;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService {

    private static final double ALIQUOTA_PADRAO = 0;
    private static final double ALIQUOTA_FISICA_ATE_500 = 0.12;
    private static final double ALIQUOTA_FISICA_ATE_2000 = 0.15;
    private static final double ALIQUOTA_FISICA_ACIMA_2000 = 0.17;

    private static final double ALIQUOTA_SIMPLES_ATE_1000 = 0.03;
    private static final double ALIQUOTA_SIMPLES_ATE_2000 = 0.07;
    private static final double ALIQUOTA_SIMPLES_ATE_5000 = 0.13;
    private static final double ALIQUOTA_SIMPLES_ACIMA_5000 = 0.19;

    private static final double ALIQUOTA_LUCRO_REAL_ATE_1000 = 0.03;
    private static final double ALIQUOTA_LUCRO_REAL_ATE_2000 = 0.09;
    private static final double ALIQUOTA_LUCRO_REAL_ATE_5000 = 0.15;
    private static final double ALIQUOTA_LUCRO_REAL_ACIMA_5000 = 0.20;

    private static final double ALIQUOTA_LUCRO_PRESUMIDO_ATE_1000 = 0.03;
    private static final double ALIQUOTA_LUCRO_PRESUMIDO_ATE_2000 = 0.09;
    private static final double ALIQUOTA_LUCRO_PRESUMIDO_ATE_5000 = 0.16;
    private static final double ALIQUOTA_LUCRO_PRESUMIDO_ACIMA_5000 = 0.20;

    private final GeradorItensNotaFiscal geradorItensNotaFiscal;
    private final EstoqueService estoqueService;
    private final RegistroService registroService;
    private final EntregaService entregaService;
    private final FinanceiroService financeiroService;

    @Autowired
    public GeradorNotaFiscalServiceImpl(GeradorItensNotaFiscal geradorItensNotaFiscal,
                                        EstoqueService estoqueService,
                                        RegistroService registroService,
                                        EntregaService entregaService,
                                        FinanceiroService financeiroService) {
        this.geradorItensNotaFiscal = geradorItensNotaFiscal;
        this.estoqueService = estoqueService;
        this.registroService = registroService;
        this.entregaService = entregaService;
        this.financeiroService = financeiroService;
    }

    @Override
    public NotaFiscal gerarNotaFiscal(Pedido pedido) {
        Destinatario destinatario = pedido.getDestinatario();
        TipoPessoa tipoPessoa = destinatario.getTipoPessoa();
        List<ItemNotaFiscal> itemNotaFiscalList = new ArrayList<>();

        double aliquota = ALIQUOTA_PADRAO;

        if (tipoPessoa == TipoPessoa.FISICA) {
            aliquota = calcularAliquotaPessoaFisica(pedido.getValorTotalItens());
        } else if (tipoPessoa == TipoPessoa.JURIDICA) {
            aliquota = calcularAliquotaPessoaJuridica(pedido.getValorTotalItens(), destinatario.getRegimeTributacao());
        }

        itemNotaFiscalList = geradorItensNotaFiscal.adicionarItensNotaFiscal(pedido.getItens(), aliquota);

        double valorFreteComPercentual = calcularValorFreteComPercentual(pedido, destinatario);

        String idNotaFiscal = UUID.randomUUID().toString();

        NotaFiscal notaFiscal = NotaFiscal.builder()
                .idNotaFiscal(idNotaFiscal)
                .data(LocalDateTime.now())
                .valorTotalItens(pedido.getValorTotalItens())
                .valorFrete(valorFreteComPercentual)
                .itens(itemNotaFiscalList)
                .destinatario(destinatario)
                .build();

        enviarServicosAssociados(notaFiscal);

        return notaFiscal;
    }

    private double calcularAliquotaPessoaFisica(double valorTotalItens) {
        if (valorTotalItens < 500) {
            return ALIQUOTA_PADRAO;
        } else if (valorTotalItens <= 2000) {
            return ALIQUOTA_FISICA_ATE_500;
        } else if (valorTotalItens <= 3500) {
            return ALIQUOTA_FISICA_ATE_2000;
        } else {
            return ALIQUOTA_FISICA_ACIMA_2000;
        }
    }

    private double calcularAliquotaPessoaJuridica(double valorTotalItens, RegimeTributacaoPJ regimeTributacao) {
        if (regimeTributacao == RegimeTributacaoPJ.SIMPLES_NACIONAL) {
            return calculoAliquotaRegimeSimplesNacional(valorTotalItens);
        } else if (regimeTributacao == RegimeTributacaoPJ.LUCRO_REAL) {
            return calculoAliquotaRegimeLucroReal(valorTotalItens);
        } else if (regimeTributacao == RegimeTributacaoPJ.LUCRO_PRESUMIDO) {
            return calculoAliquotaRegimeLucroPresumido(valorTotalItens);
        }
        return ALIQUOTA_PADRAO;
    }

    private double calculoAliquotaRegimeLucroPresumido(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return ALIQUOTA_LUCRO_PRESUMIDO_ATE_1000;
        } else if (valorTotalItens <= 2000) {
            return ALIQUOTA_LUCRO_PRESUMIDO_ATE_2000;
        } else if (valorTotalItens <= 5000) {
            return ALIQUOTA_LUCRO_PRESUMIDO_ATE_5000;
        } else {
            return ALIQUOTA_LUCRO_PRESUMIDO_ACIMA_5000;
        }
    }

    private double calculoAliquotaRegimeLucroReal(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return ALIQUOTA_LUCRO_REAL_ATE_1000;
        } else if (valorTotalItens <= 2000) {
            return ALIQUOTA_LUCRO_REAL_ATE_2000;
        } else if (valorTotalItens <= 5000) {
            return ALIQUOTA_LUCRO_REAL_ATE_5000;
        } else {
            return ALIQUOTA_LUCRO_REAL_ACIMA_5000;
        }
    }

    private double calculoAliquotaRegimeSimplesNacional(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return ALIQUOTA_SIMPLES_ATE_1000;
        } else if (valorTotalItens <= 2000) {
            return ALIQUOTA_SIMPLES_ATE_2000;
        } else if (valorTotalItens <= 5000) {
            return ALIQUOTA_SIMPLES_ATE_5000;
        } else {
            return ALIQUOTA_SIMPLES_ACIMA_5000;
        }
    }

    private double calcularValorFreteComPercentual(Pedido pedido, Destinatario destinatario) {
        Regiao regiao = destinatario.getEnderecos().stream()
                .filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
                .map(Endereco::getRegiao)
                .findFirst()
                .orElse(null);

        double valorFrete = pedido.getValorFrete();

        if (regiao != null) {
            switch (regiao) {
                case NORTE:
                    return valorFrete * 1.08;
                case NORDESTE:
                    return valorFrete * 1.085;
                case CENTRO_OESTE:
                    return valorFrete * 1.07;
                case SUDESTE:
                    return valorFrete * 1.048;
                case SUL:
                    return valorFrete * 1.06;
            }
        }
        return 0;
    }

    private void enviarServicosAssociados(NotaFiscal notaFiscal) {
        estoqueService.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
        registroService.registrarNotaFiscal(notaFiscal);
        entregaService.agendarEntrega(notaFiscal);
        financeiroService.enviarNotaFiscalParaContasReceber(notaFiscal);
    }
}