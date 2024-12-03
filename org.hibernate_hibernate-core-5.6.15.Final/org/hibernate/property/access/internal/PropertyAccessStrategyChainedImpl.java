/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.PropertyNotFoundException;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyChainedImpl
implements PropertyAccessStrategy {
    private final PropertyAccessStrategy[] chain;

    public PropertyAccessStrategyChainedImpl(PropertyAccessStrategy ... chain) {
        this.chain = chain;
    }

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        for (PropertyAccessStrategy candidate : this.chain) {
            try {
                return candidate.buildPropertyAccess(containerJavaType, propertyName);
            }
            catch (Exception exception) {
            }
        }
        throw new PropertyNotFoundException("Could not resolve PropertyAccess for " + propertyName + " on " + containerJavaType);
    }
}

