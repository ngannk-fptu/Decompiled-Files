/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.property.access.internal.PropertyAccessMixedImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyMixedImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyMixedImpl INSTANCE = new PropertyAccessStrategyMixedImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessMixedImpl(this, containerJavaType, propertyName);
    }
}

