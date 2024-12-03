/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.config.QuorumConfig;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.quorum.HeartbeatAware;
import com.hazelcast.quorum.PingAware;
import com.hazelcast.quorum.Quorum;
import com.hazelcast.quorum.QuorumEvent;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.quorum.impl.MemberCountQuorumFunction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.QuorumCheckAwareOperation;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;

public class QuorumImpl
implements Quorum {
    private final NodeEngineImpl nodeEngine;
    private final String quorumName;
    private final int size;
    private final QuorumConfig config;
    private final InternalEventService eventService;
    private final QuorumFunction quorumFunction;
    private final boolean heartbeatAwareQuorumFunction;
    private final boolean pingAwareQuorumFunction;
    private final boolean membershipListenerQuorumFunction;
    private volatile QuorumState quorumState = QuorumState.INITIAL;

    QuorumImpl(QuorumConfig config, NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.eventService = nodeEngine.getEventService();
        this.config = config;
        this.quorumName = config.getName();
        this.size = config.getSize();
        this.quorumFunction = this.initializeQuorumFunction();
        this.heartbeatAwareQuorumFunction = this.quorumFunction instanceof HeartbeatAware;
        this.membershipListenerQuorumFunction = this.quorumFunction instanceof MembershipListener;
        this.pingAwareQuorumFunction = this.quorumFunction instanceof PingAware;
    }

    void update(Collection<Member> members) {
        QuorumState previousQuorumState = this.quorumState;
        QuorumState newQuorumState = QuorumState.ABSENT;
        try {
            boolean present = this.quorumFunction.apply(members);
            newQuorumState = present ? QuorumState.PRESENT : QuorumState.ABSENT;
        }
        catch (Exception e) {
            ILogger logger = this.nodeEngine.getLogger(QuorumService.class);
            logger.severe("Quorum function of quorum: " + this.quorumName + " failed! Quorum status is set to " + (Object)((Object)newQuorumState), e);
        }
        if (previousQuorumState == QuorumState.INITIAL && newQuorumState != QuorumState.PRESENT) {
            return;
        }
        this.quorumState = newQuorumState;
        if (previousQuorumState == QuorumState.INITIAL) {
            return;
        }
        if (previousQuorumState != newQuorumState) {
            this.createAndPublishEvent(members, newQuorumState == QuorumState.PRESENT);
        }
    }

    void onHeartbeat(Member member, long timestamp) {
        if (!this.heartbeatAwareQuorumFunction) {
            return;
        }
        ((HeartbeatAware)((Object)this.quorumFunction)).onHeartbeat(member, timestamp);
    }

    void onPing(Member member, boolean successful) {
        if (!this.pingAwareQuorumFunction) {
            return;
        }
        PingAware pingAware = (PingAware)((Object)this.quorumFunction);
        if (successful) {
            pingAware.onPingRestored(member);
        } else {
            pingAware.onPingLost(member);
        }
    }

    void onMemberAdded(MembershipEvent event) {
        if (!this.membershipListenerQuorumFunction) {
            return;
        }
        ((MembershipListener)((Object)this.quorumFunction)).memberAdded(event);
    }

    void onMemberRemoved(MembershipEvent event) {
        if (!this.membershipListenerQuorumFunction) {
            return;
        }
        ((MembershipListener)((Object)this.quorumFunction)).memberRemoved(event);
    }

    public String getName() {
        return this.quorumName;
    }

    public int getSize() {
        return this.size;
    }

    public QuorumConfig getConfig() {
        return this.config;
    }

    @Override
    public boolean isPresent() {
        return this.quorumState == QuorumState.PRESENT;
    }

    boolean isHeartbeatAware() {
        return this.heartbeatAwareQuorumFunction;
    }

    boolean isPingAware() {
        return this.pingAwareQuorumFunction;
    }

    private boolean isQuorumNeeded(Operation op) {
        QuorumType type = this.config.getType();
        switch (type) {
            case WRITE: {
                return QuorumImpl.isWriteOperation(op) && QuorumImpl.shouldCheckQuorum(op);
            }
            case READ: {
                return QuorumImpl.isReadOperation(op) && QuorumImpl.shouldCheckQuorum(op);
            }
            case READ_WRITE: {
                return (QuorumImpl.isReadOperation(op) || QuorumImpl.isWriteOperation(op)) && QuorumImpl.shouldCheckQuorum(op);
            }
        }
        throw new IllegalStateException("Unhandled quorum type: " + (Object)((Object)type));
    }

    private static boolean isReadOperation(Operation op) {
        return op instanceof ReadonlyOperation;
    }

    private static boolean isWriteOperation(Operation op) {
        return op instanceof MutatingOperation;
    }

    private static boolean shouldCheckQuorum(Operation op) {
        return !(op instanceof QuorumCheckAwareOperation) || ((QuorumCheckAwareOperation)((Object)op)).shouldCheckQuorum();
    }

    void ensureQuorumPresent(Operation op) {
        if (!this.isQuorumNeeded(op)) {
            return;
        }
        this.ensureQuorumPresent();
    }

    void ensureQuorumPresent() {
        if (!this.isPresent()) {
            throw this.newQuorumException();
        }
    }

    private QuorumException newQuorumException() {
        throw new QuorumException("Split brain protection exception: " + this.quorumName + " has failed!");
    }

    private void createAndPublishEvent(Collection<Member> memberList, boolean presence) {
        QuorumEvent quorumEvent = new QuorumEvent(this.nodeEngine.getThisAddress(), this.size, memberList, presence);
        this.eventService.publishEvent("hz:impl:quorumService", this.quorumName, (Object)quorumEvent, quorumEvent.hashCode());
    }

    private QuorumFunction initializeQuorumFunction() {
        QuorumFunction quorumFunction = this.config.getQuorumFunctionImplementation();
        if (quorumFunction == null && this.config.getQuorumFunctionClassName() != null) {
            try {
                quorumFunction = (QuorumFunction)ClassLoaderUtil.newInstance(this.nodeEngine.getConfigClassLoader(), this.config.getQuorumFunctionClassName());
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        if (quorumFunction == null) {
            quorumFunction = new MemberCountQuorumFunction(this.size);
        }
        ManagedContext managedContext = this.nodeEngine.getSerializationService().getManagedContext();
        quorumFunction = (QuorumFunction)managedContext.initialize(quorumFunction);
        return quorumFunction;
    }

    public String toString() {
        return "QuorumImpl{quorumName='" + this.quorumName + '\'' + ", isPresent=" + this.isPresent() + ", size=" + this.size + ", config=" + this.config + ", quorumFunction=" + this.quorumFunction + '}';
    }

    private static enum QuorumState {
        INITIAL,
        PRESENT,
        ABSENT;

    }
}

