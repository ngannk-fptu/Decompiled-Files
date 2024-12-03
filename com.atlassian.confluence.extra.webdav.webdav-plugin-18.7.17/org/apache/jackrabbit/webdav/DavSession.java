/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

public interface DavSession {
    public void addReference(Object var1);

    public void removeReference(Object var1);

    public void addLockToken(String var1);

    public String[] getLockTokens();

    public void removeLockToken(String var1);
}

