/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;

public interface SubCalendarRestrictionsMigrator {
    public void migrateRestrictions(ActiveObjects var1, BandanaSubCalendarsProvider var2, String var3, String var4);
}

