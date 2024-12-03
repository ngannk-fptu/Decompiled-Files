/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.InjectService;
import org.hibernate.service.spi.Stoppable;

public class DatasourceConnectionProviderImpl
implements ConnectionProvider,
Configurable,
Stoppable {
    private DataSource dataSource;
    private String user;
    private String pass;
    private boolean useCredentials;
    private JndiService jndiService;
    private boolean available;

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @InjectService(required=false)
    public void setJndiService(JndiService jndiService) {
        this.jndiService = jndiService;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals((Object)unwrapType) || DatasourceConnectionProviderImpl.class.isAssignableFrom(unwrapType) || DataSource.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) || DatasourceConnectionProviderImpl.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T)this.getDataSource();
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }

    @Override
    public void configure(Map configValues) {
        if (this.dataSource == null) {
            Object dataSource = configValues.get("hibernate.connection.datasource");
            if (DataSource.class.isInstance(dataSource)) {
                this.dataSource = (DataSource)dataSource;
            } else {
                String dataSourceJndiName = (String)dataSource;
                if (dataSourceJndiName == null) {
                    throw new HibernateException("DataSource to use was not injected nor specified by [hibernate.connection.datasource] configuration property");
                }
                if (this.jndiService == null) {
                    throw new HibernateException("Unable to locate JndiService to lookup Datasource");
                }
                this.dataSource = (DataSource)this.jndiService.locate(dataSourceJndiName);
            }
        }
        if (this.dataSource == null) {
            throw new HibernateException("Unable to determine appropriate DataSource to use");
        }
        this.user = (String)configValues.get("hibernate.connection.username");
        this.pass = (String)configValues.get("hibernate.connection.password");
        this.useCredentials = this.user != null || this.pass != null;
        this.available = true;
    }

    @Override
    public void stop() {
        this.available = false;
        this.dataSource = null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!this.available) {
            throw new HibernateException("Provider is closed!");
        }
        return this.useCredentials ? this.dataSource.getConnection(this.user, this.pass) : this.dataSource.getConnection();
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}

