/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

public interface SessionIdGenerator {
    public String getJvmRoute();

    public void setJvmRoute(String var1);

    public int getSessionIdLength();

    public void setSessionIdLength(int var1);

    public String generateSessionId();

    public String generateSessionId(String var1);
}

