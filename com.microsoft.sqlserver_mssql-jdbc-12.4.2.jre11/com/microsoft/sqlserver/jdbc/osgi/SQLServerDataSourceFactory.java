/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.service.jdbc.DataSourceFactory
 */
package com.microsoft.sqlserver.jdbc.osgi;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.osgi.service.jdbc.DataSourceFactory;

public class SQLServerDataSourceFactory
implements DataSourceFactory {
    private static Logger osgiLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.osgi.SQLServerDataSourceFactory");
    private static final String NOT_SUPPORTED_MSG = ResourceBundle.getBundle("com.microsoft.sqlserver.jdbc.SQLServerResource", Locale.getDefault()).getString("R_propertyNotSupported");

    public DataSource createDataSource(Properties props) throws SQLException {
        SQLServerDataSource source = new SQLServerDataSource();
        this.setup(source, props);
        return source;
    }

    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props) throws SQLException {
        SQLServerConnectionPoolDataSource poolDataSource = new SQLServerConnectionPoolDataSource();
        this.setupXSource(poolDataSource, props);
        return poolDataSource;
    }

    public XADataSource createXADataSource(Properties props) throws SQLException {
        SQLServerXADataSource xaDataSource = new SQLServerXADataSource();
        this.setupXSource(xaDataSource, props);
        return xaDataSource;
    }

    public Driver createDriver(Properties props) throws SQLException {
        return new SQLServerDriver();
    }

    private void setup(SQLServerDataSource source, Properties props) {
        if (props == null) {
            return;
        }
        if (props.containsKey("databaseName")) {
            source.setDatabaseName(props.getProperty("databaseName"));
        }
        if (props.containsKey("dataSourceName")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "dataSourceName");
        }
        if (props.containsKey("description")) {
            source.setDescription(props.getProperty("description"));
        }
        if (props.containsKey("networkProtocol")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "networkProtocol");
        }
        if (props.containsKey("password")) {
            source.setPassword(props.getProperty("password"));
        }
        if (props.containsKey("portNumber")) {
            source.setPortNumber(Integer.parseInt(props.getProperty("portNumber")));
        }
        if (props.containsKey("roleName")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "roleName");
        }
        if (props.containsKey("serverName")) {
            source.setServerName(props.getProperty("serverName"));
        }
        if (props.containsKey("url")) {
            source.setURL(props.getProperty("url"));
        }
        if (props.containsKey("user")) {
            source.setUser(props.getProperty("user"));
        }
    }

    private void setupXSource(SQLServerConnectionPoolDataSource source, Properties props) {
        if (props == null) {
            return;
        }
        this.setup(source, props);
        if (props.containsKey("initialPoolSize")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "initialPoolSize");
        }
        if (props.containsKey("maxIdleTime")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "maxIdleTime");
        }
        if (props.containsKey("maxStatements")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "maxStatements");
        }
        if (props.containsKey("maxPoolSize")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "maxPoolSize");
        }
        if (props.containsKey("minPoolSize")) {
            osgiLogger.log(Level.WARNING, NOT_SUPPORTED_MSG, "minPoolSize");
        }
    }
}

