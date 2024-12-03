/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.property.access.internal.PropertyAccessEmbeddedImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyEmbeddedImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyEmbeddedImpl INSTANCE = new PropertyAccessStrategyEmbeddedImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessEmbeddedImpl(this, containerJavaType, propertyName);
    }
}

