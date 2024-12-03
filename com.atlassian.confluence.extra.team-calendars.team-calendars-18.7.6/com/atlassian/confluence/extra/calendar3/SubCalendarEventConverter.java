/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.google.common.base.Function;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;

public interface SubCalendarEventConverter<T extends PersistedSubCalendar> {
    public SubCalendarEvent toSubCalendarEvent(VEvent var1, T var2, TimeZone var3, Function<Void, Boolean> var4);

    public JodaIcal4jDateTimeConverter getJodaIcal4jDateTimeConverter();

    public VEvent getEvent(T var1, String var2, String var3) throws Exception;
}

