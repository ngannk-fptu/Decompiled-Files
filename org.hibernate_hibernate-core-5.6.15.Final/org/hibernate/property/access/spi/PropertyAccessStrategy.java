/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.property.access.spi.PropertyAccess;

public interface PropertyAccessStrategy {
    public PropertyAccess buildPropertyAccess(Class var1, String var2);
}

