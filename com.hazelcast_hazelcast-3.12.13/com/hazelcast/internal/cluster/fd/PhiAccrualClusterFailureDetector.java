/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetector;
import com.hazelcast.internal.cluster.fd.FailureDetector;
import com.hazelcast.internal.cluster.fd.PhiAccrualFailureDetector;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PhiAccrualClusterFailureDetector
implements ClusterFailureDetector {
    public static final HazelcastProperty HEARTBEAT_PHI_FAILURE_DETECTOR_THRESHOLD = new HazelcastProperty("hazelcast.heartbeat.phiaccrual.failuredetector.threshold", 10);
    public static final HazelcastProperty HEARTBEAT_PHI_FAILURE_DETECTOR_SAMPLE_SIZE = new HazelcastProperty("hazelcast.heartbeat.phiaccrual.failuredetector.sample.size", 200);
    public static final HazelcastProperty HEARTBEAT_PHI_FAILURE_DETECTOR_MIN_STD_DEV_MILLIS = new HazelcastProperty("hazelcast.heartbeat.phiaccrual.failuredetector.min.std.dev.millis", 100, TimeUnit.MILLISECONDS);
    private final double phiThreshold;
    private final int maxSampleSize;
    private final long minStdDeviationMillis;
    private final long acceptableHeartbeatPauseMillis;
    private final long firstHeartbeatEstimateMillis;
    private final ConcurrentMap<Member, FailureDetector> failureDetectors = new ConcurrentHashMap<Member, FailureDetector>();
    private final ConstructorFunction<Member, FailureDetector> failureDetectorConstructor = new ConstructorFunction<Member, FailureDetector>(){

        @Override
        public FailureDetector createNew(Member arg) {
            return new PhiAccrualFailureDetector(PhiAccrualClusterFailureDetector.this.phiThreshold, PhiAccrualClusterFailureDetector.this.maxSampleSize, PhiAccrualClusterFailureDetector.this.minStdDeviationMillis, PhiAccrualClusterFailureDetector.this.acceptableHeartbeatPauseMillis, PhiAccrualClusterFailureDetector.this.firstHeartbeatEstimateMillis);
        }
    };

    public PhiAccrualClusterFailureDetector(long maxNoHeartbeatMillis, long heartbeatIntervalMillis, HazelcastProperties props) {
        this(maxNoHeartbeatMillis, heartbeatIntervalMillis, props.getDouble(HEARTBEAT_PHI_FAILURE_DETECTOR_THRESHOLD), props.getInteger(HEARTBEAT_PHI_FAILURE_DETECTOR_SAMPLE_SIZE), props.getMillis(HEARTBEAT_PHI_FAILURE_DETECTOR_MIN_STD_DEV_MILLIS));
    }

    public PhiAccrualClusterFailureDetector(long maxNoHeartbeatMillis, long heartbeatIntervalMillis, double phiThreshold, int maxSampleSize, long minStdDeviationMillis) {
        this.acceptableHeartbeatPauseMillis = maxNoHeartbeatMillis;
        this.firstHeartbeatEstimateMillis = heartbeatIntervalMillis;
        this.phiThreshold = phiThreshold;
        this.maxSampleSize = maxSampleSize;
        this.minStdDeviationMillis = minStdDeviationMillis;
    }

    @Override
    public void heartbeat(Member member, long timestamp) {
        FailureDetector fd = ConcurrencyUtil.getOrPutIfAbsent(this.failureDetectors, member, this.failureDetectorConstructor);
        fd.heartbeat(timestamp);
    }

    @Override
    public boolean isAlive(Member member, long timestamp) {
        FailureDetector fd = (FailureDetector)this.failureDetectors.get(member);
        return fd != null && fd.isAlive(timestamp);
    }

    @Override
    public long lastHeartbeat(Member member) {
        FailureDetector fd = (FailureDetector)this.failureDetectors.get(member);
        return fd != null ? fd.lastHeartbeat() : 0L;
    }

    @Override
    public double suspicionLevel(Member member, long timestamp) {
        FailureDetector fd = (FailureDetector)this.failureDetectors.get(member);
        return fd != null ? fd.suspicionLevel(timestamp) : this.phiThreshold;
    }

    @Override
    public void remove(Member member) {
        this.failureDetectors.remove(member);
    }

    @Override
    public void reset() {
        this.failureDetectors.clear();
    }
}

