/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import java.util.Map;

class DynamicCPSubsystemConfig
extends CPSubsystemConfig {
    DynamicCPSubsystemConfig(CPSubsystemConfig config) {
        super(config);
    }

    @Override
    public CPSubsystemConfig setCPMemberCount(int cpMemberCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setGroupSize(int groupSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setSessionTimeToLiveSeconds(int sessionTimeToLiveSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setSessionHeartbeatIntervalSeconds(int sessionHeartbeatIntervalSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setMissingCPMemberAutoRemovalSeconds(int missingCPMemberAutoRemovalSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RaftAlgorithmConfig getRaftAlgorithmConfig() {
        return new DynamicRaftAlgorithmConfig(super.getRaftAlgorithmConfig());
    }

    @Override
    public CPSubsystemConfig setRaftAlgorithmConfig(RaftAlgorithmConfig raftAlgorithmConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig addSemaphoreConfig(CPSemaphoreConfig cpSemaphoreConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setSemaphoreConfigs(Map<String, CPSemaphoreConfig> cpSemaphoreConfigs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig addLockConfig(FencedLockConfig lockConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CPSubsystemConfig setLockConfigs(Map<String, FencedLockConfig> lockConfigs) {
        throw new UnsupportedOperationException();
    }

    static class DynamicRaftAlgorithmConfig
    extends RaftAlgorithmConfig {
        DynamicRaftAlgorithmConfig(RaftAlgorithmConfig config) {
            super(config);
        }

        @Override
        public RaftAlgorithmConfig setLeaderElectionTimeoutInMillis(long leaderElectionTimeoutInMillis) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RaftAlgorithmConfig setLeaderHeartbeatPeriodInMillis(long leaderHeartbeatPeriodInMillis) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RaftAlgorithmConfig setAppendRequestMaxEntryCount(int appendRequestMaxEntryCount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RaftAlgorithmConfig setCommitIndexAdvanceCountToSnapshot(int commitIndexAdvanceCountToSnapshot) {
            throw new UnsupportedOperationException();
        }

        @Override
        public RaftAlgorithmConfig setUncommittedEntryCountToRejectNewAppends(int uncommittedEntryCountToRejectNewAppends) {
            throw new UnsupportedOperationException();
        }
    }
}

