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
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.EncryptKeyHolder;
import com.atlassian.confluence.extra.calendar3.util.EncryptionException;
import com.atlassian.confluence.extra.calendar3.util.EncryptionUtils;
import com.atlassian.confluence.user.UserAccessor;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionCalendarBandanaProvider
extends AbstractBandanaSubCalendarProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCalendarBandanaProvider.class);
    private final EncryptKeyHolder keyHolder;

    public SubscriptionCalendarBandanaProvider(UserAccessor userAccessor, EncryptKeyHolder keyHolder) {
        super(userAccessor);
        this.keyHolder = keyHolder;
    }

    @Override
    public String getProviderKey() {
        return "com.atlassian.confluence.extra.calendar3.calendarstore.SubscriptionCalendarDataStore";
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
        activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "sourceLocation", this.getProperty(theSubCalendar, "sourceLocation", false, true, ""));
        String userName = (String)this.getProperty(theSubCalendar, "userName", true, false, "");
        if (StringUtils.isNotBlank(userName)) {
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "userName", userName);
        }
        String password = (String)this.getProperty(theSubCalendar, "password", true, false, "");
        String encryptPassword = "";
        if (StringUtils.isNotBlank(password)) {
            try {
                encryptPassword = EncryptionUtils.encrypt(this.keyHolder.getKey(), password);
            }
            catch (EncryptionException e) {
                LOG.error("Could not encrypt password", (Throwable)e);
            }
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "password", encryptPassword);
        }
        subCalendarEntity = this.getSubCalendarEntity(activeObjectsWrapper, subCalendarEntity.getID());
        return subCalendarEntity;
    }

    @Override
    public boolean requiresEventsMigration() {
        return false;
    }

    @Override
    public List<EventEntity> createEvents(ActiveObjectsServiceWrapper activeObjectsWrapper, SubCalendarEntity subCalendarEntity, Calendar iCalendarObject) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected Object getParentId(JSONObject theSubCalendar) {
        return this.getProperty(theSubCalendar, "parentId", true, false, null);
    }
}

