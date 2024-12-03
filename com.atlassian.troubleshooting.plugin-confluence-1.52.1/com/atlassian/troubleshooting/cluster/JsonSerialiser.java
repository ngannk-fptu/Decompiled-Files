/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.cluster;

public interface JsonSerialiser {
    public <T> T fromJson(String var1, Class<T> var2);

    public String toJson(Object var1);
}

