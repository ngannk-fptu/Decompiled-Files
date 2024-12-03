/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.domain;

import com.atlassian.confluence.core.NotExportable;
import java.io.Serializable;

public class BackupRestoreJobSettingsRecord
implements Serializable,
NotExportable {
    private static final long serialVersionUID = 26768129271289279L;
    private Long id;
    private String settings;

    public BackupRestoreJobSettingsRecord() {
    }

    public BackupRestoreJobSettingsRecord(Long id, String settings) {
        this.id = id;
        this.settings = settings;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettings() {
        return this.settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}

