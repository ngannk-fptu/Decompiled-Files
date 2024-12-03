/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Driver;
import java.util.Map;
import java.util.Properties;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreator;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreatorFactory;
import org.hibernate.engine.jdbc.connections.internal.DriverConnectionCreator;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionCreator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class ConnectionCreatorFactoryImpl
implements ConnectionCreatorFactory {
    public static final ConnectionCreatorFactory INSTANCE = new ConnectionCreatorFactoryImpl();

    private ConnectionCreatorFactoryImpl() {
    }

    @Override
    public ConnectionCreator create(Driver driver, ServiceRegistryImplementor serviceRegistry, String url, Properties connectionProps, Boolean autoCommit, Integer isolation, String initSql, Map<Object, Object> configurationValues) {
        if (driver == null) {
            return new DriverManagerConnectionCreator(serviceRegistry, url, connectionProps, autoCommit, isolation, initSql);
        }
        return new DriverConnectionCreator(driver, serviceRegistry, url, connectionProps, autoCommit, isolation, initSql);
    }
}

