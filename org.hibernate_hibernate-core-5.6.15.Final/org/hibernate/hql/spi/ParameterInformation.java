/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import org.hibernate.type.Type;

public interface ParameterInformation {
    public int[] getSourceLocations();

    public Type getExpectedType();

    public void setExpectedType(Type var1);

    public void addSourceLocation(int var1);
}

