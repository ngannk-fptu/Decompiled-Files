/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumConfigBuilder;
import com.hazelcast.internal.cluster.fd.PhiAccrualClusterFailureDetector;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.spi.properties.GroupProperty;
import java.util.concurrent.TimeUnit;

public class ProbabilisticQuorumConfigBuilder
extends QuorumConfigBuilder {
    public static final double DEFAULT_PHI_THRESHOLD = Double.parseDouble(PhiAccrualClusterFailureDetector.HEARTBEAT_PHI_FAILURE_DETECTOR_THRESHOLD.getDefaultValue());
    public static final int DEFAULT_SAMPLE_SIZE = Integer.parseInt(PhiAccrualClusterFailureDetector.HEARTBEAT_PHI_FAILURE_DETECTOR_SAMPLE_SIZE.getDefaultValue());
    public static final long DEFAULT_MIN_STD_DEVIATION = Long.parseLong(PhiAccrualClusterFailureDetector.HEARTBEAT_PHI_FAILURE_DETECTOR_MIN_STD_DEV_MILLIS.getDefaultValue());
    public static final long DEFAULT_HEARTBEAT_PAUSE_MILLIS = TimeUnit.SECONDS.toMillis(Integer.parseInt(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getDefaultValue()));
    public static final long DEFAULT_HEARTBEAT_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(Integer.parseInt(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getDefaultValue()));
    private final String name;
    private double phiThreshold = DEFAULT_PHI_THRESHOLD;
    private int maxSampleSize = DEFAULT_SAMPLE_SIZE;
    private long minStdDeviationMillis = DEFAULT_MIN_STD_DEVIATION;
    private long acceptableHeartbeatPauseMillis = DEFAULT_HEARTBEAT_PAUSE_MILLIS;
    private long heartbeatIntervalMillis = DEFAULT_HEARTBEAT_INTERVAL_MILLIS;

    ProbabilisticQuorumConfigBuilder(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public ProbabilisticQuorumConfigBuilder withSuspicionThreshold(double suspicionThreshold) {
        this.phiThreshold = suspicionThreshold;
        return this;
    }

    public ProbabilisticQuorumConfigBuilder withMaxSampleSize(int maxSampleSize) {
        this.maxSampleSize = maxSampleSize;
        return this;
    }

    public ProbabilisticQuorumConfigBuilder withMinStdDeviationMillis(long minStdDeviationMillis) {
        this.minStdDeviationMillis = minStdDeviationMillis;
        return this;
    }

    public ProbabilisticQuorumConfigBuilder withAcceptableHeartbeatPauseMillis(long acceptableHeartbeatPauseMillis) {
        this.acceptableHeartbeatPauseMillis = acceptableHeartbeatPauseMillis;
        return this;
    }

    public ProbabilisticQuorumConfigBuilder withHeartbeatIntervalMillis(long heartbeatIntervalMillis) {
        this.heartbeatIntervalMillis = heartbeatIntervalMillis;
        return this;
    }

    @Override
    public QuorumConfig build() {
        ProbabilisticQuorumFunction quorumFunction = new ProbabilisticQuorumFunction(this.size, this.heartbeatIntervalMillis, this.acceptableHeartbeatPauseMillis, this.maxSampleSize, this.minStdDeviationMillis, this.phiThreshold);
        QuorumConfig config = new QuorumConfig();
        config.setName(this.name);
        config.setEnabled(this.enabled);
        config.setSize(this.size);
        config.setQuorumFunctionImplementation(quorumFunction);
        return config;
    }
}

