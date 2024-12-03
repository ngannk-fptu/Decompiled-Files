/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.property.access.internal.PropertyAccessBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyBasicImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyBasicImpl INSTANCE = new PropertyAccessStrategyBasicImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessBasicImpl(this, containerJavaType, propertyName);
    }
}

