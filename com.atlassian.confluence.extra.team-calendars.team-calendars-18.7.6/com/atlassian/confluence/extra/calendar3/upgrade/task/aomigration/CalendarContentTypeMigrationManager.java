/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;

public interface CalendarContentTypeMigrationManager
extends StatusProvider {
    public boolean doMigrate(ActiveObjectsServiceWrapper var1);

    public boolean deleteAllCalendarContentTypes();
}

