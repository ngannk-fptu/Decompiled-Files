/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

public interface PropertyGetter {
    public Class getType();

    public <A> A getAnnotation(Class<A> var1);

    public Object get(Object var1);
}

