package br.com.alura.bytebank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    /**
     * Método anntigo utilizado sem o pool de conexões HikariCP
     */
//    public Connection getConnection() {
//        try {
//            return DriverManager
//                    .getConnection("jdbc:mysql://localhost:3306/byte_bank", "root", "root");
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    public Connection getConnection() {
        try {
            return this.createDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private HikariDataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/byte_bank");
        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

}
