/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.cp;

import com.hazelcast.util.Preconditions;

public class RaftAlgorithmConfig {
    public static final long DEFAULT_LEADER_ELECTION_TIMEOUT_IN_MILLIS = 2000L;
    public static final long DEFAULT_LEADER_HEARTBEAT_PERIOD_IN_MILLIS = 5000L;
    public static final int DEFAULT_APPEND_REQUEST_MAX_ENTRY_COUNT = 100;
    public static final int DEFAULT_COMMIT_INDEX_ADVANCE_COUNT_TO_SNAPSHOT = 10000;
    public static final int DEFAULT_UNCOMMITTED_ENTRY_COUNT_TO_REJECT_NEW_APPENDS = 100;
    public static final int DEFAULT_MAX_MISSED_LEADER_HEARTBEAT_COUNT = 5;
    public static final long DEFAULT_APPEND_REQUEST_BACKOFF_TIMEOUT_IN_MILLIS = 100L;
    private long leaderElectionTimeoutInMillis = 2000L;
    private long leaderHeartbeatPeriodInMillis = 5000L;
    private int maxMissedLeaderHeartbeatCount = 5;
    private int appendRequestMaxEntryCount = 100;
    private int commitIndexAdvanceCountToSnapshot = 10000;
    private int uncommittedEntryCountToRejectNewAppends = 100;
    private long appendRequestBackoffTimeoutInMillis = 100L;

    public RaftAlgorithmConfig() {
    }

    public RaftAlgorithmConfig(RaftAlgorithmConfig config) {
        this.leaderElectionTimeoutInMillis = config.leaderElectionTimeoutInMillis;
        this.leaderHeartbeatPeriodInMillis = config.leaderHeartbeatPeriodInMillis;
        this.appendRequestMaxEntryCount = config.appendRequestMaxEntryCount;
        this.commitIndexAdvanceCountToSnapshot = config.commitIndexAdvanceCountToSnapshot;
        this.uncommittedEntryCountToRejectNewAppends = config.uncommittedEntryCountToRejectNewAppends;
        this.maxMissedLeaderHeartbeatCount = config.maxMissedLeaderHeartbeatCount;
        this.appendRequestBackoffTimeoutInMillis = config.appendRequestBackoffTimeoutInMillis;
    }

    public long getLeaderElectionTimeoutInMillis() {
        return this.leaderElectionTimeoutInMillis;
    }

    public RaftAlgorithmConfig setLeaderElectionTimeoutInMillis(long leaderElectionTimeoutInMillis) {
        Preconditions.checkPositive(leaderElectionTimeoutInMillis, "leader election timeout in millis: " + leaderElectionTimeoutInMillis + " must be positive!");
        this.leaderElectionTimeoutInMillis = leaderElectionTimeoutInMillis;
        return this;
    }

    public long getLeaderHeartbeatPeriodInMillis() {
        return this.leaderHeartbeatPeriodInMillis;
    }

    public RaftAlgorithmConfig setLeaderHeartbeatPeriodInMillis(long leaderHeartbeatPeriodInMillis) {
        Preconditions.checkPositive(leaderHeartbeatPeriodInMillis, "leader heartbeat period in millis: " + leaderHeartbeatPeriodInMillis + " must be positive!");
        this.leaderHeartbeatPeriodInMillis = leaderHeartbeatPeriodInMillis;
        return this;
    }

    public int getAppendRequestMaxEntryCount() {
        return this.appendRequestMaxEntryCount;
    }

    public RaftAlgorithmConfig setAppendRequestMaxEntryCount(int appendRequestMaxEntryCount) {
        Preconditions.checkPositive(appendRequestMaxEntryCount, "append request max entry count: " + appendRequestMaxEntryCount + " must be positive!");
        this.appendRequestMaxEntryCount = appendRequestMaxEntryCount;
        return this;
    }

    public int getCommitIndexAdvanceCountToSnapshot() {
        return this.commitIndexAdvanceCountToSnapshot;
    }

    public RaftAlgorithmConfig setCommitIndexAdvanceCountToSnapshot(int commitIndexAdvanceCountToSnapshot) {
        Preconditions.checkPositive(commitIndexAdvanceCountToSnapshot, "commit index advance count to snapshot: " + commitIndexAdvanceCountToSnapshot + " must be positive!");
        this.commitIndexAdvanceCountToSnapshot = commitIndexAdvanceCountToSnapshot;
        return this;
    }

    public int getUncommittedEntryCountToRejectNewAppends() {
        return this.uncommittedEntryCountToRejectNewAppends;
    }

    public RaftAlgorithmConfig setUncommittedEntryCountToRejectNewAppends(int uncommittedEntryCountToRejectNewAppends) {
        Preconditions.checkPositive(uncommittedEntryCountToRejectNewAppends, "uncommitted entry count to reject new appends: " + uncommittedEntryCountToRejectNewAppends + " must be positive!");
        this.uncommittedEntryCountToRejectNewAppends = uncommittedEntryCountToRejectNewAppends;
        return this;
    }

    public int getMaxMissedLeaderHeartbeatCount() {
        return this.maxMissedLeaderHeartbeatCount;
    }

    public RaftAlgorithmConfig setMaxMissedLeaderHeartbeatCount(int maxMissedLeaderHeartbeatCount) {
        Preconditions.checkPositive(maxMissedLeaderHeartbeatCount, "max missed leader heartbeat count must be positive!");
        this.maxMissedLeaderHeartbeatCount = maxMissedLeaderHeartbeatCount;
        return this;
    }

    public long getAppendRequestBackoffTimeoutInMillis() {
        return this.appendRequestBackoffTimeoutInMillis;
    }

    public RaftAlgorithmConfig setAppendRequestBackoffTimeoutInMillis(long appendRequestBackoffTimeoutInMillis) {
        Preconditions.checkPositive(appendRequestBackoffTimeoutInMillis, "append request backoff timeout must be positive!");
        this.appendRequestBackoffTimeoutInMillis = appendRequestBackoffTimeoutInMillis;
        return this;
    }
}

