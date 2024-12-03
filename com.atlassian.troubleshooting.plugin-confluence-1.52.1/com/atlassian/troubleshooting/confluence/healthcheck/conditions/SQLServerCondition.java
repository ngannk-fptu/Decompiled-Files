/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.conditions;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import org.springframework.beans.factory.annotation.Autowired;

public class SQLServerCondition
implements SupportHealthCheckCondition {
    private static final String DIALECT = "SQLServerDialect";
    private final SystemInformationService systemInformationService;

    @Autowired
    public SQLServerCondition(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    @Override
    public boolean shouldDisplay() {
        String databaseDialect = this.systemInformationService.getDatabaseInfo().getDialect();
        return databaseDialect != null && databaseDialect.toLowerCase().contains(DIALECT.toLowerCase());
    }
}

