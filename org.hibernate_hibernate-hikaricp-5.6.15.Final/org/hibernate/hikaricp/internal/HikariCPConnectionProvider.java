/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.zaxxer.hikari.HikariConfig
 *  com.zaxxer.hikari.HikariDataSource
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.service.UnknownUnwrapTypeException
 *  org.hibernate.service.spi.Configurable
 *  org.hibernate.service.spi.Stoppable
 *  org.jboss.logging.Logger
 */
package org.hibernate.hikaricp.internal;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariConfigurationUtil;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.jboss.logging.Logger;

public class HikariCPConnectionProvider
implements ConnectionProvider,
Configurable,
Stoppable {
    private static final long serialVersionUID = -9131625057941275711L;
    private static final Logger LOGGER = Logger.getLogger(HikariCPConnectionProvider.class);
    private HikariConfig hcfg = null;
    private HikariDataSource hds = null;

    public void configure(Map props) throws HibernateException {
        try {
            LOGGER.debug((Object)"Configuring HikariCP");
            this.hcfg = HikariConfigurationUtil.loadConfiguration(props);
            this.hds = new HikariDataSource(this.hcfg);
        }
        catch (Exception e) {
            throw new HibernateException((Throwable)e);
        }
        LOGGER.debug((Object)"HikariCP Configured");
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        if (this.hds != null) {
            conn = this.hds.getConnection();
        }
        return conn;
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    public boolean supportsAggressiveRelease() {
        return false;
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals((Object)unwrapType) || HikariCPConnectionProvider.class.isAssignableFrom(unwrapType) || DataSource.class.isAssignableFrom(unwrapType);
    }

    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) || HikariCPConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T)this.hds;
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }

    public void stop() {
        this.hds.close();
    }
}

