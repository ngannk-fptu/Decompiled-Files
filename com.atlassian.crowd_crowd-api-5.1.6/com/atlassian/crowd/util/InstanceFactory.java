/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

public interface InstanceFactory {
    public Object getInstance(String var1) throws ClassNotFoundException;

    public Object getInstance(String var1, ClassLoader var2) throws ClassNotFoundException;

    public <T> T getInstance(Class<T> var1);
}

