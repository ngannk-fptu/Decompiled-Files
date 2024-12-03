/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 *  org.apache.derby.jdbc.EmbeddedDriver
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource.embedded;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.OutputStreamFactory;
import org.springframework.lang.Nullable;

final class DerbyEmbeddedDatabaseConfigurer
implements EmbeddedDatabaseConfigurer {
    private static final String URL_TEMPLATE = "jdbc:derby:memory:%s;%s";
    @Nullable
    private static DerbyEmbeddedDatabaseConfigurer instance;

    public static synchronized DerbyEmbeddedDatabaseConfigurer getInstance() {
        if (instance == null) {
            System.setProperty("derby.stream.error.method", OutputStreamFactory.class.getName() + ".getNoopOutputStream");
            instance = new DerbyEmbeddedDatabaseConfigurer();
        }
        return instance;
    }

    private DerbyEmbeddedDatabaseConfigurer() {
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        properties.setDriverClass(EmbeddedDriver.class);
        properties.setUrl(String.format(URL_TEMPLATE, databaseName, "create=true"));
        properties.setUsername("sa");
        properties.setPassword("");
    }

    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        block2: {
            try {
                new EmbeddedDriver().connect(String.format(URL_TEMPLATE, databaseName, "drop=true"), new Properties());
            }
            catch (SQLException ex) {
                if ("08006".equals(ex.getSQLState())) break block2;
                LogFactory.getLog(this.getClass()).warn((Object)"Could not shut down embedded Derby database", (Throwable)ex);
            }
        }
    }
}

