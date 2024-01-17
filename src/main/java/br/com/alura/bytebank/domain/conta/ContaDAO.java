package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private final Connection connection;

    ContaDAO(Connection connection) {
        this.connection = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {

        PreparedStatement preparedStatement = null;

        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO, true);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email, esta_Ativa)" +
                "VALUES (?,?,?,?,?,?)";

        try {

            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, cliente.getNome());
            preparedStatement.setString(4, cliente.getCpf());
            preparedStatement.setString(5, cliente.getEmail());
            preparedStatement.setBoolean(6, conta.getEstaAtiva());

            preparedStatement.execute();

            connection.commit();

            preparedStatement.close();

            connection.close();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }

    public Set<Conta> listar() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta WHERE esta_ativa = true";

        try {
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                int numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nomeCliente = resultSet.getString(3);
                String cpfCliente = resultSet.getString(4);
                String emailCliente = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);

                Cliente cliente = new Cliente(new DadosCadastroCliente(nomeCliente,cpfCliente,emailCliente));
                Conta conta = new Conta(numero,cliente, saldo, estaAtiva);

                contas.add(conta);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contas;
    }

    public Conta buscarContaPorNumero(Integer numero)
    {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Conta conta = null;

        String sql = "SELECT * FROM conta WHERE numero = ? AND esta_ativa = true";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                int numeroConta = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nomeCliente = resultSet.getString(3);
                String cpfCliente = resultSet.getString(4);
                String emailCliente = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);

                Cliente cliente = new Cliente(new DadosCadastroCliente(nomeCliente,cpfCliente,emailCliente));
                conta = new Conta(numeroConta,cliente, saldo, estaAtiva);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor) {

        PreparedStatement preparedStatement;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            connection.setAutoCommit(false);

            preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setBigDecimal(1, valor);
            preparedStatement.setInt(2,numero);

            preparedStatement.execute();

            connection.commit();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            throw new RuntimeException(e);
        }

    }

    public void delete(Integer numeroDaConta) {

        PreparedStatement preparedStatement;
        String sql = "DELETE FROM conta WHERE numero = ?";

        try {
            connection.setAutoCommit(false);

            preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setInt(1,numeroDaConta);

            preparedStatement.execute();

            connection.commit();

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }

    public void encerramentoLogico(Integer numeroDaConta) {

        PreparedStatement preparedStatement;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            connection.setAutoCommit(false);

            preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setInt(1,numeroDaConta);

            preparedStatement.execute();

            connection.commit();

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }
}
