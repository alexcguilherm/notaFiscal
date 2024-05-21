package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GeradorItensNotaFiscal {


    public List<ItemNotaFiscal> adicionarItensNotaFiscal(List<Item> items, double aliquotaPercentual) {


        List<ItemNotaFiscal> itemNotaFiscalList = new ArrayList<>();

        for (Item item : items) {
            double valorTributo = calcularValorTributo(item, aliquotaPercentual);
            ItemNotaFiscal itemNotaFiscal = ItemNotaFiscal.builder()
                    .idItem(item.getIdItem())
                    .descricao(item.getDescricao())
                    .valorUnitario(item.getValorUnitario())
                    .quantidade(item.getQuantidade())
                    .valorTributoItem(valorTributo)
                    .build();
            itemNotaFiscalList.add(itemNotaFiscal);
        }
        return itemNotaFiscalList;
    }

   public double calcularValorTributo(Item item, double aliquotaPercentual) {
        return item.getValorUnitario() * aliquotaPercentual;
   }
}



