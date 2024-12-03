/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicLong;

public final class AdvancedNetworkStats {
    private final EnumMap<ProtocolType, AtomicLong> bytesTransceived = new EnumMap(ProtocolType.class);

    public AdvancedNetworkStats() {
        for (ProtocolType type : ProtocolType.valuesAsSet()) {
            this.bytesTransceived.put(type, new AtomicLong());
        }
    }

    public void setBytesTransceivedForProtocol(ProtocolType protocolType, long bytes) {
        this.bytesTransceived.get((Object)protocolType).lazySet(bytes);
    }

    public long getBytesTransceivedForProtocol(ProtocolType protocolType) {
        return this.bytesTransceived.get((Object)protocolType).get();
    }

    public void registerMetrics(MetricsRegistry metricsRegistry, String prefix) {
        for (final ProtocolType type : ProtocolType.valuesAsSet()) {
            metricsRegistry.register(this, prefix + "." + type.name(), ProbeLevel.INFO, new LongProbeFunction<AdvancedNetworkStats>(){

                @Override
                public long get(AdvancedNetworkStats source) {
                    return ((AtomicLong)AdvancedNetworkStats.this.bytesTransceived.get((Object)type)).get();
                }
            });
        }
    }

    public String toString() {
        return "AdvancedNetworkStats{bytesTransceived=" + this.bytesTransceived + '}';
    }
}

