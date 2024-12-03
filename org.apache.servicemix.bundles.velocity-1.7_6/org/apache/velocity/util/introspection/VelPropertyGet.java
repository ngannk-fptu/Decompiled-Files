/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

public interface VelPropertyGet {
    public Object invoke(Object var1) throws Exception;

    public boolean isCacheable();

    public String getMethodName();
}

