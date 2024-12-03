/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;

public interface BandanaContextProvider {
    public BandanaContext getSubCalendarContext(BandanaSubCalendarsProvider var1, String var2);

    public BandanaContext getSubCalendarContext(BandanaSubCalendarsProvider var1);
}

