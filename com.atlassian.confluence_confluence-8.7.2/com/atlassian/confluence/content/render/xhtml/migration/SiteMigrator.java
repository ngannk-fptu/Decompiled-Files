/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;

public interface SiteMigrator {
    public ExceptionReport migrateSite() throws MigrationException;

    public boolean isSiteMigrationInProgress();
}

