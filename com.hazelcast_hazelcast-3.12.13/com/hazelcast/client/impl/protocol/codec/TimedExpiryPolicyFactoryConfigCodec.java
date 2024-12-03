/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.CacheSimpleConfig;
import java.util.concurrent.TimeUnit;

public final class TimedExpiryPolicyFactoryConfigCodec {
    private TimedExpiryPolicyFactoryConfigCodec() {
    }

    public static CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig decode(ClientMessage clientMessage) {
        String expiryPolicyType = clientMessage.getStringUtf8();
        long duration = clientMessage.getLong();
        String timeUnit = clientMessage.getStringUtf8();
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig durationConfig = new CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig(duration, TimeUnit.valueOf(timeUnit));
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig config = new CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig(CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType.valueOf(expiryPolicyType), durationConfig);
        return config;
    }

    public static void encode(CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.getExpiryPolicyType().name()).set(config.getDurationConfig().getDurationAmount()).set(config.getDurationConfig().getTimeUnit().name());
    }

    public static int calculateDataSize(CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig config) {
        int dataSize = 8;
        dataSize += ParameterUtil.calculateDataSize(config.getExpiryPolicyType().name());
        return dataSize += ParameterUtil.calculateDataSize(config.getDurationConfig().getTimeUnit().name());
    }
}

