/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntityDTO;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntityDTO;
import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarInSpaceEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventRecurrenceExclusionTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.InviteeTable;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.UUIDGenerate;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="activeObjectsServiceWrapper")
@ExportAsService
public class DefaultActiveObjectsServiceWrapper
implements ActiveObjectsServiceWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultActiveObjectsServiceWrapper.class);
    private static final String SUB_CAL_REQUEST_CONTEXT_CACHE_KEY = "SUB_CAL_REQUEST_CONTEXT_CACHE_KEY";
    private static final Map<Class<? extends RawEntity<?>>, String> TABLE_NAME_MAP = Collections.unmodifiableMap(new HashMap<Class<? extends RawEntity<?>>, String>(){
        {
            this.put(SubCalendarEntity.class, "AO_950DC3_TC_SUBCALS");
            this.put(ExtraSubCalendarPropertyEntity.class, "AO_950DC3_TC_SUBCALS_PROPS");
            this.put(SubCalendarUserRestrictionEntity.class, "AO_950DC3_TC_SUBCALS_PRIV_USR");
            this.put(SubCalendarGroupRestrictionEntity.class, "AO_950DC3_TC_SUBCALS_PRIV_GRP");
            this.put(EventEntity.class, "AO_950DC3_TC_EVENTS");
            this.put(InviteeEntity.class, "AO_950DC3_TC_EVENTS_INVITEES");
            this.put(EventRecurrenceExclusionEntity.class, "AO_950DC3_TC_EVENTS_EXCL");
            this.put(ReminderSettingEntity.class, "AO_950DC3_TC_REMINDER_SETTINGS");
            this.put(JiraReminderEventEntity.class, "AO_950DC3_TC_JIRA_REMI_EVENTS");
            this.put(SubCalendarInSpaceEntity.class, "AO_950DC3_TC_SUBCALS_IN_SPACE");
        }
    });
    private final ActiveObjects activeObjects;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier queryDSLSupplier;

    @Autowired
    public DefaultActiveObjectsServiceWrapper(@ComponentImport ActiveObjects activeObjects, QueryDSLMapper queryDSLMapper, @Qualifier(value="transactionalQueryDSLSupplier") QueryDSLSupplier queryDSLSupplier) {
        this.activeObjects = activeObjects;
        this.queryDSLMapper = queryDSLMapper;
        this.queryDSLSupplier = queryDSLSupplier;
    }

    @Override
    public ActiveObjects getActiveObjects() {
        return this.activeObjects;
    }

    @Override
    public String getTableName(Class<? extends RawEntity<?>> entityClass) {
        return TABLE_NAME_MAP.get(entityClass);
    }

    @Override
    public EventEntity createEventEntity(SubCalendarEntity subCalendarEntity, VEvent vEventComponent) {
        return this.createEventEntity(subCalendarEntity.getID(), vEventComponent);
    }

    @Override
    public EventEntity createEventEntity(PersistedSubCalendar subCalendarEntity, VEvent vEventComponent) {
        return this.createEventEntity(subCalendarEntity.getId(), vEventComponent);
    }

    @Override
    public InviteeEntity[] getInvitees(EventEntity eventEntity) {
        Objects.requireNonNull(eventEntity);
        int selectedEventId = eventEntity.getID();
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        SQLQuery sqlSubQuery = (SQLQuery)((SQLQuery)((SQLQuery)SQLExpressions.select(inviteeTable.ID.min()).from((Expression<?>)inviteeTable)).where(inviteeTable.EVENT_ID.eq(selectedEventId))).groupBy((Expression<?>[])new Expression[]{inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID});
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            List distinctInviteeIds = ((AbstractSQLQuery)(query = (SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).where(inviteeTable.ID.in(sqlSubQuery).and(inviteeTable.EVENT_ID.eq(selectedEventId)))).select(inviteeTable.ID)).fetch();
            if (distinctInviteeIds == null || distinctInviteeIds.size() == 0) {
                return new InviteeEntity[0];
            }
            ArrayList resultList = new ArrayList(distinctInviteeIds.size());
            Iterators.partition(distinctInviteeIds.iterator(), (int)1000).forEachRemaining(distinctInviteeIdsBatch -> {
                String placeholderCommaList = distinctInviteeIdsBatch.stream().map(id -> "?").collect(Collectors.joining(","));
                resultList.addAll(Lists.newArrayList((Object[])((InviteeEntity[])this.activeObjects.find(InviteeEntity.class, "ID IN (" + placeholderCommaList + " )", distinctInviteeIdsBatch.toArray()))));
            });
            return resultList.toArray(new InviteeEntity[0]);
        });
    }

    @Override
    public Map<Integer, Set<InviteeEntity>> getInvitees(List<EventEntity> eventEntities) {
        Objects.requireNonNull(eventEntities);
        HashMap<Integer, Set<InviteeEntity>> mapResult = new HashMap<Integer, Set<InviteeEntity>>(200);
        if (eventEntities.size() == 0) {
            return mapResult;
        }
        List selectedEventIds = eventEntities.stream().map(eventEntity -> eventEntity.getID()).collect(Collectors.toList());
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        EventTable eventTable = this.queryDSLMapper.getEventsTable();
        Iterators.partition(selectedEventIds.iterator(), (int)1000).forEachRemaining(selectedEventIdsBatch -> this.queryDSLSupplier.executeSQLQuery(query -> {
            List batchResult = ((AbstractSQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).join((EntityPath)eventTable)).on((Predicate)inviteeTable.EVENT_ID.eq(eventTable.ID))).where(eventTable.ID.in((Collection<Integer>)selectedEventIdsBatch))).groupBy((Expression<?>[])new Expression[]{inviteeTable.EVENT_ID, inviteeTable.ID, inviteeTable.INVITEE_ID})).select(Projections.constructor(InviteeEntityDTO.class, inviteeTable.ID, inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID))).fetch();
            int currentEventId = -1;
            for (InviteeEntityDTO inviteeEntityDTO : batchResult) {
                if (currentEventId != inviteeEntityDTO.getEvent().getID()) {
                    currentEventId = inviteeEntityDTO.getEvent().getID();
                }
                Set inviteeEntityListPerEvent = mapResult.computeIfAbsent(currentEventId, eventId -> new HashSet());
                inviteeEntityListPerEvent.add(inviteeEntityDTO);
            }
            return null;
        }));
        return mapResult;
    }

    @Override
    public boolean deleteInvitees(EventEntity eventEntity) {
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        int selectedEventId = eventEntity.getID();
        return this.queryDSLSupplier.executeDeleteSQLClause(inviteeTable, sqlDeleteClause -> {
            sqlDeleteClause.where((Predicate)inviteeTable.EVENT_ID.eq(selectedEventId));
            long deletedRow = sqlDeleteClause.execute();
            return deletedRow > 0L;
        });
    }

    @Override
    public void deleteInviteeFromAllEvents(String inviteeId) {
        Objects.nonNull(inviteeId);
        InviteeTable inviteeTable = this.queryDSLMapper.getInviteeTable();
        this.queryDSLSupplier.executeDeleteSQLClause(inviteeTable, sqlDeleteClause -> {
            sqlDeleteClause = sqlDeleteClause.where((Predicate)inviteeTable.INVITEE_ID.eq(inviteeId));
            sqlDeleteClause.execute();
            return null;
        });
    }

    @Override
    public EventRecurrenceExclusionEntity[] getRecurrenceExclusions(EventEntity eventEntity) {
        Objects.nonNull(eventEntity);
        int selectedEventId = eventEntity.getID();
        EventRecurrenceExclusionTable eventRecurrenceExclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        SQLQuery sqlSubQuery = (SQLQuery)((SQLQuery)((SQLQuery)SQLExpressions.select(eventRecurrenceExclusionTable.ID.min()).from((Expression<?>)eventRecurrenceExclusionTable)).where(eventRecurrenceExclusionTable.EVENT_ID.eq(selectedEventId))).groupBy((Expression<?>[])new Expression[]{eventRecurrenceExclusionTable.EVENT_ID, eventRecurrenceExclusionTable.EXCLUSION, eventRecurrenceExclusionTable.ALL_DAY});
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            List distinctExclusionIds = ((AbstractSQLQuery)(query = (SQLQuery)((SQLQuery)query.from((Expression<?>)eventRecurrenceExclusionTable)).where(eventRecurrenceExclusionTable.ID.in(sqlSubQuery).and(eventRecurrenceExclusionTable.EVENT_ID.eq(selectedEventId)))).select(eventRecurrenceExclusionTable.ID)).fetch();
            if (distinctExclusionIds == null || distinctExclusionIds.size() == 0) {
                return new EventRecurrenceExclusionEntity[0];
            }
            ArrayList resultList = new ArrayList(distinctExclusionIds.size());
            Iterators.partition(distinctExclusionIds.iterator(), (int)1000).forEachRemaining(distinctExclusionIdsBatch -> {
                String placeholderCommaList = distinctExclusionIdsBatch.stream().map(id -> "?").collect(Collectors.joining(","));
                resultList.addAll(Lists.newArrayList((Object[])((EventRecurrenceExclusionEntity[])this.activeObjects.find(EventRecurrenceExclusionEntity.class, Query.select().where("ID IN (" + placeholderCommaList + ")", distinctExclusionIdsBatch.toArray()).order("EXCLUSION DESC")))));
            });
            return resultList.toArray(new EventRecurrenceExclusionEntity[0]);
        });
    }

    @Override
    public Map<Integer, Set<EventRecurrenceExclusionEntity>> getRecurrenceExclusions(List<EventEntity> eventEntities) {
        Objects.requireNonNull(eventEntities);
        HashMap<Integer, Set<EventRecurrenceExclusionEntity>> mapResult = new HashMap<Integer, Set<EventRecurrenceExclusionEntity>>(200);
        if (eventEntities.size() == 0) {
            return mapResult;
        }
        List selectedEventIds = eventEntities.stream().map(eventEntity -> eventEntity.getID()).collect(Collectors.toList());
        EventRecurrenceExclusionTable eventRecurrenceExclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        EventTable eventTable = this.queryDSLMapper.getEventsTable();
        Iterators.partition(selectedEventIds.iterator(), (int)1000).forEachRemaining(selectedEventIdsBatch -> this.queryDSLSupplier.executeSQLQuery(query -> {
            List batchResult = ((AbstractSQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)eventRecurrenceExclusionTable)).join((EntityPath)eventTable)).on((Predicate)eventRecurrenceExclusionTable.EVENT_ID.eq(eventTable.ID))).where(eventTable.ID.in((Collection<Integer>)selectedEventIdsBatch))).groupBy((Expression<?>[])new Expression[]{eventRecurrenceExclusionTable.EVENT_ID, eventRecurrenceExclusionTable.ID, eventRecurrenceExclusionTable.EVENT_ID, eventRecurrenceExclusionTable.EXCLUSION, eventRecurrenceExclusionTable.ALL_DAY})).select(Projections.constructor(EventRecurrenceExclusionEntityDTO.class, eventRecurrenceExclusionTable.ID, eventRecurrenceExclusionTable.EVENT_ID, eventRecurrenceExclusionTable.EXCLUSION, eventRecurrenceExclusionTable.ALL_DAY))).fetch();
            int currentEventId = -1;
            for (EventRecurrenceExclusionEntity eventRecurrenceExclusionEntity : batchResult) {
                if (currentEventId != eventRecurrenceExclusionEntity.getEvent().getID()) {
                    currentEventId = eventRecurrenceExclusionEntity.getEvent().getID();
                }
                Set eventRecurrenceExclusionListPerEvent = mapResult.computeIfAbsent(currentEventId, eventId -> new HashSet());
                eventRecurrenceExclusionListPerEvent.add(eventRecurrenceExclusionEntity);
            }
            return null;
        }));
        return mapResult;
    }

    @Override
    public boolean deleteEventRecurrenceExclusionEntities(EventEntity eventEntity) {
        EventRecurrenceExclusionTable eventRecurrenceExclusionTable = (EventRecurrenceExclusionTable)this.queryDSLMapper.getMapping(EventRecurrenceExclusionEntity.class);
        int selectedEventId = eventEntity.getID();
        return this.queryDSLSupplier.executeDeleteSQLClause(eventRecurrenceExclusionTable, sqlDeleteClause -> {
            long deletedRow = (sqlDeleteClause = sqlDeleteClause.where((Predicate)eventRecurrenceExclusionTable.EVENT_ID.eq(selectedEventId))).execute();
            return deletedRow > 0L;
        });
    }

    @Override
    public EventEntity createEventEntity(String subCalendarId, VEvent vEventComponent) {
        long utcEndDate;
        long utcStartDate;
        boolean isAllDay;
        DtStart startDateProperty = vEventComponent.getStartDate();
        Date startDate = startDateProperty.getDate();
        DtEnd endDateProperty = vEventComponent.getEndDate();
        Date endDate = endDateProperty.getDate();
        boolean bl = isAllDay = !(startDate instanceof DateTime) && !(endDate instanceof DateTime);
        if (isAllDay) {
            utcStartDate = CalendarUtil.getUtcDateTimeWithAllDay(startDate).getMillis();
            utcEndDate = CalendarUtil.getUtcDateTimeWithAllDay(endDate).getMillis();
        } else {
            utcStartDate = CalendarUtil.getUtcTime(startDateProperty).getTime();
            utcEndDate = CalendarUtil.getUtcTime(endDateProperty).getTime();
        }
        Uid uid = vEventComponent.getUid();
        String uidString = UUIDGenerate.generate();
        if (uid != null) {
            uidString = uid.getValue();
        }
        RecurrenceId recurrenceId = vEventComponent.getRecurrenceId();
        EventEntity eventEntity = (EventEntity)this.getActiveObjects().create(EventEntity.class, new DBParam[]{new DBParam("VEVENT_UID", (Object)uidString), new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("START", (Object)startDate.getTime()), new DBParam("END", (Object)endDate.getTime()), new DBParam("UTC_START", (Object)utcStartDate), new DBParam("UTC_END", (Object)utcEndDate), new DBParam("ALL_DAY", (Object)isAllDay), new DBParam("SUMMARY", (Object)DefaultActiveObjectsServiceWrapper.getPropertyValue(vEventComponent.getSummary())), new DBParam("DESCRIPTION", (Object)DefaultActiveObjectsServiceWrapper.getPropertyValue(vEventComponent.getDescription())), new DBParam("LOCATION", (Object)DefaultActiveObjectsServiceWrapper.getPropertyValue(vEventComponent.getLocation())), new DBParam("URL", (Object)DefaultActiveObjectsServiceWrapper.getPropertyValue(vEventComponent.getUrl())), new DBParam("ORGANISER", (Object)DefaultActiveObjectsServiceWrapper.getPropertyParameterValue(vEventComponent.getOrganizer(), "X-CONFLUENCE-USER-KEY")), new DBParam("RECURRENCE_RULE", (Object)DefaultActiveObjectsServiceWrapper.getPropertyValue(vEventComponent.getProperty("RRULE"))), new DBParam("RECURRENCE_ID_TIMESTAMP", recurrenceId == null ? null : Long.valueOf(recurrenceId.getDate().getTime())), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("LAST_MODIFIED", (Object)System.currentTimeMillis()), new DBParam("SEQUENCE", (Object)0)});
        return eventEntity;
    }

    @Override
    public void createInviteeEntity(EventEntity eventEntity, VEvent vEventComponent, UserAccessor userAccessor) {
        PropertyList attendees = vEventComponent.getProperties("ATTENDEE");
        InviteeEntity[] existingInvitees = eventEntity.getInvitees();
        for (Attendee attendee : attendees) {
            ConfluenceUser confluenceUser;
            Object userKeyParam = attendee.getParameter("X-CONFLUENCE-USER-KEY");
            if (userKeyParam == null || (confluenceUser = userAccessor.getUserByKey(new UserKey(StringUtils.defaultString(((Content)userKeyParam).getValue())))) == null) continue;
            String confluenceUserKey = confluenceUser.getKey().toString();
            Stream<InviteeEntity> existingInviteeStream = Arrays.stream(existingInvitees);
            Optional<InviteeEntity> existingInvitee = existingInviteeStream.filter(inviteeEntity -> inviteeEntity.getInviteeId().equals(confluenceUserKey)).findFirst();
            if (existingInvitee.isPresent()) continue;
            this.activeObjects.create(InviteeEntity.class, new DBParam[]{new DBParam("EVENT_ID", (Object)eventEntity.getID()), new DBParam("INVITEE_ID", (Object)confluenceUserKey)});
        }
    }

    @Override
    public void createEventRecurrenceExclusionEntity(EventEntity eventEntity, VEvent vEventComponent) {
        PropertyList exDates = vEventComponent.getProperties("EXDATE");
        EventRecurrenceExclusionEntity[] excludedEvents = eventEntity.getExclusions();
        for (ExDate exDate : exDates) {
            DateList excludedDates = exDate.getDates();
            if (excludedDates == null) continue;
            for (Date excludedDate : excludedDates) {
                Stream<EventRecurrenceExclusionEntity> existingExclusionStream = Arrays.stream(excludedEvents);
                Optional<EventRecurrenceExclusionEntity> existingExcludedEntity = existingExclusionStream.filter(excludedEntity -> excludedEntity.isAllDay() == !(excludedDate instanceof DateTime) && excludedDate.getTime() == excludedEntity.getExclusion()).findFirst();
                if (existingExcludedEntity.isPresent()) continue;
                this.activeObjects.create(EventRecurrenceExclusionEntity.class, new DBParam[]{new DBParam("EVENT_ID", (Object)eventEntity.getID()), new DBParam("EXCLUSION", (Object)excludedDate.getTime()), new DBParam("ALL_DAY", (Object)(!(excludedDate instanceof DateTime) ? 1 : 0))});
            }
        }
    }

    @Override
    public ExtraSubCalendarPropertyEntity createSubCalendarEntityProperty(SubCalendarEntity subCalendarEntity, String key, Object value) {
        return (ExtraSubCalendarPropertyEntity)this.activeObjects.create(ExtraSubCalendarPropertyEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarEntity.getID()), new DBParam("KEY", (Object)key), new DBParam("VALUE", value)});
    }

    @Override
    public SubCalendarEntity getSubCalendarEntity(EventEntity eventEntity) {
        HashMap<String, SubCalendarEntity> cachedSubCals;
        String subCalendarId = eventEntity.getSubCalendar().getID();
        HashMap<String, HashMap<String, SubCalendarEntity>> requestCache = RequestCacheThreadLocal.getRequestCache();
        if (requestCache == null) {
            requestCache = new HashMap<String, HashMap<String, SubCalendarEntity>>();
        }
        if ((cachedSubCals = (HashMap<String, SubCalendarEntity>)requestCache.get(SUB_CAL_REQUEST_CONTEXT_CACHE_KEY)) == null) {
            cachedSubCals = new HashMap<String, SubCalendarEntity>();
            requestCache.put(SUB_CAL_REQUEST_CONTEXT_CACHE_KEY, cachedSubCals);
        }
        SubCalendarEntity result = cachedSubCals.computeIfAbsent(subCalendarId, i -> (SubCalendarEntity)this.activeObjects.get(SubCalendarEntity.class, i));
        return result;
    }

    private static String getPropertyValue(Property property) {
        return property == null ? null : property.getValue();
    }

    private static String getPropertyParameterValue(Property property, String parameterName) {
        if (property == null) {
            return null;
        }
        Object propertyParam = property.getParameter(parameterName);
        return propertyParam == null ? null : ((Content)propertyParam).getValue();
    }
}

