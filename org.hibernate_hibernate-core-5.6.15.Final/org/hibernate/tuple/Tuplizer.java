/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.property.access.spi.Getter;

public interface Tuplizer {
    public Object[] getPropertyValues(Object var1);

    public void setPropertyValues(Object var1, Object[] var2);

    public Object getPropertyValue(Object var1, int var2);

    public Object instantiate();

    public boolean isInstance(Object var1);

    public Class getMappedClass();

    public Getter getGetter(int var1);
}

