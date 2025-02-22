/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.state;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.CandidateState;
import com.hazelcast.cp.internal.raft.impl.state.LeaderState;
import com.hazelcast.cp.internal.raft.impl.state.RaftGroupMembers;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class RaftState {
    private final Endpoint localEndpoint;
    private final CPGroupId groupId;
    private final Collection<Endpoint> initialMembers;
    private RaftGroupMembers committedGroupMembers;
    private RaftGroupMembers lastGroupMembers;
    private RaftRole role = RaftRole.FOLLOWER;
    private int term;
    private volatile Endpoint leader;
    private long commitIndex;
    private long lastApplied;
    private Endpoint votedFor;
    private int lastVoteTerm;
    private final RaftLog log;
    private LeaderState leaderState;
    private CandidateState preCandidateState;
    private CandidateState candidateState;

    public RaftState(CPGroupId groupId, Endpoint localEndpoint, Collection<Endpoint> endpoints, int logCapacity) {
        RaftGroupMembers groupMembers;
        this.groupId = groupId;
        this.localEndpoint = localEndpoint;
        this.initialMembers = Collections.unmodifiableSet(new LinkedHashSet<Endpoint>(endpoints));
        this.committedGroupMembers = groupMembers = new RaftGroupMembers(0L, endpoints, localEndpoint);
        this.lastGroupMembers = groupMembers;
        this.log = new RaftLog(logCapacity);
    }

    public String name() {
        return this.groupId.name();
    }

    public CPGroupId groupId() {
        return this.groupId;
    }

    public Collection<Endpoint> initialMembers() {
        return this.initialMembers;
    }

    public Collection<Endpoint> members() {
        return this.lastGroupMembers.members();
    }

    public Collection<Endpoint> remoteMembers() {
        return this.lastGroupMembers.remoteMembers();
    }

    public int memberCount() {
        return this.lastGroupMembers.memberCount();
    }

    public int majority() {
        return this.lastGroupMembers.majority();
    }

    public long membersLogIndex() {
        return this.lastGroupMembers.index();
    }

    public RaftGroupMembers committedGroupMembers() {
        return this.committedGroupMembers;
    }

    public RaftGroupMembers lastGroupMembers() {
        return this.lastGroupMembers;
    }

    public RaftRole role() {
        return this.role;
    }

    public int term() {
        return this.term;
    }

    int incrementTerm() {
        return ++this.term;
    }

    public Endpoint leader() {
        return this.leader;
    }

    public int lastVoteTerm() {
        return this.lastVoteTerm;
    }

    public Endpoint votedFor() {
        return this.votedFor;
    }

    public void leader(Endpoint endpoint) {
        this.leader = endpoint;
        if (endpoint != null) {
            this.preCandidateState = null;
        }
    }

    public long commitIndex() {
        return this.commitIndex;
    }

    public void commitIndex(long index) {
        assert (index >= this.commitIndex) : "new commit index: " + index + " is smaller than current commit index: " + this.commitIndex;
        this.commitIndex = index;
    }

    public long lastApplied() {
        return this.lastApplied;
    }

    public void lastApplied(long index) {
        assert (index >= this.lastApplied) : "new last applied: " + index + " is smaller than current last applied: " + this.lastApplied;
        this.lastApplied = index;
    }

    public RaftLog log() {
        return this.log;
    }

    public LeaderState leaderState() {
        return this.leaderState;
    }

    public CandidateState candidateState() {
        return this.candidateState;
    }

    public void persistVote(int term, Endpoint endpoint) {
        this.lastVoteTerm = term;
        this.votedFor = endpoint;
    }

    public void toFollower(int term) {
        this.role = RaftRole.FOLLOWER;
        this.leader = null;
        this.preCandidateState = null;
        this.leaderState = null;
        this.candidateState = null;
        this.term = term;
    }

    public VoteRequest toCandidate() {
        this.role = RaftRole.CANDIDATE;
        this.preCandidateState = null;
        this.leaderState = null;
        this.candidateState = new CandidateState(this.majority());
        this.candidateState.grantVote(this.localEndpoint);
        this.persistVote(this.incrementTerm(), this.localEndpoint);
        return new VoteRequest(this.localEndpoint, this.term, this.log.lastLogOrSnapshotTerm(), this.log.lastLogOrSnapshotIndex());
    }

    public void toLeader() {
        this.role = RaftRole.LEADER;
        this.leader(this.localEndpoint);
        this.preCandidateState = null;
        this.candidateState = null;
        this.leaderState = new LeaderState(this.lastGroupMembers.remoteMembers(), this.log.lastLogOrSnapshotIndex());
    }

    public boolean isKnownMember(Endpoint endpoint) {
        return this.lastGroupMembers.isKnownMember(endpoint);
    }

    public void initPreCandidateState() {
        this.preCandidateState = new CandidateState(this.majority());
        this.preCandidateState.grantVote(this.localEndpoint);
    }

    public void removePreCandidateState() {
        this.preCandidateState = null;
    }

    public CandidateState preCandidateState() {
        return this.preCandidateState;
    }

    public void updateGroupMembers(long logIndex, Collection<Endpoint> members) {
        assert (this.committedGroupMembers == this.lastGroupMembers) : "Cannot update group members to: " + members + " at log index: " + logIndex + " because last group members: " + this.lastGroupMembers + " is different than committed group members: " + this.committedGroupMembers;
        assert (this.lastGroupMembers.index() < logIndex) : "Cannot update group members to: " + members + " at log index: " + logIndex + " because last group members: " + this.lastGroupMembers + " has a bigger log index.";
        RaftGroupMembers newGroupMembers = new RaftGroupMembers(logIndex, members, this.localEndpoint);
        this.committedGroupMembers = this.lastGroupMembers;
        this.lastGroupMembers = newGroupMembers;
        if (this.leaderState != null) {
            for (Endpoint endpoint : members) {
                if (this.committedGroupMembers.isKnownMember(endpoint)) continue;
                this.leaderState.add(endpoint, this.log.lastLogOrSnapshotIndex());
            }
            for (Endpoint endpoint : this.committedGroupMembers.remoteMembers()) {
                if (members.contains(endpoint)) continue;
                this.leaderState.remove(endpoint);
            }
        }
    }

    public void commitGroupMembers() {
        assert (this.committedGroupMembers != this.lastGroupMembers) : "Cannot commit last group members: " + this.lastGroupMembers + " because it is same with committed group members";
        this.committedGroupMembers = this.lastGroupMembers;
    }

    public void resetGroupMembers() {
        assert (this.committedGroupMembers != this.lastGroupMembers);
        this.lastGroupMembers = this.committedGroupMembers;
    }

    public void restoreGroupMembers(long logIndex, Collection<Endpoint> members) {
        RaftGroupMembers groupMembers;
        assert (this.lastGroupMembers.index() <= logIndex) : "Cannot restore group members to: " + members + " at log index: " + logIndex + " because last group members: " + this.lastGroupMembers + " has a bigger log index.";
        this.committedGroupMembers = groupMembers = new RaftGroupMembers(logIndex, members, this.localEndpoint);
        this.lastGroupMembers = groupMembers;
    }
}

