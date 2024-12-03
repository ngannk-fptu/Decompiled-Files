/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.cache.impl.JCacheDetector;
import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointImpl;
import com.hazelcast.client.impl.ClientEndpointManager;
import com.hazelcast.client.impl.ClientEndpointManagerImpl;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.ClientEvent;
import com.hazelcast.client.impl.ClientEventType;
import com.hazelcast.client.impl.ClientHeartbeatMonitor;
import com.hazelcast.client.impl.ClientPartitionListenerService;
import com.hazelcast.client.impl.ClientSelector;
import com.hazelcast.client.impl.ClientSelectors;
import com.hazelcast.client.impl.CompositeMessageTaskFactory;
import com.hazelcast.client.impl.operations.ClientDisconnectionOperation;
import com.hazelcast.client.impl.operations.GetConnectedClientsOperation;
import com.hazelcast.client.impl.operations.OnJoinClientOperation;
import com.hazelcast.client.impl.protocol.ClientExceptions;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.MessageTaskFactory;
import com.hazelcast.client.impl.protocol.task.AuthenticationCustomCredentialsMessageTask;
import com.hazelcast.client.impl.protocol.task.AuthenticationMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.client.impl.protocol.task.GetPartitionsMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.client.impl.protocol.task.PingMessageTask;
import com.hazelcast.client.impl.protocol.task.TransactionalMessageTask;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapQueryMessageTask;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.ClientType;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.internal.util.executor.UnblockablePoolExecutorThreadFactory;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.executor.ExecutorType;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.security.auth.login.LoginException;

public class ClientEngineImpl
implements ClientEngine,
CoreService,
PreJoinAwareService,
ManagedService,
MembershipAwareService,
EventPublishingService<ClientEvent, ClientListener> {
    public static final String SERVICE_NAME = "hz:core:clientEngine";
    private static final int EXECUTOR_QUEUE_CAPACITY_PER_CORE = 100000;
    private static final int BLOCKING_THREADS_PER_CORE = 20;
    private static final int THREADS_PER_CORE = 1;
    private static final int QUERY_THREADS_PER_CORE = 1;
    private static final ConstructorFunction<String, AtomicLong> LAST_AUTH_CORRELATION_ID_CONSTRUCTOR_FUNC = new ConstructorFunction<String, AtomicLong>(){

        @Override
        public AtomicLong createNew(String arg) {
            return new AtomicLong();
        }
    };
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final Executor executor;
    private final Executor blockingExecutor;
    private final ExecutorService clientManagementExecutor;
    private final Executor queryExecutor;
    private final ConcurrentMap<String, String> ownershipMappings = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, AtomicLong> lastAuthenticationCorrelationIds = new ConcurrentHashMap<String, AtomicLong>();
    private final Map<Address, Address> clientMemberAddressMap = new ConcurrentHashMap<Address, Address>();
    private volatile ClientSelector clientSelector = ClientSelectors.any();
    private final ClientEndpointManagerImpl endpointManager;
    private final ILogger logger;
    private final ConnectionListener connectionListener = new ConnectionListenerImpl();
    private final MessageTaskFactory messageTaskFactory;
    private final ClientExceptions clientExceptions;
    private final int endpointRemoveDelaySeconds;
    private final ClientPartitionListenerService partitionListenerService;
    private final boolean advancedNetworkConfigEnabled;

    public ClientEngineImpl(Node node) {
        this.logger = node.getLogger(ClientEngine.class);
        this.node = node;
        this.nodeEngine = node.nodeEngine;
        this.endpointManager = new ClientEndpointManagerImpl(this.nodeEngine);
        this.executor = this.newClientExecutor();
        this.queryExecutor = this.newClientQueryExecutor();
        this.blockingExecutor = this.newBlockingExecutor();
        this.clientManagementExecutor = this.newClientsManagementExecutor();
        this.messageTaskFactory = new CompositeMessageTaskFactory(this.nodeEngine);
        this.clientExceptions = this.initClientExceptionFactory();
        this.endpointRemoveDelaySeconds = node.getProperties().getInteger(GroupProperty.CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS);
        this.partitionListenerService = new ClientPartitionListenerService(this.nodeEngine);
        this.advancedNetworkConfigEnabled = node.getConfig().getAdvancedNetworkConfig().isEnabled();
    }

    private ClientExceptions initClientExceptionFactory() {
        boolean jcacheAvailable = JCacheDetector.isJCacheAvailable(this.nodeEngine.getConfigClassLoader());
        return new ClientExceptions(jcacheAvailable);
    }

    private ExecutorService newClientsManagementExecutor() {
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        return executionService.register("hz:client-management", 1, Integer.MAX_VALUE, ExecutorType.CACHED);
    }

    public ExecutorService getClientManagementExecutor() {
        return this.clientManagementExecutor;
    }

    private Executor newClientExecutor() {
        boolean userCodeDeploymentEnabled = this.nodeEngine.getConfig().getUserCodeDeploymentConfig().isEnabled();
        int threadsPerCore = userCodeDeploymentEnabled ? 20 : 1;
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        int coreSize = RuntimeAvailableProcessors.get();
        int threadCount = this.node.getProperties().getInteger(GroupProperty.CLIENT_ENGINE_THREAD_COUNT);
        if (threadCount <= 0) {
            threadCount = coreSize * threadsPerCore;
        }
        this.logger.finest("Creating new client executor with threadCount=" + threadCount);
        if (userCodeDeploymentEnabled) {
            return executionService.register("hz:client", threadCount, coreSize * 100000, ExecutorType.CONCRETE);
        }
        String name = "hz:client";
        ClassLoader classLoader = this.nodeEngine.getConfigClassLoader();
        String hzName = this.nodeEngine.getHazelcastInstance().getName();
        String internalName = name.substring("hz:".length());
        String threadNamePrefix = ThreadUtil.createThreadPoolName(hzName, internalName);
        UnblockablePoolExecutorThreadFactory factory = new UnblockablePoolExecutorThreadFactory(threadNamePrefix, classLoader);
        return executionService.register("hz:client", threadCount, coreSize * 100000, factory);
    }

    private Executor newClientQueryExecutor() {
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        int coreSize = RuntimeAvailableProcessors.get();
        int threadCount = this.node.getProperties().getInteger(GroupProperty.CLIENT_ENGINE_QUERY_THREAD_COUNT);
        if (threadCount <= 0) {
            threadCount = coreSize * 1;
        }
        this.logger.finest("Creating new client query executor with threadCount=" + threadCount);
        return executionService.register("hz:client-query", threadCount, coreSize * 100000, ExecutorType.CONCRETE);
    }

    private Executor newBlockingExecutor() {
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        int coreSize = Runtime.getRuntime().availableProcessors();
        int threadCount = this.node.getProperties().getInteger(GroupProperty.CLIENT_ENGINE_BLOCKING_THREAD_COUNT);
        if (threadCount <= 0) {
            threadCount = coreSize * 20;
        }
        this.logger.finest("Creating new client executor for blocking tasks with threadCount=" + threadCount);
        return executionService.register("hz:client-blocking-tasks", threadCount, coreSize * 100000, ExecutorType.CONCRETE);
    }

    @Override
    public int getClientEndpointCount() {
        return this.endpointManager.size();
    }

    @Override
    public void accept(ClientMessage clientMessage) {
        int partitionId = clientMessage.getPartitionId();
        Connection connection = clientMessage.getConnection();
        MessageTask messageTask = this.messageTaskFactory.create(clientMessage, connection);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        if (partitionId < 0) {
            if (this.isUrgent(messageTask)) {
                operationService.execute(new PriorityPartitionSpecificRunnable(messageTask));
            } else if (this.isQuery(messageTask)) {
                this.queryExecutor.execute(messageTask);
            } else if (messageTask instanceof TransactionalMessageTask) {
                this.blockingExecutor.execute(messageTask);
            } else if (messageTask instanceof BlockingMessageTask) {
                this.blockingExecutor.execute(messageTask);
            } else if (messageTask instanceof ListenerMessageTask) {
                this.blockingExecutor.execute(messageTask);
            } else {
                this.executor.execute(messageTask);
            }
        } else {
            operationService.execute(messageTask);
        }
    }

    private boolean isUrgent(MessageTask messageTask) {
        Class<?> clazz = messageTask.getClass();
        return clazz == PingMessageTask.class || clazz == GetPartitionsMessageTask.class || (clazz == AuthenticationMessageTask.class || clazz == AuthenticationCustomCredentialsMessageTask.class) && this.node.securityContext == null;
    }

    private boolean isQuery(MessageTask messageTask) {
        return messageTask instanceof AbstractMapQueryMessageTask;
    }

    @Override
    public IPartitionService getPartitionService() {
        return this.nodeEngine.getPartitionService();
    }

    @Override
    public ClusterService getClusterService() {
        return this.nodeEngine.getClusterService();
    }

    @Override
    public EventService getEventService() {
        return this.nodeEngine.getEventService();
    }

    @Override
    public ProxyService getProxyService() {
        return this.nodeEngine.getProxyService();
    }

    @Override
    public Address getThisAddress() {
        if (this.advancedNetworkConfigEnabled) {
            Address clientServerSocketAddress = this.node.getLocalMember().getAddressMap().get(EndpointQualifier.CLIENT);
            assert (clientServerSocketAddress != null);
            return clientServerSocketAddress;
        }
        return this.node.getThisAddress();
    }

    @Override
    public String getThisUuid() {
        return this.node.getThisUuid();
    }

    @Override
    public ILogger getLogger(Class clazz) {
        return this.node.getLogger(clazz);
    }

    @Override
    public ClientEndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    @Override
    public ClientExceptions getClientExceptions() {
        return this.clientExceptions;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return this.node.securityContext;
    }

    @Override
    public boolean bind(ClientEndpoint endpoint) {
        InetSocketAddress socketAddress;
        if (!this.clientSelector.select(endpoint)) {
            return false;
        }
        if (!this.endpointManager.registerEndpoint(endpoint)) {
            return true;
        }
        Connection conn = endpoint.getConnection();
        if (conn instanceof TcpIpConnection && (socketAddress = conn.getRemoteSocketAddress()) != null) {
            Address address = new Address(socketAddress);
            ((TcpIpConnection)conn).setEndPoint(address);
        }
        if (!this.clientSelector.select(endpoint)) {
            this.endpointManager.removeEndpoint(endpoint);
            return false;
        }
        return true;
    }

    @Override
    public void applySelector(ClientSelector newSelector) {
        this.logger.info("Applying a new client selector :" + newSelector);
        this.clientSelector = newSelector;
        for (ClientEndpoint endpoint : this.endpointManager.getEndpoints()) {
            if (this.clientSelector.select(endpoint)) continue;
            endpoint.getConnection().close("Client disconnected from cluster via Management Center", null);
        }
    }

    @Override
    public void dispatchEvent(ClientEvent event, ClientListener listener) {
        if (event.getEventType() == ClientEventType.CONNECTED) {
            listener.clientConnected(event);
        } else {
            listener.clientDisconnected(event);
        }
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
        if (this.advancedNetworkConfigEnabled) {
            Map<EndpointQualifier, Address> newMemberAddressMap = event.getMember().getAddressMap();
            Address memberAddress = newMemberAddressMap.get(EndpointQualifier.MEMBER);
            Address clientAddress = newMemberAddressMap.get(EndpointQualifier.CLIENT);
            if (clientAddress != null) {
                this.clientMemberAddressMap.put(clientAddress, memberAddress);
            }
        }
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        block4: {
            Address clientAddress;
            if (event.getMember().localMember()) {
                return;
            }
            if (this.advancedNetworkConfigEnabled && (clientAddress = event.getMember().getAddressMap().get(EndpointQualifier.CLIENT)) != null) {
                this.clientMemberAddressMap.remove(clientAddress);
            }
            String deadMemberUuid = event.getMember().getUuid();
            try {
                this.nodeEngine.getExecutionService().schedule(new DestroyEndpointTask(deadMemberUuid), this.endpointRemoveDelaySeconds, TimeUnit.SECONDS);
            }
            catch (RejectedExecutionException e) {
                if (!this.logger.isFinestEnabled()) break block4;
                this.logger.finest(e);
            }
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    @Override
    public Collection<Client> getClients() {
        Collection<ClientEndpoint> endpoints = this.endpointManager.getEndpoints();
        Set<Client> clients = SetUtil.createHashSet(endpoints.size());
        clients.addAll(endpoints);
        return clients;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.node.getEndpointManager(EndpointQualifier.CLIENT).addConnectionListener(this.connectionListener);
        ClientHeartbeatMonitor heartbeatMonitor = new ClientHeartbeatMonitor(this.endpointManager, this.getLogger(ClientHeartbeatMonitor.class), nodeEngine.getExecutionService(), this.node.getProperties());
        heartbeatMonitor.start();
    }

    @Override
    public void reset() {
        this.clear("Resetting clientEngine");
    }

    @Override
    public void shutdown(boolean terminate) {
        this.clear("Shutting down clientEngine");
    }

    private void clear(String reason) {
        for (ClientEndpoint ce : this.endpointManager.getEndpoints()) {
            ClientEndpointImpl endpoint = (ClientEndpointImpl)ce;
            try {
                endpoint.destroy();
            }
            catch (LoginException e) {
                this.logger.finest(e.getMessage());
            }
            try {
                Connection conn = endpoint.getConnection();
                if (!conn.isAlive()) continue;
                conn.close(reason, null);
            }
            catch (Exception e) {
                this.logger.finest(e);
            }
        }
        this.endpointManager.clear();
        this.ownershipMappings.clear();
    }

    public boolean trySetLastAuthenticationCorrelationId(String clientUuid, long newCorrelationId) {
        AtomicLong lastCorrelationId = ConcurrencyUtil.getOrPutIfAbsent(this.lastAuthenticationCorrelationIds, clientUuid, LAST_AUTH_CORRELATION_ID_CONSTRUCTOR_FUNC);
        return ConcurrencyUtil.setIfEqualOrGreaterThan(lastCorrelationId, newCorrelationId);
    }

    public String addOwnershipMapping(String clientUuid, String ownerUuid) {
        return this.ownershipMappings.put(clientUuid, ownerUuid);
    }

    public boolean removeOwnershipMapping(String clientUuid, String memberUuid) {
        this.lastAuthenticationCorrelationIds.remove(clientUuid);
        return this.ownershipMappings.remove(clientUuid, memberUuid);
    }

    @Override
    public String getOwnerUuid(String clientUuid) {
        return (String)this.ownershipMappings.get(clientUuid);
    }

    @Override
    public TransactionManagerService getTransactionManagerService() {
        return this.node.nodeEngine.getTransactionManagerService();
    }

    @Override
    public ClientPartitionListenerService getPartitionListenerService() {
        return this.partitionListenerService;
    }

    @Override
    public boolean isClientAllowed(Client client) {
        return this.clientSelector.select(client);
    }

    @Override
    public Operation getPreJoinOperation() {
        Set<Member> members = this.nodeEngine.getClusterService().getMembers();
        HashSet<String> liveMemberUUIDs = new HashSet<String>();
        for (Member member : members) {
            liveMemberUUIDs.add(member.getUuid());
        }
        HashMap<String, String> liveMappings = new HashMap<String, String>(this.ownershipMappings);
        liveMappings.values().retainAll(liveMemberUUIDs);
        return liveMappings.isEmpty() ? null : new OnJoinClientOperation(liveMappings);
    }

    @Override
    public Map<ClientType, Integer> getConnectedClientStats() {
        int numberOfCppClients = 0;
        int numberOfDotNetClients = 0;
        int numberOfJavaClients = 0;
        int numberOfNodeJSClients = 0;
        int numberOfPythonClients = 0;
        int numberOfGoClients = 0;
        int numberOfOtherClients = 0;
        InternalOperationService operationService = this.node.nodeEngine.getOperationService();
        HashMap clientsMap = new HashMap();
        for (Member member : this.node.getClusterService().getMembers()) {
            Address target = member.getAddress();
            GetConnectedClientsOperation clientInfoOperation = new GetConnectedClientsOperation();
            InternalCompletableFuture future = operationService.invokeOnTarget(SERVICE_NAME, clientInfoOperation, target);
            try {
                Map endpoints = (Map)future.get();
                if (endpoints == null) continue;
                for (Map.Entry entry : endpoints.entrySet()) {
                    clientsMap.put(entry.getKey(), entry.getValue());
                }
            }
            catch (Exception e) {
                this.logger.warning("Cannot get client information from: " + target.toString(), e);
            }
        }
        block12: for (ClientType clientType : clientsMap.values()) {
            switch (clientType) {
                case JAVA: {
                    ++numberOfJavaClients;
                    continue block12;
                }
                case CSHARP: {
                    ++numberOfDotNetClients;
                    continue block12;
                }
                case CPP: {
                    ++numberOfCppClients;
                    continue block12;
                }
                case NODEJS: {
                    ++numberOfNodeJSClients;
                    continue block12;
                }
                case PYTHON: {
                    ++numberOfPythonClients;
                    continue block12;
                }
                case GO: {
                    ++numberOfGoClients;
                    continue block12;
                }
            }
            ++numberOfOtherClients;
        }
        EnumMap<ClientType, Integer> resultMap = new EnumMap<ClientType, Integer>(ClientType.class);
        resultMap.put(ClientType.CPP, numberOfCppClients);
        resultMap.put(ClientType.CSHARP, numberOfDotNetClients);
        resultMap.put(ClientType.JAVA, numberOfJavaClients);
        resultMap.put(ClientType.NODEJS, numberOfNodeJSClients);
        resultMap.put(ClientType.PYTHON, numberOfPythonClients);
        resultMap.put(ClientType.GO, numberOfGoClients);
        resultMap.put(ClientType.OTHER, numberOfOtherClients);
        return resultMap;
    }

    @Override
    public Map<String, String> getClientStatistics() {
        Collection<ClientEndpoint> clientEndpoints = this.endpointManager.getEndpoints();
        HashMap<String, String> statsMap = new HashMap<String, String>(clientEndpoints.size());
        for (ClientEndpoint e : clientEndpoints) {
            String statistics = e.getClientStatistics();
            if (null == statistics) continue;
            statsMap.put(e.getUuid(), statistics);
        }
        return statsMap;
    }

    @Override
    public Address memberAddressOf(Address clientAddress) {
        if (!this.advancedNetworkConfigEnabled) {
            return clientAddress;
        }
        Address memberAddress = this.clientMemberAddressMap.get(clientAddress);
        if (memberAddress != null) {
            return memberAddress;
        }
        Set<Member> clusterMembers = this.node.getClusterService().getMembers();
        for (Member member : clusterMembers) {
            if (!member.getAddressMap().get(EndpointQualifier.CLIENT).equals(clientAddress)) continue;
            memberAddress = member.getAddress();
            this.clientMemberAddressMap.put(clientAddress, memberAddress);
            return memberAddress;
        }
        throw new TargetNotMemberException("Could not locate member with client address " + clientAddress);
    }

    @Override
    public Address clientAddressOf(Address memberAddress) {
        if (!this.advancedNetworkConfigEnabled) {
            return memberAddress;
        }
        MemberImpl member = this.node.getClusterService().getMember(memberAddress);
        if (member != null) {
            return member.getAddressMap().get(EndpointQualifier.CLIENT);
        }
        throw new TargetNotMemberException("Could not locate member with member address " + memberAddress);
    }

    private static class PriorityPartitionSpecificRunnable
    implements PartitionSpecificRunnable,
    UrgentSystemOperation {
        private final MessageTask task;

        public PriorityPartitionSpecificRunnable(MessageTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            this.task.run();
        }

        @Override
        public int getPartitionId() {
            return this.task.getPartitionId();
        }

        public String toString() {
            return "PriorityPartitionSpecificRunnable:{ " + this.task + "}";
        }
    }

    private class DestroyEndpointTask
    implements Runnable {
        private final String deadMemberUuid;

        DestroyEndpointTask(String deadMemberUuid) {
            this.deadMemberUuid = deadMemberUuid;
        }

        @Override
        public void run() {
            InternalOperationService service = ClientEngineImpl.this.nodeEngine.getOperationService();
            Address thisAddr = ClientEngineImpl.this.node.getThisAddress();
            for (Map.Entry entry : ClientEngineImpl.this.ownershipMappings.entrySet()) {
                String clientUuid = (String)entry.getKey();
                String memberUuid = (String)entry.getValue();
                if (!this.deadMemberUuid.equals(memberUuid)) continue;
                ClientDisconnectionOperation op = new ClientDisconnectionOperation(clientUuid, memberUuid);
                service.createInvocationBuilder(ClientEngineImpl.SERVICE_NAME, (Operation)op, thisAddr).invoke();
            }
        }
    }

    private final class ConnectionListenerImpl
    implements ConnectionListener {
        private ConnectionListenerImpl() {
        }

        @Override
        public void connectionAdded(Connection conn) {
        }

        @Override
        public void connectionRemoved(Connection connection) {
            block6: {
                if (!connection.isClient() || !ClientEngineImpl.this.nodeEngine.isRunning()) {
                    return;
                }
                ClientEndpointImpl endpoint = (ClientEndpointImpl)ClientEngineImpl.this.endpointManager.getEndpoint(connection);
                if (endpoint == null) {
                    ClientEngineImpl.this.logger.finest("connectionRemoved: No endpoint for connection:" + connection);
                    return;
                }
                ClientEngineImpl.this.endpointManager.removeEndpoint(endpoint);
                if (!endpoint.isOwnerConnection()) {
                    ClientEngineImpl.this.logger.finest("connectionRemoved: Not the owner conn:" + connection + " for endpoint " + endpoint);
                    return;
                }
                String localMemberUuid = ClientEngineImpl.this.node.getThisUuid();
                final String clientUuid = endpoint.getUuid();
                String ownerUuid = (String)ClientEngineImpl.this.ownershipMappings.get(clientUuid);
                if (localMemberUuid.equals(ownerUuid)) {
                    final long authenticationCorrelationId = endpoint.getAuthenticationCorrelationId();
                    try {
                        ClientEngineImpl.this.nodeEngine.getExecutionService().schedule(new Runnable(){

                            @Override
                            public void run() {
                                ConnectionListenerImpl.this.callDisconnectionOperation(clientUuid, authenticationCorrelationId);
                            }
                        }, ClientEngineImpl.this.endpointRemoveDelaySeconds, TimeUnit.SECONDS);
                    }
                    catch (RejectedExecutionException e) {
                        if (!ClientEngineImpl.this.logger.isFinestEnabled()) break block6;
                        ClientEngineImpl.this.logger.finest(e);
                    }
                }
            }
        }

        private void callDisconnectionOperation(String clientUuid, long authenticationCorrelationId) {
            String ownerMember;
            Set<Member> memberList = ClientEngineImpl.this.nodeEngine.getClusterService().getMembers();
            InternalOperationService operationService = ClientEngineImpl.this.nodeEngine.getOperationService();
            String memberUuid = ClientEngineImpl.this.getThisUuid();
            if (!memberUuid.equals(ownerMember = (String)ClientEngineImpl.this.ownershipMappings.get(clientUuid))) {
                return;
            }
            if (((AtomicLong)ClientEngineImpl.this.lastAuthenticationCorrelationIds.get(clientUuid)).get() > authenticationCorrelationId) {
                return;
            }
            for (Member member : memberList) {
                ClientDisconnectionOperation op = new ClientDisconnectionOperation(clientUuid, memberUuid);
                operationService.createInvocationBuilder(ClientEngineImpl.SERVICE_NAME, (Operation)op, member.getAddress()).invoke();
            }
        }
    }
}

