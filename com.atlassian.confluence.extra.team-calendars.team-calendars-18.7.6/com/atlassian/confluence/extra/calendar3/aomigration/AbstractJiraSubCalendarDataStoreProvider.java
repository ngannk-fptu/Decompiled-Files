/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarPropertyProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import org.json.JSONObject;

public abstract class AbstractJiraSubCalendarDataStoreProvider
extends AbstractBandanaSubCalendarProvider {
    private final SubCalendarPropertyProvider subCalendarPropertyProvider;

    public AbstractJiraSubCalendarDataStoreProvider(UserAccessor userAccessor, SubCalendarPropertyProvider subCalendarPropertyProvider) {
        super(userAccessor);
        this.subCalendarPropertyProvider = subCalendarPropertyProvider;
    }

    @Override
    public boolean requiresNewParent() {
        return false;
    }

    @Override
    public SubCalendarEntity createParent(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        SubCalendarEntity subCalendarEntity = super.createSubCalendarEntity(activeObjectsWrapper, subCalendarMigratedForUserKey, theSubCalendar);
        return this.subCalendarPropertyProvider.addProperties(activeObjectsWrapper, subCalendarEntity, theSubCalendar, new AbstractBandanaSubCalendarProvider.BandanaProviderJsonPropertyGetter());
    }

    @Override
    public boolean requiresEventsMigration() {
        return false;
    }
}

