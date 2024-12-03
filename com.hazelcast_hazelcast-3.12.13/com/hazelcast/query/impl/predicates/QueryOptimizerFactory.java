/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.impl.predicates.EmptyOptimizer;
import com.hazelcast.query.impl.predicates.QueryOptimizer;
import com.hazelcast.query.impl.predicates.RuleBasedQueryOptimizer;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.Arrays;

public final class QueryOptimizerFactory {
    private QueryOptimizerFactory() {
    }

    public static QueryOptimizer newOptimizer(HazelcastProperties properties) {
        Type type;
        HazelcastProperty property = GroupProperty.QUERY_OPTIMIZER_TYPE;
        String string = properties.getString(property);
        try {
            type = Type.valueOf(string);
        }
        catch (IllegalArgumentException e) {
            throw QueryOptimizerFactory.onInvalidOptimizerType(string);
        }
        switch (type) {
            case RULES: {
                return new RuleBasedQueryOptimizer();
            }
        }
        return new EmptyOptimizer();
    }

    private static IllegalArgumentException onInvalidOptimizerType(String type) {
        StringBuilder sb = new StringBuilder("Unknown Optimizer Type: ").append(type).append(". Use property '").append(GroupProperty.QUERY_OPTIMIZER_TYPE.getName()).append("' to select optimizer. ").append("Available optimizers: ");
        Type[] values = Type.values();
        sb.append(Arrays.toString((Object[])values));
        return new IllegalArgumentException(sb.toString());
    }

    public static enum Type {
        NONE,
        RULES;

    }
}

