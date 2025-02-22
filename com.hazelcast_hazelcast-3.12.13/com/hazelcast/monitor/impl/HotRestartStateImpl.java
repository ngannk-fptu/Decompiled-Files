/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.hotrestart.BackupTaskState;
import com.hazelcast.hotrestart.BackupTaskStatus;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.HotRestartState;
import com.hazelcast.util.JsonUtil;

public class HotRestartStateImpl
implements HotRestartState {
    private BackupTaskStatus backupTaskStatus;
    private boolean isHotBackupEnabled;
    private String backupDirectory;

    public HotRestartStateImpl() {
    }

    public HotRestartStateImpl(BackupTaskStatus backupTaskStatus, boolean isHotBackupEnabled, String backupDirectory) {
        this.backupTaskStatus = backupTaskStatus;
        this.isHotBackupEnabled = isHotBackupEnabled;
        this.backupDirectory = backupDirectory;
    }

    @Override
    public BackupTaskStatus getBackupTaskStatus() {
        return this.backupTaskStatus;
    }

    @Override
    public boolean isHotBackupEnabled() {
        return this.isHotBackupEnabled;
    }

    @Override
    public String getBackupDirectory() {
        return this.backupDirectory;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        if (this.backupTaskStatus != null) {
            root.add("backupTaskState", this.backupTaskStatus.getState().name());
            root.add("backupTaskCompleted", this.backupTaskStatus.getCompleted());
            root.add("backupTaskTotal", this.backupTaskStatus.getTotal());
            root.add("isHotBackupEnabled", this.isHotBackupEnabled);
            root.add("backupDirectory", this.backupDirectory);
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        String jsonBackupTaskState = JsonUtil.getString(json, "backupTaskState", null);
        int jsonBackupTaskCompleted = JsonUtil.getInt(json, "backupTaskCompleted", 0);
        int jsonBackupTaskTotal = JsonUtil.getInt(json, "backupTaskTotal", 0);
        this.backupTaskStatus = jsonBackupTaskState != null ? new BackupTaskStatus(BackupTaskState.valueOf(jsonBackupTaskState), jsonBackupTaskCompleted, jsonBackupTaskTotal) : null;
        this.isHotBackupEnabled = JsonUtil.getBoolean(json, "isHotBackupEnabled", false);
        this.backupDirectory = JsonUtil.getString(json, "backupDirectory", null);
    }

    public String toString() {
        return "HotRestartStateImpl{backupTaskStatus=" + this.backupTaskStatus + ", isHotBackupEnabled=" + this.isHotBackupEnabled + ", backupDirectory=" + this.backupDirectory + '}';
    }
}

