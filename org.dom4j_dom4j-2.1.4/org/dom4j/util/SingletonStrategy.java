/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.util;

public interface SingletonStrategy<T> {
    public T instance();

    public void reset();

    public void setSingletonClassName(String var1);
}

