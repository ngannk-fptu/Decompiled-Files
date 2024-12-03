/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.quorum.HeartbeatAware;
import com.hazelcast.quorum.PingAware;
import com.hazelcast.quorum.Quorum;
import com.hazelcast.quorum.QuorumEvent;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.QuorumListener;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.quorum.impl.QuorumImpl;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.executor.ExecutorType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuorumServiceImpl
implements EventPublishingService<QuorumEvent, QuorumListener>,
MembershipAwareService,
QuorumService,
HeartbeatAware,
PingAware {
    public static final String SERVICE_NAME = "hz:impl:quorumService";
    private static final String QUORUM_EXECUTOR = "hz:quorum";
    private final NodeEngineImpl nodeEngine;
    private final EventService eventService;
    private volatile Map<String, QuorumImpl> quorums;
    private volatile boolean heartbeatAware;
    private volatile boolean pingAware;

    public QuorumServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.eventService = nodeEngine.getEventService();
    }

    public void start() {
        this.quorums = Collections.unmodifiableMap(this.initializeQuorums());
        this.scanQuorums();
        this.initializeListeners();
        if (this.isInactive()) {
            return;
        }
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.register(QUORUM_EXECUTOR, 1, Integer.MAX_VALUE, ExecutorType.CACHED);
        long heartbeatInterval = this.nodeEngine.getProperties().getSeconds(GroupProperty.HEARTBEAT_INTERVAL_SECONDS);
        executionService.scheduleWithRepetition(QUORUM_EXECUTOR, new UpdateQuorums(), heartbeatInterval, heartbeatInterval, TimeUnit.SECONDS);
    }

    private Map<String, QuorumImpl> initializeQuorums() {
        HashMap<String, QuorumImpl> quorums = new HashMap<String, QuorumImpl>();
        for (QuorumConfig quorumConfig : this.nodeEngine.getConfig().getQuorumConfigs().values()) {
            this.validateQuorumConfig(quorumConfig);
            if (!quorumConfig.isEnabled()) continue;
            QuorumImpl quorum = new QuorumImpl(quorumConfig, this.nodeEngine);
            quorums.put(quorumConfig.getName(), quorum);
        }
        return quorums;
    }

    private void validateQuorumConfig(QuorumConfig quorumConfig) {
        if (quorumConfig.getQuorumFunctionImplementation() == null) {
            return;
        }
        QuorumFunction quorumFunction = quorumConfig.getQuorumFunctionImplementation();
        if (quorumFunction instanceof ProbabilisticQuorumFunction) {
            this.validateQuorumParameters(quorumConfig.getName(), ((ProbabilisticQuorumFunction)quorumFunction).getAcceptableHeartbeatPauseMillis(), "acceptable heartbeat pause");
        } else if (quorumFunction instanceof RecentlyActiveQuorumFunction) {
            this.validateQuorumParameters(quorumConfig.getName(), ((RecentlyActiveQuorumFunction)quorumFunction).getHeartbeatToleranceMillis(), "heartbeat tolerance");
        }
    }

    private void validateQuorumParameters(String quorumName, long value, String parameterName) {
        HazelcastProperties nodeProperties = this.nodeEngine.getProperties();
        long maxNoHeartbeatMillis = nodeProperties.getMillis(GroupProperty.MAX_NO_HEARTBEAT_SECONDS);
        long heartbeatIntervalMillis = nodeProperties.getMillis(GroupProperty.HEARTBEAT_INTERVAL_SECONDS);
        if (value > maxNoHeartbeatMillis) {
            throw new ConfigurationException("This member is configured with maximum no-heartbeat duration " + maxNoHeartbeatMillis + " millis. For the quorum '" + quorumName + "' to be effective, set " + parameterName + " to a lower value. Currently configured value is " + value + ", reconfigure to a value lower than " + maxNoHeartbeatMillis + ".");
        }
        if (value < heartbeatIntervalMillis) {
            throw new ConfigurationException("Quorum '" + quorumName + "' is misconfigured: the value of acceptable heartbeat pause (" + value + ") must be greater than the configured heartbeat interval (" + heartbeatIntervalMillis + "), otherwise quorum will be always absent.");
        }
    }

    private void initializeListeners() {
        for (Map.Entry<String, QuorumConfig> configEntry : this.nodeEngine.getConfig().getQuorumConfigs().entrySet()) {
            QuorumConfig config = configEntry.getValue();
            String instanceName = configEntry.getKey();
            for (QuorumListenerConfig listenerConfig : config.getListenerConfigs()) {
                this.initializeListenerInternal(instanceName, listenerConfig);
            }
        }
    }

    private void initializeListenerInternal(String instanceName, QuorumListenerConfig listenerConfig) {
        QuorumListener listener = null;
        if (listenerConfig.getImplementation() != null) {
            listener = listenerConfig.getImplementation();
        } else if (listenerConfig.getClassName() != null) {
            try {
                listener = (QuorumListener)ClassLoaderUtil.newInstance(this.nodeEngine.getConfigClassLoader(), listenerConfig.getClassName());
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        if (listener != null) {
            this.addQuorumListener(instanceName, listener);
        }
    }

    private void scanQuorums() {
        for (QuorumImpl quorum : this.quorums.values()) {
            if (quorum.isHeartbeatAware()) {
                this.heartbeatAware = true;
            }
            if (!quorum.isPingAware()) continue;
            this.pingAware = true;
        }
    }

    private boolean isInactive() {
        return this.quorums.isEmpty();
    }

    public void addQuorumListener(String name, QuorumListener listener) {
        this.eventService.registerLocalListener(SERVICE_NAME, name, listener);
    }

    public void ensureQuorumPresent(Operation op) {
        if (this.isInactive()) {
            return;
        }
        QuorumImpl quorum = this.findQuorum(op);
        if (quorum == null) {
            return;
        }
        quorum.ensureQuorumPresent(op);
    }

    @Override
    public void ensureQuorumPresent(String quorumName, QuorumType requiredQuorumPermissionType) {
        if (this.isInactive() || quorumName == null) {
            return;
        }
        QuorumImpl definedQuorum = this.quorums.get(quorumName);
        if (definedQuorum == null) {
            return;
        }
        QuorumType definedQuorumType = definedQuorum.getConfig().getType();
        switch (requiredQuorumPermissionType) {
            case WRITE: {
                if (!definedQuorumType.equals((Object)QuorumType.WRITE) && !definedQuorumType.equals((Object)QuorumType.READ_WRITE)) break;
                definedQuorum.ensureQuorumPresent();
                break;
            }
            case READ: {
                if (!definedQuorumType.equals((Object)QuorumType.READ) && !definedQuorumType.equals((Object)QuorumType.READ_WRITE)) break;
                definedQuorum.ensureQuorumPresent();
                break;
            }
            case READ_WRITE: {
                if (!definedQuorumType.equals((Object)QuorumType.READ_WRITE)) break;
                definedQuorum.ensureQuorumPresent();
                break;
            }
            default: {
                throw new IllegalStateException("Unhandled quorum type: " + (Object)((Object)requiredQuorumPermissionType));
            }
        }
    }

    private QuorumImpl findQuorum(Operation op) {
        if (!this.isNamedOperation(op) || !this.isQuorumAware(op)) {
            return null;
        }
        String quorumName = this.getQuorumName(op);
        if (quorumName == null) {
            return null;
        }
        return this.quorums.get(quorumName);
    }

    private String getQuorumName(Operation op) {
        QuorumAwareService service;
        if (op instanceof ServiceNamespaceAware) {
            ServiceNamespace serviceNamespace = ((ServiceNamespaceAware)((Object)op)).getServiceNamespace();
            service = (QuorumAwareService)this.nodeEngine.getService(serviceNamespace.getServiceName());
        } else {
            service = (QuorumAwareService)op.getService();
        }
        String name = ((NamedOperation)((Object)op)).getName();
        return service.getQuorumName(name);
    }

    private boolean isQuorumAware(Operation op) {
        return op.getService() instanceof QuorumAwareService;
    }

    private boolean isNamedOperation(Operation op) {
        return op instanceof NamedOperation;
    }

    @Override
    public void dispatchEvent(QuorumEvent event, QuorumListener listener) {
        listener.onChange(event);
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
        if (this.isInactive() || event.getMember().localMember()) {
            return;
        }
        this.nodeEngine.getExecutionService().execute(QUORUM_EXECUTOR, new UpdateQuorums(event));
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        if (this.isInactive()) {
            return;
        }
        this.nodeEngine.getExecutionService().execute(QUORUM_EXECUTOR, new UpdateQuorums(event));
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    @Override
    public Quorum getQuorum(String quorumName) {
        Preconditions.checkNotNull(quorumName, "quorumName cannot be null!");
        Quorum quorum = this.quorums.get(quorumName);
        if (quorum == null) {
            throw new IllegalArgumentException("No quorum configuration named [ " + quorumName + " ] is found!");
        }
        return quorum;
    }

    @Override
    public void onHeartbeat(Member member, long timestamp) {
        if (this.isInactive() || !this.heartbeatAware) {
            return;
        }
        this.nodeEngine.getExecutionService().execute(QUORUM_EXECUTOR, new OnHeartbeat(member, timestamp));
    }

    @Override
    public void onPingLost(Member member) {
        if (this.isInactive() || !this.pingAware) {
            return;
        }
        this.nodeEngine.getExecutionService().execute(QUORUM_EXECUTOR, new OnPing(member, false));
    }

    @Override
    public void onPingRestored(Member member) {
        if (this.isInactive() || !this.pingAware) {
            return;
        }
        this.nodeEngine.getExecutionService().execute(QUORUM_EXECUTOR, new OnPing(member, true));
    }

    private class OnPing
    implements Runnable {
        private final Member member;
        private final boolean successful;

        OnPing(Member member, boolean successful) {
            this.member = member;
            this.successful = successful;
        }

        @Override
        public void run() {
            ClusterService clusterService = QuorumServiceImpl.this.nodeEngine.getClusterService();
            Collection<Member> members = clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
            for (QuorumImpl quorum : QuorumServiceImpl.this.quorums.values()) {
                quorum.onPing(this.member, this.successful);
                quorum.update(members);
            }
        }
    }

    private class OnHeartbeat
    implements Runnable {
        private final Member member;
        private final long timestamp;

        OnHeartbeat(Member member, long timestamp) {
            this.member = member;
            this.timestamp = timestamp;
        }

        @Override
        public void run() {
            ClusterService clusterService = QuorumServiceImpl.this.nodeEngine.getClusterService();
            Collection<Member> members = clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
            for (QuorumImpl quorum : QuorumServiceImpl.this.quorums.values()) {
                quorum.onHeartbeat(this.member, this.timestamp);
                quorum.update(members);
            }
        }
    }

    private class UpdateQuorums
    implements Runnable {
        private final MembershipEvent event;

        UpdateQuorums() {
            this.event = null;
        }

        UpdateQuorums(MembershipEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            ClusterService clusterService = QuorumServiceImpl.this.nodeEngine.getClusterService();
            Collection<Member> members = clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
            for (QuorumImpl quorum : QuorumServiceImpl.this.quorums.values()) {
                if (this.event != null) {
                    switch (this.event.getEventType()) {
                        case 1: {
                            quorum.onMemberAdded(this.event);
                            break;
                        }
                        case 2: {
                            quorum.onMemberRemoved(this.event);
                            break;
                        }
                    }
                }
                quorum.update(members);
            }
        }
    }
}

