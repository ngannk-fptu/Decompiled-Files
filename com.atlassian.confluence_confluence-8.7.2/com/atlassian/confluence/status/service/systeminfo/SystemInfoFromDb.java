/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;

@Deprecated
public class SystemInfoFromDb {
    private final ConfluenceInfo confluenceInfo;
    private final DatabaseInfo databaseInfo;
    private final UsageInfo usageInfo;

    public SystemInfoFromDb(ConfluenceInfo confluenceInfo, DatabaseInfo databaseInfo, UsageInfo usageInfo) {
        this.confluenceInfo = confluenceInfo;
        this.databaseInfo = databaseInfo;
        this.usageInfo = usageInfo;
    }

    public ConfluenceInfo getConfluenceInfo() {
        return this.confluenceInfo;
    }

    public DatabaseInfo getDatabaseInfo() {
        return this.databaseInfo;
    }

    public UsageInfo getUsageInfo() {
        return this.usageInfo;
    }
}

