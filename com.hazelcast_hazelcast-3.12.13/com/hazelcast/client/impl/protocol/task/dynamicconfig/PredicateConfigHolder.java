/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.config.PredicateConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.serialization.SerializationService;

public class PredicateConfigHolder {
    private final String className;
    private final String sql;
    private final Data implementation;

    public PredicateConfigHolder(String className, String sql, Data implementation) {
        this.className = className;
        this.sql = sql;
        this.implementation = implementation;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSql() {
        return this.sql;
    }

    public Data getImplementation() {
        return this.implementation;
    }

    public PredicateConfig asPredicateConfig(SerializationService serializationService) {
        if (this.className != null) {
            return new PredicateConfig(this.className);
        }
        if (this.implementation != null) {
            Predicate predicate = (Predicate)serializationService.toObject(this.implementation);
            return new PredicateConfig(predicate);
        }
        return new PredicateConfig();
    }

    public static PredicateConfigHolder of(PredicateConfig config, SerializationService serializationService) {
        return new PredicateConfigHolder(config.getClassName(), config.getSql(), (Data)serializationService.toData(config.getImplementation()));
    }
}

