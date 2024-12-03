/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import java.io.IOException;
import java.util.Set;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.json.JSONException;
import org.json.JSONObject;

public interface BandanaSubCalendarAccessor {
    public Calendar getSubCalendarContent(BandanaSubCalendarsProvider var1, String var2) throws IOException, ParserException;

    public JSONObject getSubCalendarJson(BandanaSubCalendarsProvider var1, String var2) throws JSONException;

    public Set<String> getSubCalendarIds(BandanaSubCalendarsProvider var1);
}

