/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.client.ClientPrincipal;
import com.hazelcast.core.ClientType;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.security.Credentials;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.impl.xa.XATransactionContextImpl;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public final class ClientEndpointImpl
implements ClientEndpoint {
    private final ClientEngine clientEngine;
    private final ILogger logger;
    private final NodeEngineImpl nodeEngine;
    private final Connection connection;
    private final ConcurrentMap<String, TransactionContext> transactionContextMap = new ConcurrentHashMap<String, TransactionContext>();
    private final ConcurrentMap<String, Callable> removeListenerActions = new ConcurrentHashMap<String, Callable>();
    private final SocketAddress socketAddress;
    private final long creationTime;
    private LoginContext loginContext;
    private ClientPrincipal principal;
    private boolean ownerConnection;
    private Credentials credentials;
    private volatile boolean authenticated;
    private int clientVersion;
    private String clientVersionString;
    private long authenticationCorrelationId;
    private volatile String stats;
    private String clientName;
    private Set<String> labels;
    private volatile boolean destroyed;

    public ClientEndpointImpl(ClientEngine clientEngine, NodeEngineImpl nodeEngine, Connection connection) {
        this.clientEngine = clientEngine;
        this.logger = clientEngine.getLogger(this.getClass());
        this.nodeEngine = nodeEngine;
        this.connection = connection;
        if (connection instanceof TcpIpConnection) {
            TcpIpConnection tcpIpConnection = (TcpIpConnection)connection;
            this.socketAddress = tcpIpConnection.getRemoteSocketAddress();
        } else {
            this.socketAddress = null;
        }
        this.clientVersion = -1;
        this.clientVersionString = "Unknown";
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public String getUuid() {
        return this.principal != null ? this.principal.getUuid() : null;
    }

    @Override
    public boolean isAlive() {
        return this.connection.isAlive();
    }

    @Override
    public void setLoginContext(LoginContext loginContext) {
        this.loginContext = loginContext;
    }

    @Override
    public Subject getSubject() {
        return this.loginContext != null ? this.loginContext.getSubject() : null;
    }

    @Override
    public boolean isOwnerConnection() {
        return this.ownerConnection;
    }

    @Override
    public void authenticated(ClientPrincipal principal, Credentials credentials, boolean firstConnection, String clientVersion, long authCorrelationId, String clientName, Set<String> labels) {
        this.principal = principal;
        this.ownerConnection = firstConnection;
        this.credentials = credentials;
        this.authenticated = true;
        this.authenticationCorrelationId = authCorrelationId;
        this.setClientVersion(clientVersion);
        this.clientName = clientName;
        this.labels = labels;
    }

    @Override
    public void authenticated(ClientPrincipal principal) {
        this.principal = principal;
        this.authenticated = true;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public int getClientVersion() {
        return this.clientVersion;
    }

    @Override
    public void setClientVersion(String version) {
        this.clientVersionString = version;
        this.clientVersion = BuildInfo.calculateVersion(version);
    }

    @Override
    public void setClientStatistics(String stats) {
        this.stats = stats;
    }

    @Override
    public String getClientStatistics() {
        return this.stats;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return (InetSocketAddress)this.socketAddress;
    }

    @Override
    public ClientType getClientType() {
        ClientType type;
        switch (this.connection.getType()) {
            case JAVA_CLIENT: {
                type = ClientType.JAVA;
                break;
            }
            case CSHARP_CLIENT: {
                type = ClientType.CSHARP;
                break;
            }
            case CPP_CLIENT: {
                type = ClientType.CPP;
                break;
            }
            case PYTHON_CLIENT: {
                type = ClientType.PYTHON;
                break;
            }
            case RUBY_CLIENT: {
                type = ClientType.RUBY;
                break;
            }
            case NODEJS_CLIENT: {
                type = ClientType.NODEJS;
                break;
            }
            case GO_CLIENT: {
                type = ClientType.GO;
                break;
            }
            case BINARY_CLIENT: {
                type = ClientType.OTHER;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid connection type: " + (Object)((Object)this.connection.getType()));
            }
        }
        return type;
    }

    @Override
    public String getName() {
        return this.clientName;
    }

    @Override
    public Set<String> getLabels() {
        return this.labels;
    }

    @Override
    public TransactionContext getTransactionContext(String txnId) {
        TransactionContext transactionContext = (TransactionContext)this.transactionContextMap.get(txnId);
        if (transactionContext == null) {
            throw new TransactionException("No transaction context found for txnId:" + txnId);
        }
        return transactionContext;
    }

    @Override
    public Credentials getCredentials() {
        return this.credentials;
    }

    @Override
    public void setTransactionContext(TransactionContext transactionContext) {
        this.transactionContextMap.put(transactionContext.getTxnId(), transactionContext);
        if (this.destroyed) {
            this.removedAndRollbackTransactionContext(transactionContext.getTxnId());
        }
    }

    @Override
    public void removeTransactionContext(String txnId) {
        this.transactionContextMap.remove(txnId);
    }

    @Override
    public void addListenerDestroyAction(final String service, final String topic, final String id) {
        final EventService eventService = this.clientEngine.getEventService();
        this.addDestroyAction(id, new Callable<Boolean>(){

            @Override
            public Boolean call() {
                return eventService.deregisterListener(service, topic, id);
            }
        });
    }

    @Override
    public void addDestroyAction(String registrationId, Callable<Boolean> removeAction) {
        this.removeListenerActions.put(registrationId, removeAction);
        if (this.destroyed) {
            this.removeAndCallRemoveAction(registrationId);
        }
    }

    @Override
    public boolean removeDestroyAction(String id) {
        return this.removeListenerActions.remove(id) != null;
    }

    @Override
    public void clearAllListeners() {
        for (String registrationId : this.removeListenerActions.keySet()) {
            this.removeAndCallRemoveAction(registrationId);
        }
    }

    public void destroy() throws LoginException {
        this.destroyed = true;
        this.nodeEngine.onClientDisconnected(this.getUuid());
        this.clearAllListeners();
        for (String txnId : this.transactionContextMap.keySet()) {
            this.removedAndRollbackTransactionContext(txnId);
        }
        try {
            LoginContext lc = this.loginContext;
            if (lc != null) {
                lc.logout();
            }
        }
        finally {
            this.authenticated = false;
        }
    }

    private void removeAndCallRemoveAction(String uuid) {
        Callable callable = (Callable)this.removeListenerActions.remove(uuid);
        if (callable != null) {
            try {
                callable.call();
            }
            catch (Exception e) {
                this.logger.warning("Exception during remove listener action", e);
            }
        }
    }

    private void removedAndRollbackTransactionContext(String txnId) {
        TransactionContext context = (TransactionContext)this.transactionContextMap.remove(txnId);
        if (context != null) {
            if (context instanceof XATransactionContextImpl) {
                return;
            }
            try {
                context.rollbackTransaction();
            }
            catch (HazelcastInstanceNotActiveException e) {
                this.logger.finest(e);
            }
            catch (Exception e) {
                this.logger.warning(e);
            }
        }
    }

    public String toString() {
        return "ClientEndpoint{connection=" + this.connection + ", principal='" + this.principal + ", ownerConnection=" + this.ownerConnection + ", authenticated=" + this.authenticated + ", clientVersion=" + this.clientVersionString + ", creationTime=" + this.creationTime + ", latest statistics=" + this.stats + '}';
    }

    public long getAuthenticationCorrelationId() {
        return this.authenticationCorrelationId;
    }
}

