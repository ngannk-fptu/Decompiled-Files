/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.connection;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import org.springframework.core.NamedThreadLocal;
import org.springframework.jca.cci.connection.DelegatingConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class ConnectionSpecConnectionFactoryAdapter
extends DelegatingConnectionFactory {
    @Nullable
    private ConnectionSpec connectionSpec;
    private final ThreadLocal<ConnectionSpec> threadBoundSpec = new NamedThreadLocal("Current CCI ConnectionSpec");

    public void setConnectionSpec(ConnectionSpec connectionSpec) {
        this.connectionSpec = connectionSpec;
    }

    public void setConnectionSpecForCurrentThread(ConnectionSpec spec) {
        this.threadBoundSpec.set(spec);
    }

    public void removeConnectionSpecFromCurrentThread() {
        this.threadBoundSpec.remove();
    }

    @Override
    public final Connection getConnection() throws ResourceException {
        ConnectionSpec threadSpec = this.threadBoundSpec.get();
        if (threadSpec != null) {
            return this.doGetConnection(threadSpec);
        }
        return this.doGetConnection(this.connectionSpec);
    }

    protected Connection doGetConnection(@Nullable ConnectionSpec spec) throws ResourceException {
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No 'targetConnectionFactory' set");
        return spec != null ? connectionFactory.getConnection(spec) : connectionFactory.getConnection();
    }
}

