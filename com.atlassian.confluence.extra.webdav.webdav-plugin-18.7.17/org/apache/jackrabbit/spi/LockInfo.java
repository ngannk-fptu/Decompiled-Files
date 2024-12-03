/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.NodeId;

public interface LockInfo {
    public String getLockToken();

    public String getOwner();

    public boolean isDeep();

    public boolean isSessionScoped();

    public long getSecondsRemaining();

    public boolean isLockOwner();

    public NodeId getNodeId();
}

