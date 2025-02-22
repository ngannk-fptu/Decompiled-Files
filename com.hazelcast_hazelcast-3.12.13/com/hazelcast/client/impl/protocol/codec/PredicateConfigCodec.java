/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.PredicateConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;

public final class PredicateConfigCodec {
    private static final int ENCODED_BOOLEANS = 3;

    private PredicateConfigCodec() {
    }

    public static PredicateConfigHolder decode(ClientMessage clientMessage) {
        boolean isNullImplementation;
        boolean isNullSql;
        String className = null;
        String sql = null;
        Data implementation = null;
        boolean isNullClassName = clientMessage.getBoolean();
        if (!isNullClassName) {
            className = clientMessage.getStringUtf8();
        }
        if (!(isNullSql = clientMessage.getBoolean())) {
            sql = clientMessage.getStringUtf8();
        }
        if (!(isNullImplementation = clientMessage.getBoolean())) {
            implementation = clientMessage.getData();
        }
        return new PredicateConfigHolder(className, sql, implementation);
    }

    public static void encode(PredicateConfigHolder config, ClientMessage clientMessage) {
        boolean isNullClassName = config.getClassName() == null;
        boolean isNullSql = config.getSql() == null;
        boolean isNullImplementation = config.getImplementation() == null;
        clientMessage.set(isNullClassName);
        if (!isNullClassName) {
            clientMessage.set(config.getClassName());
        }
        clientMessage.set(isNullSql);
        if (!isNullSql) {
            clientMessage.set(config.getSql());
        }
        clientMessage.set(isNullImplementation);
        if (!isNullImplementation) {
            clientMessage.set(config.getImplementation());
        }
    }

    public static int calculateDataSize(PredicateConfigHolder config) {
        boolean hasClassName = config.getClassName() != null;
        boolean hasSql = config.getSql() != null;
        boolean hasImplementation = config.getImplementation() != null;
        int dataSize = 3;
        if (hasClassName) {
            dataSize += ParameterUtil.calculateDataSize(config.getClassName());
        }
        if (hasSql) {
            dataSize += ParameterUtil.calculateDataSize(config.getSql());
        }
        if (hasImplementation) {
            dataSize += ParameterUtil.calculateDataSize(config.getImplementation());
        }
        return dataSize;
    }
}

