/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import java.io.IOException;
import java.util.List;
import net.fortuna.ical4j.data.ParserException;
import org.json.JSONException;

public interface BandanaToActiveObjectsMigrator {
    public void migrateSubCalendar(ActiveObjectsServiceWrapper var1, BandanaSubCalendarsProvider var2, String var3) throws JSONException, IOException, ParserException;

    public void migrateProvider(ActiveObjectsServiceWrapper var1, int var2, int var3, BandanaSubCalendarsProvider var4) throws JSONException, ParserException, IOException;

    public void doMigrate(ActiveObjectsServiceWrapper var1, List<BandanaSubCalendarsProvider> var2) throws IOException, ParserException, JSONException;
}

