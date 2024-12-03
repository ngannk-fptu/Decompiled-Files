/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.util.Map;
import org.hibernate.property.access.internal.PropertyAccessMapImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class PropertyAccessStrategyMapImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyMapImpl INSTANCE = new PropertyAccessStrategyMapImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        if (containerJavaType != null && !Map.class.isAssignableFrom(containerJavaType)) {
            throw new IllegalArgumentException(String.format("Expecting class: [%1$s], but containerJavaType is of type: [%2$s] for propertyName: [%3$s]", Map.class.getName(), containerJavaType.getName(), propertyName));
        }
        return new PropertyAccessMapImpl(this, propertyName);
    }
}

