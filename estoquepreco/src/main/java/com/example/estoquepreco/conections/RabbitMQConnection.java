package com.example.estoquepreco.conections;

import com.example.estoquepreco.consts.RabbitMQConsts;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;


@Component
public class RabbitMQConnection {
    private static final String NOME_EXCHANGE = "amq.direct";
    private AmqpAdmin amppAdmin;
    public RabbitMQConnection(AmqpAdmin amqpAdmin) {
        this.amppAdmin = amqpAdmin;
    }

    private Queue fila(String nomeFila){
        return new Queue(nomeFila,true,false,false);
    }

    private DirectExchange trocaDireta(){
        return new DirectExchange(NOME_EXCHANGE);
    }

    private Binding relacionamento(Queue fila, DirectExchange troca){
        return new Binding(fila.getName(), Binding.DestinationType.QUEUE,troca.getName(),fila.getName(),null);
    }

    //assim que a aplicação for inciada o metodo adicinar sera executado, a anotação postConstructor é responsavel por isso.
    @PostConstruct
    private void adiciona(){
        Queue filaEstoque = this.fila(RabbitMQConsts.FILA_ESTOQUE);
        Queue filaPreco = this.fila(RabbitMQConsts.FILA_PRECO);

        DirectExchange troca = this.trocaDireta();

        Binding ligacaoEstoque = this.relacionamento(filaEstoque, troca);
        Binding ligacaoPreco = this.relacionamento(filaEstoque, troca);

        this.amppAdmin.declareQueue(filaEstoque);
        this.amppAdmin.declareQueue(filaPreco);

        this.amppAdmin.declareExchange(troca);

        this.amppAdmin.declareBinding(ligacaoEstoque);
        this.amppAdmin.declareBinding(ligacaoPreco);

    }
}
