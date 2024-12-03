/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

public interface VelPropertySet {
    public Object invoke(Object var1, Object var2) throws Exception;

    public boolean isCacheable();

    public String getMethodName();
}

