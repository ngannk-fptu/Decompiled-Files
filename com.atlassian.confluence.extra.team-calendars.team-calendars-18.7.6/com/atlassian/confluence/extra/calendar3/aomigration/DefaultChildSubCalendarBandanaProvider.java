/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.aomigration.AbstractLocallyManagedChildSubCalendarBandanaProvider;
import com.atlassian.confluence.user.UserAccessor;

public class DefaultChildSubCalendarBandanaProvider
extends AbstractLocallyManagedChildSubCalendarBandanaProvider {
    protected static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore";

    public DefaultChildSubCalendarBandanaProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }
}

