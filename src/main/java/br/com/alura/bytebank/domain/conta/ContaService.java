package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connectionFactory;

    public ContaService() {
        connectionFactory = new ConnectionFactory();
    }


    public Set<Conta> listarContasAbertas() {
        Connection connection = connectionFactory.getConnection();
        return new ContaDAO(connection).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection connection = connectionFactory.getConnection();
        new ContaDAO(connection).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        if(!conta.getEstaAtiva())
        {
            throw new RegraDeNegocioException("A conta não está ativa!");
        }

        new ContaDAO(this.connectionFactory.getConnection()).alterar(conta.getNumero(),conta.getSaldo().subtract(valor));
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        if(!conta.getEstaAtiva())
        {
            throw new RegraDeNegocioException("A conta não está ativa!");
        }

        new ContaDAO(connectionFactory.getConnection()).alterar(conta.getNumero(),conta.getSaldo().add(valor));
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroDaContaOrigem,valor);
        this.realizarDeposito(numeroDaContaDestino, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }
        new ContaDAO(this.connectionFactory.getConnection()).delete(conta.getNumero());
    }

    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }
        new ContaDAO(this.connectionFactory.getConnection()).encerramentoLogico(conta.getNumero());
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Conta conta = new ContaDAO(connectionFactory.getConnection()).buscarContaPorNumero(numero);
        if(conta != null)
            return conta;
        else
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
    }
}
