/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.property.access.internal.PropertyAccessFieldImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyFieldImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyFieldImpl INSTANCE = new PropertyAccessStrategyFieldImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessFieldImpl(this, containerJavaType, propertyName);
    }
}

