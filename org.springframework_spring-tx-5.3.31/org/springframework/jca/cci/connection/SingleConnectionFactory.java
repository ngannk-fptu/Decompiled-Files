/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.NotSupportedException
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jca.cci.connection.DelegatingConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class SingleConnectionFactory
extends DelegatingConnectionFactory
implements DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Connection target;
    @Nullable
    private Connection connection;
    private final Object connectionMonitor = new Object();

    public SingleConnectionFactory() {
    }

    public SingleConnectionFactory(Connection target) {
        Assert.notNull((Object)target, (String)"Target Connection must not be null");
        this.target = target;
        this.connection = this.getCloseSuppressingConnectionProxy(target);
    }

    public SingleConnectionFactory(ConnectionFactory targetConnectionFactory) {
        Assert.notNull((Object)targetConnectionFactory, (String)"Target ConnectionFactory must not be null");
        this.setTargetConnectionFactory(targetConnectionFactory);
    }

    @Override
    public void afterPropertiesSet() {
        if (this.connection == null && this.getTargetConnectionFactory() == null) {
            throw new IllegalArgumentException("Connection or 'targetConnectionFactory' is required");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Connection getConnection() throws ResourceException {
        Object object = this.connectionMonitor;
        synchronized (object) {
            if (this.connection == null) {
                this.initConnection();
            }
            return this.connection;
        }
    }

    @Override
    public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
        throw new NotSupportedException("SingleConnectionFactory does not support custom ConnectionSpec");
    }

    public void destroy() {
        this.resetConnection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initConnection() throws ResourceException {
        if (this.getTargetConnectionFactory() == null) {
            throw new IllegalStateException("'targetConnectionFactory' is required for lazily initializing a Connection");
        }
        Object object = this.connectionMonitor;
        synchronized (object) {
            if (this.target != null) {
                this.closeConnection(this.target);
            }
            this.target = this.doCreateConnection();
            this.prepareConnection(this.target);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Established shared CCI Connection: " + this.target));
            }
            this.connection = this.getCloseSuppressingConnectionProxy(this.target);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetConnection() {
        Object object = this.connectionMonitor;
        synchronized (object) {
            if (this.target != null) {
                this.closeConnection(this.target);
            }
            this.target = null;
            this.connection = null;
        }
    }

    protected Connection doCreateConnection() throws ResourceException {
        ConnectionFactory connectionFactory = this.getTargetConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No 'targetConnectionFactory' set");
        return connectionFactory.getConnection();
    }

    protected void prepareConnection(Connection con) throws ResourceException {
    }

    protected void closeConnection(Connection con) {
        try {
            con.close();
        }
        catch (Throwable ex) {
            this.logger.warn((Object)"Could not close shared CCI Connection", ex);
        }
    }

    protected Connection getCloseSuppressingConnectionProxy(Connection target) {
        return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, (InvocationHandler)new CloseSuppressingInvocationHandler(target));
    }

    private static final class CloseSuppressingInvocationHandler
    implements InvocationHandler {
        private final Connection target;

        private CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "close": {
                    return null;
                }
            }
            try {
                return method.invoke((Object)this.target, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

