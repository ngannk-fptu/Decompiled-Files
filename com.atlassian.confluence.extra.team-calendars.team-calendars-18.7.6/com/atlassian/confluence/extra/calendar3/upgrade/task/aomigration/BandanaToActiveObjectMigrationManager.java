/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  org.json.JSONException
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.events.migration.ProgressCalendarEvent;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.io.IOException;
import java.util.List;
import net.fortuna.ical4j.data.ParserException;
import org.json.JSONException;

public interface BandanaToActiveObjectMigrationManager
extends StatusProvider {
    public boolean doMigrate(ActiveObjectsServiceWrapper var1) throws JSONException, ParserException, IOException;

    public boolean doMigrate(ActiveObjectsServiceWrapper var1, boolean var2) throws JSONException, ParserException, IOException;

    public boolean forceDeleteAllData(ActiveObjectsServiceWrapper var1);

    public String getInProgressMessage(I18NBean var1);

    public List<ProgressCalendarEvent> getMigrationEvents();
}

