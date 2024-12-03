/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointImpl;
import com.hazelcast.client.impl.ClientEndpointManager;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.StubAuthenticationException;
import com.hazelcast.client.impl.client.SecureRequest;
import com.hazelcast.client.impl.protocol.ClientExceptions;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.Credentials;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.ExceptionUtil;
import java.lang.reflect.Field;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractMessageTask<P>
implements MessageTask,
SecureRequest {
    private static final ExceptionUtil.ExceptionWrapper<Throwable> NOOP_WRAPPER = new ExceptionUtil.ExceptionWrapper<Throwable>(){

        @Override
        public Throwable create(Throwable throwable, String message) {
            return throwable;
        }
    };
    private static final List<Class<? extends Throwable>> NON_PEELABLE_EXCEPTIONS = Arrays.asList(Error.class, MemberLeftException.class);
    protected final ClientMessage clientMessage;
    protected final Connection connection;
    protected final ClientEndpoint endpoint;
    protected final NodeEngineImpl nodeEngine;
    protected final InternalSerializationService serializationService;
    protected final ILogger logger;
    protected final ClientEngine clientEngine;
    protected P parameters;
    final ClientEndpointManager endpointManager;
    private final Node node;

    protected AbstractMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        this.clientMessage = clientMessage;
        this.logger = node.getLogger(this.getClass());
        this.node = node;
        this.nodeEngine = node.nodeEngine;
        this.serializationService = node.getSerializationService();
        this.connection = connection;
        this.clientEngine = node.clientEngine;
        this.endpointManager = this.clientEngine.getEndpointManager();
        this.endpoint = this.initEndpoint();
    }

    public <S> S getService(String serviceName) {
        return (S)this.node.nodeEngine.getService(serviceName);
    }

    private ClientEndpoint initEndpoint() {
        ClientEndpoint endpoint = this.endpointManager.getEndpoint(this.connection);
        if (endpoint != null) {
            return endpoint;
        }
        return new ClientEndpointImpl(this.clientEngine, this.nodeEngine, this.connection);
    }

    protected abstract P decodeClientMessage(ClientMessage var1);

    protected abstract ClientMessage encodeResponse(Object var1);

    @Override
    public int getPartitionId() {
        return this.clientMessage.getPartitionId();
    }

    @Override
    public final void run() {
        try {
            if (this.requiresAuthentication() && !this.endpoint.isAuthenticated()) {
                this.handleAuthenticationFailure();
            } else {
                this.initializeAndProcessMessage();
            }
        }
        catch (Throwable e) {
            this.handleProcessingFailure(e);
        }
    }

    protected boolean requiresAuthentication() {
        return true;
    }

    private void initializeAndProcessMessage() throws Throwable {
        if (!this.node.getNodeExtension().isStartCompleted()) {
            throw new HazelcastInstanceNotActiveException("Hazelcast instance is not ready yet!");
        }
        this.parameters = this.decodeClientMessage(this.clientMessage);
        assert (this.addressesDecodedWithTranslation()) : this.formatWrongAddressInDecodedMessage();
        Credentials credentials = this.endpoint.getCredentials();
        this.interceptBefore(credentials);
        this.checkPermissions(this.endpoint);
        this.processMessage();
        this.interceptAfter(credentials);
    }

    private void handleAuthenticationFailure() {
        RuntimeException exception;
        if (this.nodeEngine.isRunning()) {
            String message = "Client " + this.endpoint + " must authenticate before any operation.";
            this.logger.severe(message);
            exception = new RetryableHazelcastException(new StubAuthenticationException(message));
        } else {
            exception = new HazelcastInstanceNotActiveException();
        }
        this.sendClientMessage(exception);
        this.connection.close("Authentication failed. " + exception.getMessage(), null);
    }

    private void logProcessingFailure(Throwable throwable) {
        if (this.logger.isFinestEnabled()) {
            if (this.parameters == null) {
                this.logger.finest(throwable.getMessage(), throwable);
            } else {
                this.logger.finest("While executing request: " + this.parameters + " -> " + throwable.getMessage(), throwable);
            }
        }
    }

    protected void handleProcessingFailure(Throwable throwable) {
        this.logProcessingFailure(throwable);
        this.sendClientMessage(throwable);
    }

    private void interceptBefore(Credentials credentials) {
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        String methodName = this.getMethodName();
        if (securityContext != null && methodName != null) {
            String objectType = this.getDistributedObjectType();
            String objectName = this.getDistributedObjectName();
            securityContext.interceptBefore(credentials, objectType, objectName, methodName, this.getParameters());
        }
    }

    private void interceptAfter(Credentials credentials) {
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        String methodName = this.getMethodName();
        if (securityContext != null && methodName != null) {
            String objectType = this.getDistributedObjectType();
            String objectName = this.getDistributedObjectName();
            securityContext.interceptAfter(credentials, objectType, objectName, methodName);
        }
    }

    private void checkPermissions(ClientEndpoint endpoint) {
        Permission permission;
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        if (securityContext != null && (permission = this.getRequiredPermission()) != null) {
            securityContext.checkPermission(endpoint.getSubject(), permission);
        }
    }

    protected abstract void processMessage() throws Throwable;

    protected void sendResponse(Object response) {
        try {
            ClientMessage clientMessage = this.encodeResponse(response);
            this.sendClientMessage(clientMessage);
        }
        catch (Exception e) {
            this.handleProcessingFailure(e);
        }
    }

    protected void sendClientMessage(ClientMessage resultClientMessage) {
        resultClientMessage.setCorrelationId(this.clientMessage.getCorrelationId());
        resultClientMessage.addFlag((short)192);
        resultClientMessage.setVersion((short)1);
        this.connection.write(resultClientMessage);
    }

    protected void sendClientMessage(Object key, ClientMessage resultClientMessage) {
        int partitionId = key == null ? -1 : this.nodeEngine.getPartitionService().getPartitionId(key);
        resultClientMessage.setPartitionId(partitionId);
        this.sendClientMessage(resultClientMessage);
    }

    protected void sendClientMessage(Throwable throwable) {
        ClientExceptions exceptionFactory = this.clientEngine.getClientExceptions();
        Throwable throwable1 = this.peelIfNeeded(throwable);
        ClientMessage exception = exceptionFactory.createExceptionMessage(throwable1);
        this.sendClientMessage(exception);
    }

    public abstract String getServiceName();

    @Override
    public String getDistributedObjectType() {
        return this.getServiceName();
    }

    @Override
    public abstract String getDistributedObjectName();

    @Override
    public abstract String getMethodName();

    @Override
    public abstract Object[] getParameters();

    protected final BuildInfo getMemberBuildInfo() {
        return this.node.getBuildInfo();
    }

    protected boolean isAdvancedNetworkEnabled() {
        return this.node.getConfig().getAdvancedNetworkConfig().isEnabled();
    }

    final boolean addressesDecodedWithTranslation() {
        if (!this.isAdvancedNetworkEnabled()) {
            return true;
        }
        Class<Address> addressClass = Address.class;
        Field[] fields = this.parameters.getClass().getDeclaredFields();
        HashSet<Address> addresses = new HashSet<Address>();
        try {
            for (Field field : fields) {
                if (!addressClass.isAssignableFrom(field.getType())) continue;
                addresses.add((Address)field.get(this.parameters));
            }
        }
        catch (IllegalAccessException e) {
            this.logger.info("Could not reflectively access parameter fields", e);
        }
        if (!addresses.isEmpty()) {
            Collection<Address> allMemberAddresses = this.node.clusterService.getMemberAddresses();
            for (Address address : addresses) {
                if (allMemberAddresses.contains(address)) continue;
                return false;
            }
        }
        return true;
    }

    final String formatWrongAddressInDecodedMessage() {
        return "Decoded message of type " + this.parameters.getClass() + " contains untranslated addresses. Use ClientEngine.memberAddressOf to translate addresses while decoding this client message.";
    }

    private Throwable peelIfNeeded(Throwable t) {
        if (t == null) {
            return null;
        }
        for (Class<? extends Throwable> clazz : NON_PEELABLE_EXCEPTIONS) {
            if (!clazz.isAssignableFrom(t.getClass())) continue;
            return t;
        }
        return ExceptionUtil.peel(t, null, null, NOOP_WRAPPER);
    }
}

