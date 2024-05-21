package br.com.itau.calculadoratributos;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.GeradorItensNotaFiscal;
import br.com.itau.geradornotafiscal.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GeradorNotaFiscalServiceImplTest {

    @InjectMocks
    private GeradorNotaFiscalServiceImpl geradorNotaFiscalService;

    @Spy
    private GeradorItensNotaFiscal geradorItensNotaFiscal;

    @Mock
    private EntregaService entregaService;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private FinanceiroService financeiroService;

    @Mock
    private RegistroService registroService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaFisicaWithValorTotalItensLessThan500() {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(400);
        pedido.setValorFrete(100);
        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(TipoPessoa.FISICA);

        // Create and add Endereco to the Destinatario
        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));

        pedido.setDestinatario(destinatario);

        // Create and add items to the Pedido
        Item item = new Item();
        item.setValorUnitario(100);
        item.setQuantidade(4);
        pedido.setItens(Arrays.asList(item));

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0, notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoLucroPresumidoAndValorTotalItensGreaterThan5000() {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(6000);
        pedido.setValorFrete(100);
        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(TipoPessoa.JURIDICA);
        destinatario.setRegimeTributacao(RegimeTributacaoPJ.LUCRO_PRESUMIDO);

        // Create and add Endereco to the Destinatario
        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));

        pedido.setDestinatario(destinatario);

        // Create and add items to the Pedido
        Item item = new Item();
        item.setValorUnitario(1000);
        item.setQuantidade(6);
        pedido.setItens(Arrays.asList(item));

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.20 * item.getValorUnitario(), notaFiscal.getItens().get(0).getValorTributoItem());
    }


    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoLucroRealAndValorTotalItensLessThan1000() {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(900);
        pedido.setValorFrete(50);
        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(TipoPessoa.JURIDICA);
        destinatario.setRegimeTributacao(RegimeTributacaoPJ.LUCRO_REAL);
        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));
        pedido.setDestinatario(destinatario);
        Item item = new Item();
        item.setValorUnitario(100);
        item.setQuantidade(3);
        pedido.setItens(Arrays.asList(item));


        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.03 * item.getValorUnitario(), notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoSimplesNacionalAndValorTotalItensGreaterThan2000() {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(3000);
        pedido.setValorFrete(50);
        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(TipoPessoa.JURIDICA);
        destinatario.setRegimeTributacao(RegimeTributacaoPJ.SIMPLES_NACIONAL);
        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));
        pedido.setDestinatario(destinatario);
        Item item = new Item();
        item.setValorUnitario(1000);
        item.setQuantidade(3);
        pedido.setItens(Arrays.asList(item));


        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.13 * item.getValorUnitario(), notaFiscal.getItens().get(0).getValorTributoItem());
    }


    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoSimplesNacionalAndValorTotalItensGreaterThan2000With10Items() {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(30000);
        pedido.setValorFrete(50);
        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(TipoPessoa.JURIDICA);
        destinatario.setRegimeTributacao(RegimeTributacaoPJ.SIMPLES_NACIONAL);
        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));
        pedido.setDestinatario(destinatario);

        List<Item> itens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setValorUnitario(1000);
            item.setQuantidade(3);
            itens.add(item);
        }
        pedido.setItens(itens);


        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(10, notaFiscal.getItens().size());
        assertEquals(0.19 * itens.get(0).getValorUnitario() , notaFiscal.getItens().get(0).getValorTributoItem());
    }

}