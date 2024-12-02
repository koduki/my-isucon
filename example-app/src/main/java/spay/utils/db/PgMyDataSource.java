package spay.utils.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.jdbc.PgConnection;

public class PgMyDataSource extends org.postgresql.ds.PGSimpleDataSource {
    @Override
    public Connection getConnection() throws SQLException {
        System.out.println("hnakada:01");
        return new PgProxyConnection(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        System.out.println("hnakada:02");
        return new PgProxyConnection(super.getConnection(username, password));
    }
}
