/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumConfigBuilder;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.spi.properties.GroupProperty;
import java.util.concurrent.TimeUnit;

public class RecentlyActiveQuorumConfigBuilder
extends QuorumConfigBuilder {
    public static final int DEFAULT_HEARTBEAT_TOLERANCE_MILLIS = (int)TimeUnit.SECONDS.toMillis(Integer.parseInt(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getDefaultValue()));
    private final String name;
    private final int size;
    private final int heartbeatToleranceMillis;

    RecentlyActiveQuorumConfigBuilder(String name, int size, int heartbeatToleranceMillis) {
        this.name = name;
        this.size = size;
        this.heartbeatToleranceMillis = heartbeatToleranceMillis;
    }

    @Override
    public QuorumConfig build() {
        RecentlyActiveQuorumFunction quorumFunction = new RecentlyActiveQuorumFunction(this.size, this.heartbeatToleranceMillis);
        QuorumConfig quorumConfig = new QuorumConfig(this.name, this.enabled, this.size);
        quorumConfig.setQuorumFunctionImplementation(quorumFunction);
        return quorumConfig;
    }
}

