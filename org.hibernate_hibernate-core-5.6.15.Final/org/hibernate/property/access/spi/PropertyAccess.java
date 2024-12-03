/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public interface PropertyAccess {
    public PropertyAccessStrategy getPropertyAccessStrategy();

    public Getter getGetter();

    public Setter getSetter();
}

