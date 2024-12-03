/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface ActiveLock
extends XmlSerializable {
    public boolean isLockedByToken(String var1);

    public boolean isExpired();

    public String getToken();

    public String getOwner();

    public void setOwner(String var1);

    public long getTimeout();

    public void setTimeout(long var1);

    public boolean isDeep();

    public void setIsDeep(boolean var1);

    public String getLockroot();

    public void setLockroot(String var1);

    public Type getType();

    public Scope getScope();
}

