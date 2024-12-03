/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.hotrestart.BackupTaskStatus;
import com.hazelcast.internal.management.JsonSerializable;

public interface HotRestartState
extends JsonSerializable {
    public BackupTaskStatus getBackupTaskStatus();

    public boolean isHotBackupEnabled();

    public String getBackupDirectory();
}

