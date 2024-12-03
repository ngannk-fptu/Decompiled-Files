/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyEmbeddedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMixedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyNoopImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public enum BuiltInPropertyAccessStrategies {
    BASIC("property", PropertyAccessStrategyBasicImpl.INSTANCE),
    FIELD("field", PropertyAccessStrategyFieldImpl.INSTANCE),
    MIXED("mixed", PropertyAccessStrategyMixedImpl.INSTANCE),
    MAP("map", PropertyAccessStrategyMapImpl.INSTANCE),
    EMBEDDED("embedded", PropertyAccessStrategyEmbeddedImpl.INSTANCE),
    NOOP("noop", PropertyAccessStrategyNoopImpl.INSTANCE);

    private final String externalName;
    private final PropertyAccessStrategy strategy;

    private BuiltInPropertyAccessStrategies(String externalName, PropertyAccessStrategy strategy) {
        this.externalName = externalName;
        this.strategy = strategy;
    }

    public String getExternalName() {
        return this.externalName;
    }

    public PropertyAccessStrategy getStrategy() {
        return this.strategy;
    }

    public static BuiltInPropertyAccessStrategies interpret(String name) {
        if (BuiltInPropertyAccessStrategies.BASIC.externalName.equals(name)) {
            return BASIC;
        }
        if (BuiltInPropertyAccessStrategies.FIELD.externalName.equals(name)) {
            return FIELD;
        }
        if (BuiltInPropertyAccessStrategies.MAP.externalName.equals(name)) {
            return MAP;
        }
        if (BuiltInPropertyAccessStrategies.EMBEDDED.externalName.equals(name)) {
            return EMBEDDED;
        }
        if (BuiltInPropertyAccessStrategies.NOOP.externalName.equals(name)) {
            return NOOP;
        }
        return null;
    }
}

