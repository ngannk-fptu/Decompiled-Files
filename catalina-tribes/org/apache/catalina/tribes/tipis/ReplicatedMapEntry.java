/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.tipis;

import java.io.IOException;
import java.io.Serializable;

public interface ReplicatedMapEntry
extends Serializable {
    public boolean isDirty();

    public boolean isDiffable();

    public byte[] getDiff() throws IOException;

    public void applyDiff(byte[] var1, int var2, int var3) throws IOException, ClassNotFoundException;

    public void resetDiff();

    public void lock();

    public void unlock();

    public void setOwner(Object var1);

    public long getVersion();

    public void setVersion(long var1);

    public long getLastTimeReplicated();

    public void setLastTimeReplicated(long var1);

    public boolean isAccessReplicate();

    public void accessEntry();
}

