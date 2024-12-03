/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.spi.ConnectionManager
 *  javax.resource.spi.ManagedConnectionFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.support;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

public class LocalConnectionFactoryBean
implements FactoryBean<Object>,
InitializingBean {
    @Nullable
    private ManagedConnectionFactory managedConnectionFactory;
    @Nullable
    private ConnectionManager connectionManager;
    @Nullable
    private Object connectionFactory;

    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void afterPropertiesSet() throws ResourceException {
        if (this.managedConnectionFactory == null) {
            throw new IllegalArgumentException("Property 'managedConnectionFactory' is required");
        }
        this.connectionFactory = this.connectionManager != null ? this.managedConnectionFactory.createConnectionFactory(this.connectionManager) : this.managedConnectionFactory.createConnectionFactory();
    }

    @Nullable
    public Object getObject() {
        return this.connectionFactory;
    }

    public Class<?> getObjectType() {
        return this.connectionFactory != null ? this.connectionFactory.getClass() : null;
    }

    public boolean isSingleton() {
        return true;
    }
}

