/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.config.cp;

import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.config.matcher.MatchingPointConfigPatternMatcher;
import com.hazelcast.internal.config.ConfigUtils;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CPSubsystemConfig {
    public static final int DEFAULT_SESSION_TTL_SECONDS = (int)TimeUnit.MINUTES.toSeconds(5L);
    public static final int DEFAULT_HEARTBEAT_INTERVAL_SECONDS = 5;
    public static final int MIN_GROUP_SIZE = 3;
    public static final int MAX_GROUP_SIZE = 7;
    public static final int DEFAULT_MISSING_CP_MEMBER_AUTO_REMOVAL_SECONDS = (int)TimeUnit.HOURS.toSeconds(4L);
    private int cpMemberCount;
    private int groupSize;
    private int sessionTimeToLiveSeconds = DEFAULT_SESSION_TTL_SECONDS;
    private int sessionHeartbeatIntervalSeconds = 5;
    private int missingCPMemberAutoRemovalSeconds = DEFAULT_MISSING_CP_MEMBER_AUTO_REMOVAL_SECONDS;
    private boolean failOnIndeterminateOperationState;
    private RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig();
    private final Map<String, CPSemaphoreConfig> semaphoreConfigs = new ConcurrentHashMap<String, CPSemaphoreConfig>();
    private final Map<String, FencedLockConfig> lockConfigs = new ConcurrentHashMap<String, FencedLockConfig>();
    private final ConfigPatternMatcher configPatternMatcher = new MatchingPointConfigPatternMatcher();

    public CPSubsystemConfig() {
    }

    public CPSubsystemConfig(CPSubsystemConfig config) {
        this.cpMemberCount = config.cpMemberCount;
        this.groupSize = config.groupSize;
        this.raftAlgorithmConfig = new RaftAlgorithmConfig(config.raftAlgorithmConfig);
        this.sessionTimeToLiveSeconds = config.sessionTimeToLiveSeconds;
        this.sessionHeartbeatIntervalSeconds = config.sessionHeartbeatIntervalSeconds;
        this.failOnIndeterminateOperationState = config.failOnIndeterminateOperationState;
        this.missingCPMemberAutoRemovalSeconds = config.missingCPMemberAutoRemovalSeconds;
        for (CPSemaphoreConfig semaphoreConfig : config.semaphoreConfigs.values()) {
            this.semaphoreConfigs.put(semaphoreConfig.getName(), new CPSemaphoreConfig(semaphoreConfig));
        }
        for (FencedLockConfig lockConfig : config.lockConfigs.values()) {
            this.lockConfigs.put(lockConfig.getName(), new FencedLockConfig(lockConfig));
        }
    }

    public int getCPMemberCount() {
        return this.cpMemberCount;
    }

    public CPSubsystemConfig setCPMemberCount(int cpMemberCount) {
        Preconditions.checkTrue(cpMemberCount == 0 || cpMemberCount >= 3, "CP subsystem must have at least 3 CP members");
        this.cpMemberCount = cpMemberCount;
        return this;
    }

    public int getGroupSize() {
        if (this.groupSize > 0 || this.cpMemberCount == 0) {
            return this.groupSize;
        }
        int groupSize = this.cpMemberCount;
        if (groupSize % 2 == 0) {
            --groupSize;
        }
        return Math.min(groupSize, 7);
    }

    @SuppressFBWarnings(value={"IM_BAD_CHECK_FOR_ODD"}, justification="It's obvious that groupSize is not negative.")
    public CPSubsystemConfig setGroupSize(int groupSize) {
        Preconditions.checkTrue(groupSize == 0 || groupSize >= 3 && groupSize <= 7 && groupSize % 2 == 1, "Group size must be an odd value between 3 and 7");
        this.groupSize = groupSize;
        return this;
    }

    public int getSessionTimeToLiveSeconds() {
        return this.sessionTimeToLiveSeconds;
    }

    public CPSubsystemConfig setSessionTimeToLiveSeconds(int sessionTimeToLiveSeconds) {
        Preconditions.checkPositive(sessionTimeToLiveSeconds, "Session TTL must be a positive value!");
        this.sessionTimeToLiveSeconds = sessionTimeToLiveSeconds;
        return this;
    }

    public int getSessionHeartbeatIntervalSeconds() {
        return this.sessionHeartbeatIntervalSeconds;
    }

    public CPSubsystemConfig setSessionHeartbeatIntervalSeconds(int sessionHeartbeatIntervalSeconds) {
        Preconditions.checkPositive(this.sessionTimeToLiveSeconds, "Session heartbeat interval must be a positive value!");
        this.sessionHeartbeatIntervalSeconds = sessionHeartbeatIntervalSeconds;
        return this;
    }

    public int getMissingCPMemberAutoRemovalSeconds() {
        return this.missingCPMemberAutoRemovalSeconds;
    }

    public CPSubsystemConfig setMissingCPMemberAutoRemovalSeconds(int missingCPMemberAutoRemovalSeconds) {
        Preconditions.checkTrue(missingCPMemberAutoRemovalSeconds >= 0, "missing cp member auto-removal seconds must be non-negative");
        this.missingCPMemberAutoRemovalSeconds = missingCPMemberAutoRemovalSeconds;
        return this;
    }

    public boolean isFailOnIndeterminateOperationState() {
        return this.failOnIndeterminateOperationState;
    }

    public CPSubsystemConfig setFailOnIndeterminateOperationState(boolean failOnIndeterminateOperationState) {
        this.failOnIndeterminateOperationState = failOnIndeterminateOperationState;
        return this;
    }

    public RaftAlgorithmConfig getRaftAlgorithmConfig() {
        return this.raftAlgorithmConfig;
    }

    public CPSubsystemConfig setRaftAlgorithmConfig(RaftAlgorithmConfig raftAlgorithmConfig) {
        Preconditions.checkNotNull(raftAlgorithmConfig);
        this.raftAlgorithmConfig = raftAlgorithmConfig;
        return this;
    }

    public Map<String, CPSemaphoreConfig> getSemaphoreConfigs() {
        return this.semaphoreConfigs;
    }

    public CPSemaphoreConfig findSemaphoreConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.semaphoreConfigs, StringPartitioningStrategy.getBaseName(name));
    }

    public CPSubsystemConfig addSemaphoreConfig(CPSemaphoreConfig cpSemaphoreConfig) {
        this.semaphoreConfigs.put(cpSemaphoreConfig.getName(), cpSemaphoreConfig);
        return this;
    }

    public CPSubsystemConfig setSemaphoreConfigs(Map<String, CPSemaphoreConfig> cpSemaphoreConfigs) {
        this.semaphoreConfigs.clear();
        this.semaphoreConfigs.putAll(cpSemaphoreConfigs);
        for (Map.Entry<String, CPSemaphoreConfig> entry : this.semaphoreConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, FencedLockConfig> getLockConfigs() {
        return this.lockConfigs;
    }

    public FencedLockConfig findLockConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.lockConfigs, StringPartitioningStrategy.getBaseName(name));
    }

    public CPSubsystemConfig addLockConfig(FencedLockConfig lockConfig) {
        this.lockConfigs.put(lockConfig.getName(), lockConfig);
        return this;
    }

    public CPSubsystemConfig setLockConfigs(Map<String, FencedLockConfig> lockConfigs) {
        this.lockConfigs.clear();
        this.lockConfigs.putAll(lockConfigs);
        for (Map.Entry<String, FencedLockConfig> entry : this.lockConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public String toString() {
        return "CPSubsystemConfig{cpMemberCount=" + this.cpMemberCount + ", groupSize=" + this.groupSize + ", sessionTimeToLiveSeconds=" + this.sessionTimeToLiveSeconds + ", sessionHeartbeatIntervalSeconds=" + this.sessionHeartbeatIntervalSeconds + ", missingCPMemberAutoRemovalSeconds=" + this.missingCPMemberAutoRemovalSeconds + ", failOnIndeterminateOperationState=" + this.failOnIndeterminateOperationState + ", raftAlgorithmConfig=" + this.raftAlgorithmConfig + ", semaphoreConfigs=" + this.semaphoreConfigs + ", lockConfigs=" + this.lockConfigs + '}';
    }
}

