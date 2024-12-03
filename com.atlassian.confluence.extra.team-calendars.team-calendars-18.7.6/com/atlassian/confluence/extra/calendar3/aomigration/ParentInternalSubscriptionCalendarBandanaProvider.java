/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.InternalSubscriptionCalendarBandanaProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentInternalSubscriptionCalendarBandanaProvider
extends InternalSubscriptionCalendarBandanaProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParentInternalSubscriptionCalendarBandanaProvider.class);

    public ParentInternalSubscriptionCalendarBandanaProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        if (this.getParentId(theSubCalendar) == null) {
            return this.createInternalSubcalendarEntity(activeObjectsWrapper, subCalendarMigratedForUserKey, theSubCalendar);
        }
        LOGGER.debug("Did not create SubCalendarEntity for subCalendar, it is probably a child calendar: " + theSubCalendar);
        return null;
    }
}

