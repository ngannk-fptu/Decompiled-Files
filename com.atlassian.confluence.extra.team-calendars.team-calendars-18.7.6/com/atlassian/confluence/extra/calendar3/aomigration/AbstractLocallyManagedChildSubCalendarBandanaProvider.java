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
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import org.json.JSONObject;

public abstract class AbstractLocallyManagedChildSubCalendarBandanaProvider
extends AbstractBandanaSubCalendarProvider {
    public AbstractLocallyManagedChildSubCalendarBandanaProvider(UserAccessor userAccessor) {
        super(userAccessor);
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
    public boolean requiresEventsMigration() {
        return true;
    }
}

