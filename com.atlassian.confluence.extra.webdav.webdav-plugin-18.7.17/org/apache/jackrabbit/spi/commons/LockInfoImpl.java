/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import org.apache.jackrabbit.spi.LockInfo;
import org.apache.jackrabbit.spi.NodeId;

public class LockInfoImpl
implements LockInfo,
Serializable {
    private final String lockToken;
    private final String lockOwner;
    private final boolean isDeep;
    private final boolean isSessionScoped;
    private final long secondsRemaining;
    private final boolean isLockOwner;
    private final NodeId nodeId;

    public LockInfoImpl(String lockToken, String lockOwner, boolean isDeep, boolean isSessionScoped, NodeId nodeId) {
        this(lockToken, lockOwner, isDeep, isSessionScoped, Long.MAX_VALUE, lockToken != null, nodeId);
    }

    public LockInfoImpl(String lockToken, String lockOwner, boolean isDeep, boolean isSessionScoped, long secondsRemaining, boolean isLockOwner, NodeId nodeId) {
        this.lockToken = lockToken;
        this.lockOwner = lockOwner;
        this.isDeep = isDeep;
        this.isSessionScoped = isSessionScoped;
        this.secondsRemaining = secondsRemaining;
        this.isLockOwner = isLockOwner;
        this.nodeId = nodeId;
    }

    @Override
    public String getLockToken() {
        return this.lockToken;
    }

    @Override
    public String getOwner() {
        return this.lockOwner;
    }

    @Override
    public boolean isDeep() {
        return this.isDeep;
    }

    @Override
    public boolean isSessionScoped() {
        return this.isSessionScoped;
    }

    @Override
    public long getSecondsRemaining() {
        return this.secondsRemaining;
    }

    @Override
    public boolean isLockOwner() {
        return this.isLockOwner;
    }

    @Override
    public NodeId getNodeId() {
        return this.nodeId;
    }
}

