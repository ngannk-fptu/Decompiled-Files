/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarBandanaContext;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaContextProvider;
import com.atlassian.confluence.extra.calendar3.util.BandanaKeyUtil;

public class DefaultBandanaContextProvider
implements BandanaContextProvider {
    @Override
    public BandanaContext getSubCalendarContext(BandanaSubCalendarsProvider provider, String subCalendarId) {
        CalendarBandanaContext bandanaContext = new CalendarBandanaContext(BandanaKeyUtil.toShaHex(provider.getProviderKey()), subCalendarId);
        return bandanaContext;
    }

    @Override
    public BandanaContext getSubCalendarContext(BandanaSubCalendarsProvider provider) {
        CalendarBandanaContext bandanaContext = new CalendarBandanaContext(BandanaKeyUtil.toShaHex(provider.getProviderKey()));
        return bandanaContext;
    }
}

