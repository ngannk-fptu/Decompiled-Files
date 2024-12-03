/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.Utils;

public class DataSourceConnectionFactory
implements ConnectionFactory {
    private final DataSource dataSource;
    private final String userName;
    private final char[] userPassword;

    public DataSourceConnectionFactory(DataSource dataSource) {
        this(dataSource, null, (char[])null);
    }

    public DataSourceConnectionFactory(DataSource dataSource, String userName, char[] userPassword) {
        this.dataSource = dataSource;
        this.userName = userName;
        this.userPassword = Utils.clone(userPassword);
    }

    public DataSourceConnectionFactory(DataSource dataSource, String userName, String password) {
        this.dataSource = dataSource;
        this.userName = userName;
        this.userPassword = Utils.toCharArray(password);
    }

    @Override
    public Connection createConnection() throws SQLException {
        if (null == this.userName && null == this.userPassword) {
            return this.dataSource.getConnection();
        }
        return this.dataSource.getConnection(this.userName, Utils.toString(this.userPassword));
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public String getUserName() {
        return this.userName;
    }

    public char[] getUserPassword() {
        return Utils.clone(this.userPassword);
    }
}

