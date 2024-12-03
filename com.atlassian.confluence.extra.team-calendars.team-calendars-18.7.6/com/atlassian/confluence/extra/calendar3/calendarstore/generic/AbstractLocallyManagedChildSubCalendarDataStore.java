/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractPeopleHandlingSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.model.AbstractChildSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ChildSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractLocallyManagedChildSubCalendarDataStore<T extends AbstractChildSubCalendar>
extends AbstractPeopleHandlingSubCalendarDataStore<T> {
    private final ParentSubCalendarHelper parentSubCalendarHelper;

    protected AbstractLocallyManagedChildSubCalendarDataStore(DataStoreCommonPropertyAccessor DataStoreCommonPropertyAccessor2, ParentSubCalendarHelper parentSubCalendarHelper) {
        super(DataStoreCommonPropertyAccessor2);
        this.parentSubCalendarHelper = parentSubCalendarHelper;
    }

    @Override
    public T save(SubCalendar subCalendar) {
        if (StringUtils.isBlank(subCalendar.getColor())) {
            subCalendar.setColor(this.getDefaultSubCalendarColour());
        }
        return (T)((AbstractChildSubCalendar)super.save(subCalendar));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected T fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"AbstractLocallyManagedChildSubCalendarDataStore.fromStorageFormat()");
        try {
            T childSubCalendar = this.createNewSubCalendarInstance();
            PersistedSubCalendar parentPersistedSubCalendar = this.parentSubCalendarHelper.getParentSubCalendar(subCalendarEntity.getParent().getID());
            ((SubCalendar)childSubCalendar).setParent(parentPersistedSubCalendar);
            ((AbstractChildSubCalendar)childSubCalendar).setId(subCalendarEntity.getID());
            ((SubCalendar)childSubCalendar).setName(subCalendarEntity.getName());
            ((SubCalendar)childSubCalendar).setDescription(subCalendarEntity.getDescription());
            ((SubCalendar)childSubCalendar).setColor(subCalendarEntity.getColour());
            ((AbstractChildSubCalendar)childSubCalendar).setCreator(subCalendarEntity.getCreator());
            ((SubCalendar)childSubCalendar).setSpaceKey(subCalendarEntity.getSpaceKey());
            ((SubCalendar)childSubCalendar).setTimeZoneId(subCalendarEntity.getTimeZoneId());
            ((SubCalendar)childSubCalendar).setStoreKey(this.getStoreKey());
            ((SubCalendar)childSubCalendar).setCustomEventTypeId(subCalendarEntity.getUsingCustomEventTypeId());
            ((SubCalendar)childSubCalendar).setCreatedDate(subCalendarEntity.getCreated());
            ((SubCalendar)childSubCalendar).setLastUpdateDate(subCalendarEntity.getLastModified());
            if (StringUtils.isNotBlank(subCalendarEntity.getUsingCustomEventTypeId())) {
                for (CustomEventType customEventType : parentPersistedSubCalendar.getCustomEventTypes()) {
                    if (!StringUtils.equals(customEventType.getCustomEventTypeId(), subCalendarEntity.getUsingCustomEventTypeId())) continue;
                    ((SubCalendar)childSubCalendar).setCustomEventTypes(new HashSet<CustomEventType>(Arrays.asList(customEventType)));
                    break;
                }
            }
            T t = childSubCalendar;
            return t;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractLocallyManagedChildSubCalendarDataStore.fromStorageFormat()");
        }
    }

    protected abstract String getDefaultSubCalendarColour();

    protected abstract T createNewSubCalendarInstance();

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        PersistedSubCalendar parent = subCalendar.getParent();
        if (parent == null) {
            this.addFieldError(fieldErrors, "parentId", this.getText("calendar3.error.generic.parentnotspecified"));
        } else if (!this.parentSubCalendarHelper.canEditParentSubCalendarEvents(parent, AuthenticatedUserThreadLocal.get())) {
            this.addFieldError(fieldErrors, "parentId", this.getText("calendar3.error.generic.parentnotpermitted"));
        }
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new ChildSubCalendarSummary(subCalendarEntity.getParent().getID(), subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    public int getSubCalendarsCount() {
        return 0;
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasEditEventPrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasViewEventPrivilege(this.getSubCalendarEntity(subCalendarId).getParent().getID(), user);
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasDeletePrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasAdminPrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    @Override
    protected abstract String getStoreKey();

    @Override
    public abstract String getType();
}

