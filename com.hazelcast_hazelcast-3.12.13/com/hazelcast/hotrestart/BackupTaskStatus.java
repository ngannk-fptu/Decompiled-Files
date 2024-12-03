/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

import com.hazelcast.hotrestart.BackupTaskState;

public class BackupTaskStatus {
    private final BackupTaskState state;
    private final int completed;
    private final int total;

    public BackupTaskStatus(BackupTaskState state, int completed, int total) {
        this.state = state;
        this.completed = completed;
        this.total = total;
    }

    public BackupTaskState getState() {
        return this.state;
    }

    public int getCompleted() {
        return this.completed;
    }

    public int getTotal() {
        return this.total;
    }

    public float getProgress() {
        return this.total > 0 ? (float)this.completed / (float)this.total : 0.0f;
    }

    public String toString() {
        return "BackupTaskStatus{state=" + (Object)((Object)this.state) + ", completed=" + this.completed + ", total=" + this.total + '}';
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof BackupTaskStatus)) return false;
        BackupTaskStatus that = (BackupTaskStatus)obj;
        if (this.completed != that.completed) return false;
        if (this.total != that.total) return false;
        if (this.state != that.state) return false;
        return true;
    }

    public int hashCode() {
        int result = this.state != null ? this.state.hashCode() : 0;
        result = 31 * result + this.completed;
        result = 31 * result + this.total;
        return result;
    }
}

