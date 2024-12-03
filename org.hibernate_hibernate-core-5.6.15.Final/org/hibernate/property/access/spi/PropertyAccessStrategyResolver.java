/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.EntityMode;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.service.Service;

public interface PropertyAccessStrategyResolver
extends Service {
    public PropertyAccessStrategy resolvePropertyAccessStrategy(Class var1, String var2, EntityMode var3);
}

