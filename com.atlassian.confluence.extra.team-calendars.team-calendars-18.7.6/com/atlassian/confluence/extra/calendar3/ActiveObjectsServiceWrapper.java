/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.user.UserAccessor
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fortuna.ical4j.model.component.VEvent;
import net.java.ao.RawEntity;

public interface ActiveObjectsServiceWrapper {
    public ActiveObjects getActiveObjects();

    public String getTableName(Class<? extends RawEntity<?>> var1);

    public EventEntity createEventEntity(SubCalendarEntity var1, VEvent var2);

    public EventEntity createEventEntity(String var1, VEvent var2);

    public EventEntity createEventEntity(PersistedSubCalendar var1, VEvent var2);

    public InviteeEntity[] getInvitees(EventEntity var1);

    public Map<Integer, Set<InviteeEntity>> getInvitees(List<EventEntity> var1);

    public boolean deleteInvitees(EventEntity var1);

    public void deleteInviteeFromAllEvents(String var1);

    public EventRecurrenceExclusionEntity[] getRecurrenceExclusions(EventEntity var1);

    public Map<Integer, Set<EventRecurrenceExclusionEntity>> getRecurrenceExclusions(List<EventEntity> var1);

    public boolean deleteEventRecurrenceExclusionEntities(EventEntity var1);

    public void createInviteeEntity(EventEntity var1, VEvent var2, UserAccessor var3);

    public void createEventRecurrenceExclusionEntity(EventEntity var1, VEvent var2);

    public ExtraSubCalendarPropertyEntity createSubCalendarEntityProperty(SubCalendarEntity var1, String var2, Object var3);

    public SubCalendarEntity getSubCalendarEntity(EventEntity var1);
}

