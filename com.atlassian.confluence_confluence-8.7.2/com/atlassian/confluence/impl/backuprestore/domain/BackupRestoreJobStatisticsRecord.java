/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.domain;

import com.atlassian.confluence.core.NotExportable;
import java.io.Serializable;

public class BackupRestoreJobStatisticsRecord
implements Serializable,
NotExportable {
    private static final long serialVersionUID = -4654322426140579026L;
    private long id;
    private String statistics;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatistics() {
        return this.statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }
}

