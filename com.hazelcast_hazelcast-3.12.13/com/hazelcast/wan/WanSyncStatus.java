/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

public enum WanSyncStatus {
    READY(0),
    IN_PROGRESS(1),
    FAILED(2);

    private int status;

    private WanSyncStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static WanSyncStatus getByStatus(int status) {
        for (WanSyncStatus syncState : WanSyncStatus.values()) {
            if (syncState.status != status) continue;
            return syncState;
        }
        return null;
    }
}

