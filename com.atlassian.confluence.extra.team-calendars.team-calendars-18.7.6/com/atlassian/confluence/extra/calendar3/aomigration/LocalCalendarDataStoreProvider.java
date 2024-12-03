/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.aomigration.AbstractLegacyDataStoreProvider;
import com.atlassian.confluence.user.UserAccessor;

public class LocalCalendarDataStoreProvider
extends AbstractLegacyDataStoreProvider {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.LocalCalendarDataStore";

    public LocalCalendarDataStoreProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    protected String getStoreKey() {
        return "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore";
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }

    @Override
    public boolean requiresEventsMigration() {
        return true;
    }
}

