/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.thready.manager;

public interface ThreadNameManager {
    public boolean isUnchanged();

    public void setThreadName();

    public void addThreadAttribute(String var1, String var2);

    public void putThreadAttribute(String var1, String var2);

    public void clearThreadAttributes();
}

