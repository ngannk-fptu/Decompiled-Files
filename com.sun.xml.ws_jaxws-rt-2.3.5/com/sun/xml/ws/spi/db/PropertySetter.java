/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

public interface PropertySetter {
    public Class getType();

    public <A> A getAnnotation(Class<A> var1);

    public void set(Object var1, Object var2);
}

