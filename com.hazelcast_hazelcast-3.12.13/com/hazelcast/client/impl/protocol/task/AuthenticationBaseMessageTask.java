/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.ReAuthenticationOperationSupplier;
import com.hazelcast.client.impl.client.ClientPrincipal;
import com.hazelcast.client.impl.protocol.AuthenticationStatus;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractStableClusterMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.security.Credentials;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.security.UsernamePasswordCredentials;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public abstract class AuthenticationBaseMessageTask<P>
extends AbstractStableClusterMessageTask<P>
implements BlockingMessageTask {
    protected transient ClientPrincipal principal;
    protected transient String clientName;
    protected transient Set<String> labels;
    protected transient Credentials credentials;
    protected transient String clusterId;
    protected transient Integer partitionCount;
    transient byte clientSerializationVersion;
    transient String clientVersion;

    AuthenticationBaseMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        return new ReAuthenticationOperationSupplier(this.getUuid(), this.clientMessage.getCorrelationId());
    }

    @Override
    protected Object resolve(Object response) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Processed owner authentication with principal " + this.principal);
        }
        return this.prepareAuthenticatedClientMessage();
    }

    @Override
    protected boolean requiresAuthentication() {
        return false;
    }

    @Override
    public void processMessage() throws Throwable {
        switch (this.authenticate()) {
            case SERIALIZATION_VERSION_MISMATCH: {
                this.sendClientMessage(this.prepareSerializationVersionMismatchClientMessage());
                break;
            }
            case NOT_ALLOWED_IN_CLUSTER: {
                this.sendClientMessage(this.prepareNotAllowedInCluster());
                break;
            }
            case CREDENTIALS_FAILED: {
                this.sendClientMessage(this.prepareUnauthenticatedClientMessage());
                break;
            }
            case AUTHENTICATED: {
                if (this.isOwnerConnection()) {
                    this.principal = new ClientPrincipal(this.getUuid(), this.clientEngine.getThisUuid());
                    if (this.logger.isFineEnabled()) {
                        this.logger.fine("Processing owner authentication with principal " + this.principal);
                    }
                    super.processMessage();
                    break;
                }
                this.sendClientMessage(this.prepareAuthenticatedClientMessage());
                break;
            }
            default: {
                throw new IllegalStateException("Unhandled authentication result");
            }
        }
    }

    private AuthenticationStatus authenticate() {
        if (this.endpoint.isAuthenticated()) {
            return AuthenticationStatus.AUTHENTICATED;
        }
        if (this.clientSerializationVersion != this.serializationService.getVersion()) {
            return AuthenticationStatus.SERIALIZATION_VERSION_MISMATCH;
        }
        if (!this.isOwnerConnection() && !this.isMember(this.principal)) {
            this.logger.warning("Member having UUID " + this.principal.getOwnerUuid() + " is not part of the cluster. Client Authentication rejected.");
            return AuthenticationStatus.CREDENTIALS_FAILED;
        }
        if (this.credentials == null) {
            this.logger.severe("Could not retrieve Credentials object!");
            return AuthenticationStatus.CREDENTIALS_FAILED;
        }
        if (this.partitionCount != null && this.clientEngine.getPartitionService().getPartitionCount() != this.partitionCount.intValue()) {
            this.logger.warning("Received auth from " + this.connection + " with principal " + this.principal + ",  authentication rejected because client has a different partition count. Partition count client expects :" + this.partitionCount + ", Member partition count:" + this.clientEngine.getPartitionService().getPartitionCount());
            return AuthenticationStatus.NOT_ALLOWED_IN_CLUSTER;
        }
        if (this.clusterId != null && !this.clientEngine.getClusterService().getClusterId().equals(this.clusterId)) {
            this.logger.warning("Received auth from " + this.connection + " with principal " + this.principal + ",  authentication rejected because client has a different cluster id. Cluster Id client expects :" + this.clusterId + ", Member partition count:" + this.clientEngine.getClusterService().getClusterId());
            return AuthenticationStatus.NOT_ALLOWED_IN_CLUSTER;
        }
        if (this.clientEngine.getSecurityContext() != null) {
            return this.authenticate(this.clientEngine.getSecurityContext());
        }
        if (this.credentials instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials)this.credentials;
            return this.authenticate(usernamePasswordCredentials);
        }
        this.logger.severe("Hazelcast security is disabled.\nUsernamePasswordCredentials or cluster group-name and group-password should be used for authentication!\nCurrent credentials type is: " + this.credentials.getClass().getName());
        return AuthenticationStatus.CREDENTIALS_FAILED;
    }

    private boolean isMember(ClientPrincipal principal) {
        return this.clientEngine.getClusterService().getMember(principal.getOwnerUuid()) != null;
    }

    private AuthenticationStatus authenticate(SecurityContext securityContext) {
        Connection connection = this.endpoint.getConnection();
        this.credentials.setEndpoint(connection.getInetAddress().getHostAddress());
        try {
            LoginContext lc = securityContext.createClientLoginContext(this.credentials);
            lc.login();
            this.endpoint.setLoginContext(lc);
            return AuthenticationStatus.AUTHENTICATED;
        }
        catch (LoginException e) {
            this.logger.warning(e);
            return AuthenticationStatus.CREDENTIALS_FAILED;
        }
    }

    private AuthenticationStatus authenticate(UsernamePasswordCredentials credentials) {
        GroupConfig groupConfig = this.nodeEngine.getConfig().getGroupConfig();
        String nodeGroupName = groupConfig.getName();
        boolean usernameMatch = nodeGroupName.equals(credentials.getUsername());
        return usernameMatch ? AuthenticationStatus.AUTHENTICATED : AuthenticationStatus.CREDENTIALS_FAILED;
    }

    private ClientMessage prepareUnauthenticatedClientMessage() {
        Connection connection = this.endpoint.getConnection();
        this.logger.warning("Received auth from " + connection + " with principal " + this.principal + ", authentication failed");
        byte status = AuthenticationStatus.CREDENTIALS_FAILED.getId();
        return this.encodeAuth(status, null, null, null, this.serializationService.getVersion(), null, this.clientEngine.getPartitionService().getPartitionCount(), this.clientEngine.getClusterService().getClusterId());
    }

    private ClientMessage prepareNotAllowedInCluster() {
        byte status = AuthenticationStatus.NOT_ALLOWED_IN_CLUSTER.getId();
        return this.encodeAuth(status, null, null, null, this.serializationService.getVersion(), null, this.clientEngine.getPartitionService().getPartitionCount(), this.clientEngine.getClusterService().getClusterId());
    }

    private ClientMessage prepareSerializationVersionMismatchClientMessage() {
        return this.encodeAuth(AuthenticationStatus.SERIALIZATION_VERSION_MISMATCH.getId(), null, null, null, this.serializationService.getVersion(), null, this.clientEngine.getPartitionService().getPartitionCount(), this.clientEngine.getClusterService().getClusterId());
    }

    private ClientMessage prepareAuthenticatedClientMessage() {
        Connection connection = this.endpoint.getConnection();
        this.endpoint.authenticated(this.principal, this.credentials, this.isOwnerConnection(), this.clientVersion, this.clientMessage.getCorrelationId(), this.clientName, this.labels);
        this.setConnectionType();
        if (!this.clientEngine.bind(this.endpoint)) {
            return this.prepareNotAllowedInCluster();
        }
        this.logger.info("Received auth from " + connection + ", successfully authenticated, principal: " + this.principal + ", owner connection: " + this.isOwnerConnection() + ", client version: " + this.clientVersion);
        Address thisAddress = this.clientEngine.getThisAddress();
        byte status = AuthenticationStatus.AUTHENTICATED.getId();
        return this.encodeAuth(status, thisAddress, this.principal.getUuid(), this.principal.getOwnerUuid(), this.serializationService.getVersion(), Collections.emptyList(), this.clientEngine.getPartitionService().getPartitionCount(), this.clientEngine.getClusterService().getClusterId());
    }

    private void setConnectionType() {
        String type = this.getClientType();
        if ("JVM".equals(type)) {
            this.connection.setType(ConnectionType.JAVA_CLIENT);
        } else if ("CSP".equals(type)) {
            this.connection.setType(ConnectionType.CSHARP_CLIENT);
        } else if ("CPP".equals(type)) {
            this.connection.setType(ConnectionType.CPP_CLIENT);
        } else if ("PYH".equals(type)) {
            this.connection.setType(ConnectionType.PYTHON_CLIENT);
        } else if ("RBY".equals(type)) {
            this.connection.setType(ConnectionType.RUBY_CLIENT);
        } else if ("NJS".equals(type)) {
            this.connection.setType(ConnectionType.NODEJS_CLIENT);
        } else if ("GOO".equals(type)) {
            this.connection.setType(ConnectionType.GO_CLIENT);
        } else {
            this.logger.info("Unknown client type: " + type);
            this.connection.setType(ConnectionType.BINARY_CLIENT);
        }
    }

    protected abstract ClientMessage encodeAuth(byte var1, Address var2, String var3, String var4, byte var5, List<Member> var6, int var7, String var8);

    protected abstract boolean isOwnerConnection();

    protected abstract String getClientType();

    private String getUuid() {
        if (this.principal != null) {
            return this.principal.getUuid();
        }
        return UuidUtil.createClientUuid(this.endpoint.getConnection().getEndPoint());
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

