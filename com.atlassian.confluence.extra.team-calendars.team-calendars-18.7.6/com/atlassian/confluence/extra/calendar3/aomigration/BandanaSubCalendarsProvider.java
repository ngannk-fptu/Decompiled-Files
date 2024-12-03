/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import org.json.JSONObject;

public interface BandanaSubCalendarsProvider {
    public String getProviderKey();

    public boolean requiresNewParent();

    public SubCalendarEntity createParent(ActiveObjectsServiceWrapper var1, boolean var2, JSONObject var3);

    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper var1, boolean var2, JSONObject var3);

    public boolean requiresEventsMigration();

    public List<EventEntity> createEvents(ActiveObjectsServiceWrapper var1, SubCalendarEntity var2, Calendar var3);
}

