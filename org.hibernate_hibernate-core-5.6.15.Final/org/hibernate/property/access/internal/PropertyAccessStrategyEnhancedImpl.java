/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.property.access.internal.PropertyAccessEnhancedImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyEnhancedImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyEnhancedImpl INSTANCE = new PropertyAccessStrategyEnhancedImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessEnhancedImpl(this, containerJavaType, propertyName);
    }
}

