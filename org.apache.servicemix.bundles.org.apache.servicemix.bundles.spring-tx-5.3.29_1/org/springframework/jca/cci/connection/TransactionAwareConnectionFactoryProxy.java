/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.spi.IllegalStateException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.cci.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.IllegalStateException;
import org.springframework.jca.cci.connection.ConnectionFactoryUtils;
import org.springframework.jca.cci.connection.DelegatingConnectionFactory;
import org.springframework.lang.Nullable;

@Deprecated
public class TransactionAwareConnectionFactoryProxy
extends DelegatingConnectionFactory {
    public TransactionAwareConnectionFactoryProxy() {
    }

    public TransactionAwareConnectionFactoryProxy(ConnectionFactory targetConnectionFactory) {
        this.setTargetConnectionFactory(targetConnectionFactory);
        this.afterPropertiesSet();
    }

    @Override
    public Connection getConnection() throws ResourceException {
        ConnectionFactory targetConnectionFactory = this.obtainTargetConnectionFactory();
        Connection con = ConnectionFactoryUtils.doGetConnection(targetConnectionFactory);
        return this.getTransactionAwareConnectionProxy(con, targetConnectionFactory);
    }

    protected Connection getTransactionAwareConnectionProxy(Connection target, ConnectionFactory cf) {
        return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, (InvocationHandler)new TransactionAwareInvocationHandler(target, cf));
    }

    private static class TransactionAwareInvocationHandler
    implements InvocationHandler {
        private final Connection target;
        private final ConnectionFactory connectionFactory;

        public TransactionAwareInvocationHandler(Connection target, ConnectionFactory cf) {
            this.target = target;
            this.connectionFactory = cf;
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
                case "getLocalTransaction": {
                    if (ConnectionFactoryUtils.isConnectionTransactional(this.target, this.connectionFactory)) {
                        throw new IllegalStateException("Local transaction handling not allowed within a managed transaction");
                    }
                    return this.target.getLocalTransaction();
                }
                case "close": {
                    ConnectionFactoryUtils.doReleaseConnection(this.target, this.connectionFactory);
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

