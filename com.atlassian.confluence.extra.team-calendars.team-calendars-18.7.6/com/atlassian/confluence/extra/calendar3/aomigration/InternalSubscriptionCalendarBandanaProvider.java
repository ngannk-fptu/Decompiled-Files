/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.exception.CalendarMigrationException;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalSubscriptionCalendarBandanaProvider
extends AbstractBandanaSubCalendarProvider {
    private static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore";
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalSubscriptionCalendarBandanaProvider.class);

    public InternalSubscriptionCalendarBandanaProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }

    @Override
    public boolean requiresNewParent() {
        return false;
    }

    protected SubCalendarEntity createInternalSubcalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        String subscriptionId = this.getProperty(theSubCalendar, "subscriptionId", true, true, "").toString();
        if (StringUtils.isBlank((CharSequence)subscriptionId)) {
            String message = String.format("Cannot migrate SubCalendar [%s] because we cannot find subscriptionId", theSubCalendar.toString());
            LOGGER.warn(message);
            throw new CalendarMigrationException(message);
        }
        SubCalendarEntity subscriptionSubCalendar = this.getSubCalendarEntity(activeObjectsWrapper, subscriptionId);
        if (null == subscriptionSubCalendar) {
            String message = String.format("Cannot migrate internal subscription: [%s] because we cannot find source calendar: [%s]", theSubCalendar.toString(), subscriptionId);
            LOGGER.warn(message);
            throw new CalendarMigrationException(message);
        }
        if (this.isAlreadySubscribed(activeObjectsWrapper, (String)this.getParentId(theSubCalendar), subscriptionId)) {
            LOGGER.warn("Duplicate subscription with subscriptionID {} detected. Ignoring.", (Object)subscriptionId);
            return null;
        }
        SubCalendarEntity subCalendarEntity = super.createSubCalendarEntity(activeObjectsWrapper, subCalendarMigratedForUserKey, theSubCalendar);
        subCalendarEntity.setSubscription(subscriptionSubCalendar);
        String createdString = this.getProperty(theSubCalendar, "created", true, true, "").toString();
        long createdTime = StringUtils.isNotBlank((CharSequence)createdString) ? Long.parseLong(createdString) : System.currentTimeMillis();
        subCalendarEntity.setCreated(createdTime);
        subCalendarEntity.save();
        return subCalendarEntity;
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        if (this.hasParent(activeObjectsWrapper, theSubCalendar)) {
            return this.createInternalSubcalendarEntity(activeObjectsWrapper, subCalendarMigratedForUserKey, theSubCalendar);
        }
        LOGGER.debug("Did not create SubCalendarEntity for subCalendar, it is probably a parent calendar: " + theSubCalendar);
        return null;
    }

    @Override
    public SubCalendarEntity createParent(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<EventEntity> createEvents(ActiveObjectsServiceWrapper activeObjectsWrapper, SubCalendarEntity subCalendarEntity, Calendar iCalendarObject) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean requiresEventsMigration() {
        return false;
    }

    @Override
    protected Object getParentId(JSONObject theSubCalendar) {
        return this.getProperty(theSubCalendar, "parentId", true, false, null);
    }

    protected boolean hasParent(ActiveObjectsServiceWrapper activeObjectsWrapper, JSONObject theSubCalendar) {
        String parentId = (String)this.getParentId(theSubCalendar);
        return StringUtils.isNotBlank((CharSequence)parentId) && this.getSubCalendarEntity(activeObjectsWrapper, parentId) != null;
    }

    protected boolean isAlreadySubscribed(ActiveObjectsServiceWrapper activeObjectsWrapper, String parentId, String subscriptionId) {
        return activeObjectsWrapper.getActiveObjects().count(SubCalendarEntity.class, "STORE_KEY = ? AND  PARENT_ID = ? AND SUBSCRIPTION_ID = ?", new Object[]{this.getProviderKey(), parentId, subscriptionId}) > 0;
    }
}

