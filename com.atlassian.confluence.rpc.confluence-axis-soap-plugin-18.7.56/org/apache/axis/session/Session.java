/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.session;

import java.util.Enumeration;

public interface Session {
    public Object get(String var1);

    public void set(String var1, Object var2);

    public void remove(String var1);

    public Enumeration getKeys();

    public void setTimeout(int var1);

    public int getTimeout();

    public void touch();

    public void invalidate();

    public Object getLockObject();
}

