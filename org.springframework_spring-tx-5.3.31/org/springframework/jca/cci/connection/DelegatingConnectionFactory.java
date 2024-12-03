/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  javax.resource.cci.RecordFactory
 *  javax.resource.cci.ResourceAdapterMetaData
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.connection;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class DelegatingConnectionFactory
implements ConnectionFactory,
InitializingBean {
    @Nullable
    private ConnectionFactory targetConnectionFactory;

    public void setTargetConnectionFactory(@Nullable ConnectionFactory targetConnectionFactory) {
        this.targetConnectionFactory = targetConnectionFactory;
    }

    @Nullable
    public ConnectionFactory getTargetConnectionFactory() {
        return this.targetConnectionFactory;
    }

    protected ConnectionFactory obtainTargetConnectionFactory() {
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No 'targetConnectionFactory' set");
        return connectionFactory;
    }

    public void afterPropertiesSet() {
        if (this.getTargetConnectionFactory() == null) {
            throw new IllegalArgumentException("Property 'targetConnectionFactory' is required");
        }
    }

    public Connection getConnection() throws ResourceException {
        return this.obtainTargetConnectionFactory().getConnection();
    }

    public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
        return this.obtainTargetConnectionFactory().getConnection(connectionSpec);
    }

    public RecordFactory getRecordFactory() throws ResourceException {
        return this.obtainTargetConnectionFactory().getRecordFactory();
    }

    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        return this.obtainTargetConnectionFactory().getMetaData();
    }

    public Reference getReference() throws NamingException {
        return this.obtainTargetConnectionFactory().getReference();
    }

    public void setReference(Reference reference) {
        this.obtainTargetConnectionFactory().setReference(reference);
    }
}

