/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.logging.ILogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class RaftInvocationContext {
    private final ILogger logger;
    private final RaftService raftService;
    private final ConcurrentMap<CPGroupId, CPMember> knownLeaders = new ConcurrentHashMap<CPGroupId, CPMember>();
    private final boolean failOnIndeterminateOperationState;
    private AtomicReference<ActiveCPMembersContainer> membersContainer = new AtomicReference<Object>(null);

    public RaftInvocationContext(ILogger logger, RaftService raftService) {
        this.logger = logger;
        this.raftService = raftService;
        this.failOnIndeterminateOperationState = raftService.getConfig().isFailOnIndeterminateOperationState();
    }

    public void reset() {
        this.membersContainer.set(null);
        this.knownLeaders.clear();
    }

    public void setMembers(long groupIdSeed, long membersCommitIndex, Collection<CPMemberInfo> members) {
        ActiveCPMembersContainer currentContainer;
        ActiveCPMembersVersion version = new ActiveCPMembersVersion(groupIdSeed, membersCommitIndex);
        ActiveCPMembersContainer newContainer = new ActiveCPMembersContainer(version, members.toArray(new CPMemberInfo[0]));
        while ((currentContainer = this.membersContainer.get()) == null || newContainer.version.compareTo(currentContainer.version) > 0) {
            if (!this.membersContainer.compareAndSet(currentContainer, newContainer)) continue;
            return;
        }
    }

    CPMember getKnownLeader(CPGroupId groupId) {
        return (CPMember)this.knownLeaders.get(groupId);
    }

    boolean setKnownLeader(CPGroupId groupId, CPMember leader) {
        if (leader != null) {
            this.logger.fine("Setting known leader for raft: " + groupId + " to " + leader);
            this.knownLeaders.put(groupId, leader);
            return true;
        }
        return false;
    }

    void updateKnownLeaderOnFailure(CPGroupId groupId, Throwable cause) {
        if (cause instanceof CPSubsystemException) {
            CPSubsystemException e = (CPSubsystemException)cause;
            CPMember leader = (CPMember)e.getLeader();
            if (!this.setKnownLeader(groupId, leader)) {
                this.resetKnownLeader(groupId);
            }
        } else {
            this.resetKnownLeader(groupId);
        }
    }

    boolean shouldFailOnIndeterminateOperationState() {
        return this.failOnIndeterminateOperationState;
    }

    private void resetKnownLeader(CPGroupId groupId) {
        this.logger.fine("Resetting known leader for raft: " + groupId);
        this.knownLeaders.remove(groupId);
    }

    MemberCursor newMemberCursor(CPGroupId groupId) {
        CPGroupInfo group = this.raftService.getCPGroupLocally(groupId);
        if (group != null) {
            return new MemberCursor(group.membersArray());
        }
        ActiveCPMembersContainer container = this.membersContainer.get();
        CPMember[] members = container != null ? container.members : new CPMember[]{};
        return new MemberCursor(members);
    }

    @SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
    private static class ActiveCPMembersVersion
    implements Comparable<ActiveCPMembersVersion> {
        private final long groupIdSeed;
        private final long version;

        ActiveCPMembersVersion(long groupIdSeed, long version) {
            this.groupIdSeed = groupIdSeed;
            this.version = version;
        }

        @Override
        public int compareTo(@Nonnull ActiveCPMembersVersion other) {
            if (this.groupIdSeed < other.groupIdSeed) {
                return -1;
            }
            if (this.groupIdSeed > other.groupIdSeed) {
                return 1;
            }
            return this.version < other.version ? -1 : (this.version > other.version ? 1 : 0);
        }
    }

    private static class ActiveCPMembersContainer {
        final ActiveCPMembersVersion version;
        final CPMemberInfo[] members;

        ActiveCPMembersContainer(ActiveCPMembersVersion version, CPMemberInfo[] members) {
            this.version = version;
            this.members = members;
        }
    }

    static final class MemberCursor {
        private final CPMember[] members;
        private int index = -1;

        MemberCursor(CPMember[] members) {
            this.members = members;
        }

        boolean advance() {
            return ++this.index < this.members.length;
        }

        CPMember get() {
            return this.members[this.index];
        }
    }
}

