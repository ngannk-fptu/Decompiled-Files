/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  net.java.ao.DBParam
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.Entity
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Days
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrievalMode;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.QueryDSLWhereTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.EntityTimeRangeOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.KeyStoreToEventTypeMapper;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.ReminderPeriods;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.DisableEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderUsersEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarInSpaceEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.EventDTO;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.JiraReminderEventDTO;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.TransactionalQueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.CustomEventTypeTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.EventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.InviteeTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.JiraReminderEventTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.ReminderSettingTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.ReminderUserTable;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.SubCalendarTable;
import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.EventEntitiesToEventsTransformer;
import com.atlassian.confluence.extra.calendar3.util.RecurrenceIdJodaTimeHelper;
import com.atlassian.confluence.extra.calendar3.util.UUIDGenerate;
import com.atlassian.confluence.extra.calendar3.wrapper.UserAccessorWrapper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fortuna.ical4j.extensions.property.WrCalDesc;
import net.fortuna.ical4j.extensions.property.WrCalName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.Entity;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCalendarDataStore<T extends PersistedSubCalendar>
implements CalendarDataStore<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCalendarDataStore.class);
    public static final String SPACE_NAME_CACHE = "space.name.cache";
    private static final String[] RECURRENCE_RULE_COMPARISON_CRITERIA = new String[]{"FREQ", "BYDAY", "INTERVAL"};
    protected static final Set<String> CONFLUENCE_ADMINISTRATORS_GROUP_NAMES = Collections.unmodifiableSet(Sets.newHashSet((Object[])new String[]{"confluence-administrators"}));
    protected final String CONFLUENCE_SITE_ADMINISTRATORS_GROUP = "site-admins";
    private static final int UPCOMING_EVENT_REMINDER_LIMIT = Integer.getInteger("com.atlassian.confluence.extra.calendar3.upcoming.event.reminder.limit", 2000);
    protected final DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor;
    private QueryDSLSupplier queryDSLSupplier;

    protected AbstractCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        this.dataStoreCommonPropertyAccessor = dataStoreCommonPropertyAccessor;
        this.queryDSLSupplier = new TransactionalQueryDSLSupplier(dataStoreCommonPropertyAccessor.getTransactionalExecutorFactory(), dataStoreCommonPropertyAccessor.getSystemInformationService());
    }

    @VisibleForTesting
    public void setQueryDSLSupplier(QueryDSLSupplier queryDSLSupplier) {
        this.queryDSLSupplier = queryDSLSupplier;
    }

    protected ActiveObjectsServiceWrapper getActiveObjectsServiceWrapper() {
        return this.dataStoreCommonPropertyAccessor.getActiveObjectsServiceWrapper();
    }

    protected SettingsManager getSettingsManager() {
        return this.dataStoreCommonPropertyAccessor.getSettingsManager();
    }

    protected UserAccessor getUserAccessor() {
        return this.dataStoreCommonPropertyAccessor.getUserAccessor();
    }

    protected UserAccessorWrapper getCachingUserAccessorHelper() {
        return this.dataStoreCommonPropertyAccessor.getCachingUserAccessorHelper();
    }

    protected SpaceManager getSpaceManager() {
        return this.dataStoreCommonPropertyAccessor.getSpaceManager();
    }

    protected SpacePermissionManager getSpacePermissionManager() {
        return this.dataStoreCommonPropertyAccessor.getSpacePermissionManager();
    }

    protected LocaleManager getLocaleManager() {
        return this.dataStoreCommonPropertyAccessor.getLocaleManager();
    }

    protected CalendarSettingsManager getCalendarSettingsManager() {
        return this.dataStoreCommonPropertyAccessor.getCalendarSettingsManager();
    }

    protected I18NBeanFactory getI18NBeanFactory() {
        return this.dataStoreCommonPropertyAccessor.getI18NBeanFactory();
    }

    protected SubCalendarEventTransformerFactory getSubCalendarEventTransformerFactory() {
        return this.dataStoreCommonPropertyAccessor.getSubCalendarEventTransformerFactory();
    }

    protected JodaIcal4jTimeZoneMapper getJodaIcal4jTimeZoneMapper() {
        return this.dataStoreCommonPropertyAccessor.getJodaIcal4jTimeZoneMapper();
    }

    protected JodaIcal4jDateTimeConverter getJodaIcal4jDateTimeConverter() {
        return this.dataStoreCommonPropertyAccessor.getJodaIcal4jDateTimeConverter();
    }

    protected QueryDSLMapper getQueryDSLMapper() {
        return this.dataStoreCommonPropertyAccessor.getQueryDSLMapper();
    }

    protected TransactionTemplate getTransactionTemplate() {
        return this.dataStoreCommonPropertyAccessor.getTransactionTemplate();
    }

    protected AsynchronousTaskExecutor getExecutor() {
        return this.dataStoreCommonPropertyAccessor.getExecutor();
    }

    protected VEventMapper getvEventMapper() {
        VEventMapper vEventMapper = this.dataStoreCommonPropertyAccessor.getvEventMapper();
        vEventMapper.setCustomEventTypeSupport(this);
        return vEventMapper;
    }

    protected QueryDSLWhereTransformer getQueryDSLWhereTransformer() {
        return this.dataStoreCommonPropertyAccessor.getQueryDSLWhereTransformer();
    }

    protected PropertyToDBFieldMapperSupplier getPropertyToDBFieldMapperSupplier() {
        return this.dataStoreCommonPropertyAccessor.getPropertyToDBFieldMapperSupplier();
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        String subCalendarName = subCalendar.getName();
        if (StringUtils.isNotBlank(subCalendarName) && subCalendarName.length() > 255) {
            this.addFieldError(fieldErrors, "name", this.getI18NBean().getText("calendar3.error.name.toolong", (List)Lists.newArrayList((Object[])new Integer[]{255})));
        }
        if (!this.isValidSpaceKey(subCalendar)) {
            this.addFieldError(fieldErrors, "spaceKeyAutocomplete", this.getI18NBean().getText("calendar3.error.invalidspacekey"));
        }
    }

    private boolean isValidSpaceKey(SubCalendar subCalendar) {
        boolean isValidSpaceKey = true;
        if (!CalendarUtil.isJiraSubCalendarType(subCalendar.getType())) {
            String spaceKey = subCalendar.getSpaceKey();
            if (StringUtils.isBlank(spaceKey)) {
                if (subCalendar instanceof PersistedSubCalendar) {
                    isValidSpaceKey = false;
                } else if (!"internal-subscription".equals(subCalendar.getType())) {
                    isValidSpaceKey = false;
                }
            } else if (this.getSpaceManager().getSpace(spaceKey) == null) {
                isValidSpaceKey = false;
            }
        }
        return isValidSpaceKey;
    }

    protected abstract String getStoreKey();

    @Override
    public T save(SubCalendar subCalendar) {
        SubCalendarEntity subCalendarEntity;
        if (subCalendar instanceof PersistedSubCalendar) {
            subCalendarEntity = this.toStorageFormat(subCalendar);
            subCalendarEntity.save();
        } else {
            subCalendarEntity = this.toStorageFormat(subCalendar);
        }
        return this.fromStorageFormat(subCalendarEntity);
    }

    protected abstract T fromStorageFormat(SubCalendarEntity var1);

    protected SubCalendarEntity toStorageFormat(SubCalendar subCalendar) {
        PersistedSubCalendar parentSubCalendar = subCalendar.getParent();
        String parentId = this.getParentId(parentSubCalendar);
        String spaceKey = StringUtils.defaultIfBlank(subCalendar.getSpaceKey(), null);
        if (!(subCalendar instanceof PersistedSubCalendar)) {
            SubCalendarEntity subCalendarEntity = (SubCalendarEntity)this.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getStoreKey()), new DBParam("ID", (Object)UUIDGenerate.generate()), new DBParam("PARENT_ID", (Object)parentId), new DBParam("NAME", (Object)subCalendar.getName()), new DBParam("DESCRIPTION", (Object)StringUtils.defaultString(subCalendar.getDescription())), new DBParam("COLOUR", (Object)subCalendar.getColor()), new DBParam("SPACE_KEY", (Object)(this.isParentCalendar(parentId) ? spaceKey : null)), new DBParam("TIME_ZONE_ID", (Object)subCalendar.getTimeZoneId()), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", (Object)AuthenticatedUserThreadLocal.get().getKey().toString()), new DBParam("USING_CUSTOM_EVENT_TYPE_ID", (Object)StringUtils.defaultIfBlank(subCalendar.getCustomEventTypeId(), null))});
            this.addCalendarToSpaceView(parentId, subCalendarEntity.getID(), spaceKey);
            return subCalendarEntity;
        }
        PersistedSubCalendar toUpdate = (PersistedSubCalendar)subCalendar;
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(toUpdate.getId());
        String oldSpaceKey = StringUtils.defaultIfBlank(subCalendarEntity.getSpaceKey(), null);
        subCalendarEntity.setName(subCalendar.getName());
        subCalendarEntity.setDescription(StringUtils.defaultString(subCalendar.getDescription()));
        subCalendarEntity.setColour(subCalendar.getColor());
        subCalendarEntity.setSpaceKey(this.isParentCalendar(parentId) ? spaceKey : null);
        subCalendarEntity.setTimeZoneId(subCalendar.getTimeZoneId());
        subCalendarEntity.setLastModified(System.currentTimeMillis());
        subCalendarEntity.save();
        if (!StringUtils.equals(spaceKey, oldSpaceKey)) {
            this.removeSubCalendarFromSpaceView(subCalendarEntity, oldSpaceKey);
        }
        this.addCalendarToSpaceView(parentId, toUpdate.getId(), spaceKey);
        return subCalendarEntity;
    }

    @Override
    public void addCalendarsToSpaceView(Set<String> calendarIds, String spaceKey) {
        if (StringUtils.isNotBlank(spaceKey) && calendarIds != null) {
            Set<String> existedCalendarIds = this.getSubCalendarIdsOnSpace(spaceKey);
            calendarIds.removeAll(existedCalendarIds);
            if (calendarIds.size() > 0) {
                for (String calendarId : calendarIds) {
                    try {
                        if (!this.isSubCalendarExisted(calendarId)) continue;
                        this.getActiveObjects().create(SubCalendarInSpaceEntity.class, new DBParam[]{new DBParam("SPACE_KEY", (Object)spaceKey), new DBParam("SUB_CALENDAR_ID", (Object)calendarId)});
                    }
                    catch (Exception e) {
                        LOG.warn(String.format("Calendar which has id %s doesn't exist, maybe someone has deleted it", calendarId));
                    }
                }
            }
        }
    }

    @Override
    public void removeSubCalendarRestrictions(String userKey) {
        ActiveObjects activeObjects = this.getActiveObjects();
        SubCalendarUserRestrictionEntity[] entities = (SubCalendarUserRestrictionEntity[])activeObjects.find(SubCalendarUserRestrictionEntity.class, Query.select().limit(1));
        if (entities != null && entities.length > 0) {
            DatabaseProvider provider = entities[0].getEntityManager().getProvider();
            activeObjects.deleteWithSQL(SubCalendarUserRestrictionEntity.class, provider.quote("USER_KEY") + " = ?", new Object[]{userKey});
        }
    }

    @Override
    public void deleteInviteeFromAllEvents(String userKey) {
        this.getActiveObjectsServiceWrapper().deleteInviteeFromAllEvents(userKey);
    }

    protected void addCalendarToSpaceView(String parentId, String calendarId, String spaceKey) {
        SubCalendarInSpaceEntity[] subCalendarInSpaceEntities;
        if (StringUtils.isEmpty(parentId) && StringUtils.isNotBlank(spaceKey) && this.isSubCalendarExisted(calendarId) && ((subCalendarInSpaceEntities = (SubCalendarInSpaceEntity[])this.getActiveObjects().find(SubCalendarInSpaceEntity.class, Query.select().where("SPACE_KEY = ? AND SUB_CALENDAR_ID = ?", new Object[]{spaceKey, calendarId}))) == null || subCalendarInSpaceEntities.length == 0)) {
            this.getActiveObjects().create(SubCalendarInSpaceEntity.class, new DBParam[]{new DBParam("SPACE_KEY", (Object)spaceKey), new DBParam("SUB_CALENDAR_ID", (Object)calendarId)});
        }
    }

    protected String getParentId(PersistedSubCalendar parentSubCalendar) {
        if (parentSubCalendar != null) {
            return this.getParentId(this.getSubCalendarEntity(parentSubCalendar.getId()));
        }
        return null;
    }

    private String getParentId(SubCalendarEntity parentSubCalendarEntity) {
        if (parentSubCalendarEntity.getSubscription() == null) {
            return parentSubCalendarEntity.getID();
        }
        return this.getParentId(parentSubCalendarEntity.getSubscription());
    }

    protected ActiveObjects getActiveObjects() {
        return this.getActiveObjectsServiceWrapper().getActiveObjects();
    }

    @Override
    public void remove(T subCalendar) {
        this.removeSubCalendar(this.getSubCalendarEntity(((PersistedSubCalendar)subCalendar).getId()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SubCalendarEntity getSubCalendarEntity(String id) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getSubCalendarEntity()");
        try {
            Collection<SubCalendarEntity> results = this.getSubCalendarBy(Query.select().where("ID = ?", new Object[]{id}));
            Option firstMatchSubCalendarEntity = com.atlassian.fugue.Iterables.first(results);
            SubCalendarEntity subCalendarEntity = (SubCalendarEntity)firstMatchSubCalendarEntity.getOrNull();
            return subCalendarEntity;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.getSubCalendarEntity()");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SubCalendarInSpaceEntity getSubCalendaInSpaceEntity(String subCalendarId) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getSubCalendarInSpaceEntity()");
        try {
            Query query = Query.select().where("SUB_CALENDAR_ID = ?", new Object[]{subCalendarId});
            ArrayList results = Lists.newArrayList((Object[])((SubCalendarInSpaceEntity[])this.getActiveObjects().find(SubCalendarInSpaceEntity.class, query)));
            Option firstMatchSubCalendarEntity = com.atlassian.fugue.Iterables.first((Iterable)results);
            SubCalendarInSpaceEntity subCalendarInSpaceEntity = (SubCalendarInSpaceEntity)firstMatchSubCalendarEntity.getOrNull();
            return subCalendarInSpaceEntity;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.getSubCalendarInSpaceEntity()");
        }
    }

    @Override
    public Option<T> getChildSubCalendarByStoreKey(T parentSubCalendar, String storeKey) {
        Option firstMatchSubCalendar = com.atlassian.fugue.Iterables.first(this.getSubCalendarBy(Query.select().where("STORE_KEY = ? AND PARENT_ID = ?", new Object[]{storeKey, ((PersistedSubCalendar)parentSubCalendar).getId()})));
        if (firstMatchSubCalendar.isEmpty()) {
            return Option.none();
        }
        return Option.option(this.fromStorageFormat((SubCalendarEntity)firstMatchSubCalendar.get()));
    }

    @Override
    public Option<T> getChildSubCalendarByCustomEventTypeId(T parentSubCalendar, String customEventTypeId) {
        Option firstMatchSubCalendar = com.atlassian.fugue.Iterables.first(this.getSubCalendarBy(Query.select().where("USING_CUSTOM_EVENT_TYPE_ID = ? AND PARENT_ID = ?", new Object[]{customEventTypeId, ((PersistedSubCalendar)parentSubCalendar).getId()})));
        if (firstMatchSubCalendar.isEmpty()) {
            return Option.none();
        }
        return Option.option(this.fromStorageFormat((SubCalendarEntity)firstMatchSubCalendar.get()));
    }

    protected Collection<SubCalendarEntity> getSubCalendarBy(Query query) {
        return Lists.newArrayList((Object[])((SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, query)));
    }

    private void removeSubCalendar(SubCalendarEntity subCalendarEntity) {
        SubCalendarEntity[] childSubCalendarEntities;
        SubCalendarEntity[] subscribers;
        if ("com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore".equals(subCalendarEntity.getStoreKey())) {
            LOG.info("Removing calendar [{}]", (Object)subCalendarEntity.getName());
        }
        if ((subscribers = subCalendarEntity.getSubscribers()) != null) {
            for (SubCalendarEntity subscriber : subscribers) {
                this.removeSubCalendar(subscriber);
            }
        }
        if ((childSubCalendarEntities = subCalendarEntity.getChildSubCalendarEntities()) != null) {
            for (SubCalendarEntity childSubCalendarEntity : childSubCalendarEntities) {
                this.removeSubCalendar(childSubCalendarEntity);
            }
        }
        this.removeSubCalendarRestrictions(subCalendarEntity);
        this.removeSubCalendarProperties(subCalendarEntity);
        this.removeSubCalendarEvents(subCalendarEntity);
        this.removeSubcalendarDisableEventType(subCalendarEntity);
        this.removeSubcalendarCustomEventType(subCalendarEntity);
        this.removeReminderSetting(subCalendarEntity);
        this.removeReminderUser(subCalendarEntity);
        this.removeJiraEventReminder(subCalendarEntity);
        this.removeSubCalendarFromAllSpaceViews(subCalendarEntity);
        this.getActiveObjects().delete(new RawEntity[]{subCalendarEntity});
    }

    private void removeSubCalendarEvents(SubCalendarEntity subCalendarEntity) {
        this.removeSubCalendarEvents(subCalendarEntity.getID());
    }

    private void removeSubCalendarEvents(String subCalendarId) {
        ActiveObjects activeObjects = this.getActiveObjects();
        SubCalendarEntity subCalendarEntity = ((SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().where("ID = ? ", new Object[]{subCalendarId})))[0];
        DatabaseProvider currentDatabaseProvider = subCalendarEntity.getEntityManager().getProvider();
        ArrayList removeEventIds = Lists.newArrayList();
        activeObjects.stream(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ?", new Object[]{subCalendarId}), eventEntity -> removeEventIds.add(eventEntity.getID()));
        Iterators.partition(removeEventIds.iterator(), (int)1000).forEachRemaining(removeEventIdsBatch -> {
            String removeByEventIdSql = currentDatabaseProvider.quote("EVENT_ID") + " IN (" + StringUtils.repeat("?", ", ", removeEventIdsBatch.size()) + ")";
            activeObjects.deleteWithSQL(InviteeEntity.class, removeByEventIdSql, removeEventIdsBatch.toArray());
            activeObjects.deleteWithSQL(EventRecurrenceExclusionEntity.class, removeByEventIdSql, removeEventIdsBatch.toArray());
        });
        activeObjects.deleteWithSQL(EventEntity.class, currentDatabaseProvider.quote("SUB_CALENDAR_ID") + " = ?", new Object[]{subCalendarId});
    }

    private void removeSubCalendarProperties(SubCalendarEntity subCalendarEntity) {
        ExtraSubCalendarPropertyEntity[] subCalendarPropertyEntities = subCalendarEntity.getExtraProperties();
        if (subCalendarPropertyEntities != null) {
            this.getActiveObjects().delete((RawEntity[])subCalendarPropertyEntities);
        }
    }

    private void removeSubcalendarDisableEventType(SubCalendarEntity subCalendarEntity) {
        DisableEventTypeEntity[] disableEventTypeEntities = subCalendarEntity.getDisableEventTypes();
        if (disableEventTypeEntities != null && disableEventTypeEntities.length > 0) {
            this.getActiveObjects().delete((RawEntity[])disableEventTypeEntities);
        }
    }

    private void removeSubcalendarCustomEventType(SubCalendarEntity subCalendarEntity) {
        RawEntity[] customEventTypeEntities = subCalendarEntity.getAvailableCustomEventTypes();
        if (customEventTypeEntities != null && customEventTypeEntities.length > 0) {
            this.getActiveObjects().delete(customEventTypeEntities);
        }
    }

    private void removeReminderSetting(RawEntity rawEntity) {
        DatabaseProvider databaseProvider = rawEntity.getEntityManager().getProvider();
        if (rawEntity instanceof SubCalendarEntity) {
            SubCalendarEntity parentSubCalEntity = ((SubCalendarEntity)rawEntity).getParent();
            if (parentSubCalEntity != null) {
                this.getActiveObjects().deleteWithSQL(ReminderSettingEntity.class, databaseProvider.quote("SUB_CALENDAR_ID") + " = ? AND " + databaseProvider.quote("STORE_KEY") + " = ?", new Object[]{parentSubCalEntity.getID(), ((SubCalendarEntity)rawEntity).getStoreKey()});
            }
        } else if (rawEntity instanceof CustomEventTypeEntity) {
            this.getActiveObjects().deleteWithSQL(ReminderSettingEntity.class, databaseProvider.quote("CUSTOM_EVENT_TYPE_ID") + " = ?", new Object[]{((CustomEventTypeEntity)rawEntity).getID()});
        }
    }

    private void removeSubCalendarFromAllSpaceViews(SubCalendarEntity subCalendarEntity) {
        DatabaseProvider databaseProvider = subCalendarEntity.getEntityManager().getProvider();
        this.getActiveObjects().deleteWithSQL(SubCalendarInSpaceEntity.class, databaseProvider.quote("SUB_CALENDAR_ID") + " = ?", new Object[]{subCalendarEntity.getID()});
    }

    @Override
    protected void removeSubCalendarFromSpaceView(SubCalendarEntity subCalendarEntity, String spaceKey) {
        if (subCalendarEntity.getParent() == null && StringUtils.isNotBlank(spaceKey)) {
            DatabaseProvider databaseProvider = subCalendarEntity.getEntityManager().getProvider();
            this.getActiveObjects().deleteWithSQL(SubCalendarInSpaceEntity.class, databaseProvider.quote("SUB_CALENDAR_ID") + " = ? AND " + databaseProvider.quote("SPACE_KEY") + " = ?", new Object[]{subCalendarEntity.getID(), spaceKey});
        }
    }

    private void removeReminderUser(SubCalendarEntity subCalendarEntity) {
        DatabaseProvider databaseProvider = subCalendarEntity.getEntityManager().getProvider();
        this.getActiveObjects().deleteWithSQL(ReminderUsersEntity.class, databaseProvider.quote("SUB_CALENDAR_ID") + "  = ? ", new Object[]{subCalendarEntity.getID()});
    }

    private void removeJiraEventReminder(SubCalendarEntity subCalendarEntity) {
        String storeKey = subCalendarEntity.getStoreKey();
        if (StringUtils.isNotEmpty(storeKey) && CalendarUtil.isJiraStoreKey(storeKey)) {
            DatabaseProvider databaseProvider = subCalendarEntity.getEntityManager().getProvider();
            this.getActiveObjects().deleteWithSQL(JiraReminderEventEntity.class, databaseProvider.quote("SUB_CALENDAR_ID") + "  = ? ", new Object[]{subCalendarEntity.getID()});
        }
    }

    private void removeSubCalendarRestrictions(SubCalendarEntity subCalendarEntity) {
        ActiveObjects activeObjects = this.getActiveObjects();
        SubCalendarUserRestrictionEntity[] userRestrictionEntities = subCalendarEntity.getPrivilegedUsers();
        if (userRestrictionEntities != null) {
            activeObjects.delete((RawEntity[])userRestrictionEntities);
        }
        SubCalendarGroupRestrictionEntity[] groupRestrictionEntities = subCalendarEntity.getPrivilegedGroups();
        if (userRestrictionEntities != null) {
            activeObjects.delete((RawEntity[])groupRestrictionEntities);
        }
    }

    @Override
    public T getSubCalendar(String subCalendarId) {
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(subCalendarId);
        return subCalendarEntity != null ? (T)this.fromStorageFormat(subCalendarEntity) : null;
    }

    @Override
    public List<T> getSubCalendarsWithRestriction(String ... subCalendarIds) {
        return this.loadRestrictions(this.getSubCalendarsInternal(subCalendarIds));
    }

    @Override
    public List<T> loadRestrictions(List<T> persistedEntities) {
        ArrayList groupRestrictionEntities = Lists.newArrayList((Object[])((SubCalendarGroupRestrictionEntity[])this.getActiveObjects().find(SubCalendarGroupRestrictionEntity.class)));
        ArrayList userRestrictionEntities = Lists.newArrayList((Object[])((SubCalendarUserRestrictionEntity[])this.getActiveObjects().find(SubCalendarUserRestrictionEntity.class)));
        for (PersistedSubCalendar persistedCalendar : persistedEntities) {
            this.loadRestrictions(persistedCalendar, groupRestrictionEntities, userRestrictionEntities);
            this.loadAdditionalRestrictions(persistedCalendar, groupRestrictionEntities, userRestrictionEntities);
        }
        return persistedEntities;
    }

    protected void loadAdditionalRestrictions(PersistedSubCalendar persistedCalendar, List<SubCalendarGroupRestrictionEntity> groupRestrictionEntities, List<SubCalendarUserRestrictionEntity> userRestrictionEntities) {
        PersistedSubCalendar persistedParentCalendar = persistedCalendar.getParent();
        if (persistedParentCalendar != null) {
            this.loadRestrictions(persistedParentCalendar, groupRestrictionEntities, userRestrictionEntities);
        }
    }

    protected void loadRestrictions(PersistedSubCalendar persistedCalendar, List<SubCalendarGroupRestrictionEntity> groupRestrictionEntities, List<SubCalendarUserRestrictionEntity> userRestrictionEntities) {
        String subCalendarId = persistedCalendar.getId();
        Collection calendarGroupRestrictionEntities = Collections2.filter(groupRestrictionEntities, subCalendarGroupRestrictionEntity -> subCalendarGroupRestrictionEntity.getSubCalendar().getID().equals(subCalendarId));
        Collection calendarUserRestrictionEntities = Collections2.filter(userRestrictionEntities, subCalendarUserRestrictionEntity -> subCalendarUserRestrictionEntity.getSubCalendar().getID().equals(subCalendarId));
        persistedCalendar.setGroupRestrictionMap(this.fillRestriction(calendarGroupRestrictionEntities, SubCalendarGroupRestrictionEntity::getGroupName));
        persistedCalendar.setUserRestrictionMap(this.fillRestriction(calendarUserRestrictionEntities, SubCalendarUserRestrictionEntity::getUserKey));
    }

    private <TA extends SubCalendarRestrictionEntity> Map<String, List<String>> fillRestriction(Collection<TA> restrictions, Function<TA, String> extractor) {
        ArrayList viewRestriction = Lists.newArrayList();
        ArrayList editRestriction = Lists.newArrayList();
        for (SubCalendarRestrictionEntity restriction : restrictions) {
            if (restriction.getType().equals("VIEW")) {
                viewRestriction.add((String)extractor.apply((Object)restriction));
            }
            if (!restriction.getType().equals("EDIT")) continue;
            editRestriction.add((String)extractor.apply((Object)restriction));
        }
        HashMap returnMap = Maps.newHashMap();
        returnMap.put("VIEW", viewRestriction);
        returnMap.put("EDIT", editRestriction);
        return returnMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<T> getSubCalendarsInternal(String ... subCalendarIds) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getSubCalendars()");
        try {
            ArrayList<T> results = new ArrayList<T>();
            List<SubCalendarEntity> subCalendarEntities = this.getSubCalendarEntities(subCalendarIds);
            for (SubCalendarEntity subCalendarEntity : subCalendarEntities) {
                UtilTimerStack.push((String)("AbstractCalendarDataStore.getSubCalendars() -- entity " + subCalendarEntity.getID()));
                try {
                    if (subCalendarEntity == null) continue;
                    results.add(this.fromStorageFormat(subCalendarEntity));
                }
                finally {
                    UtilTimerStack.pop((String)("AbstractCalendarDataStore.getSubCalendars() -- entity " + subCalendarEntity.getID()));
                }
            }
            ArrayList<T> arrayList = results;
            return arrayList;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.getSubCalendars()");
        }
    }

    @Override
    public List<String> filterSubCalendarIds(String ... subCalendarIds) {
        if (subCalendarIds == null || subCalendarIds.length == 0) {
            LOG.debug("returning an empty list. subCalendarIds={}", (Object[])subCalendarIds);
            return Collections.emptyList();
        }
        QueryDSLMapper queryDSLMapper = this.getQueryDSLMapper();
        if (!queryDSLMapper.isReady()) {
            LOG.warn("QueryDSL is not ready for TC, fallback to AO");
            return this.filterSubCalendarIdsByAO(subCalendarIds);
        }
        LOG.debug("partitioning IDs: {}", (Object[])subCalendarIds);
        ArrayList<String> resultSubCalendarIds = new ArrayList<String>();
        try {
            SubCalendarTable SUB_CALENDAR = (SubCalendarTable)queryDSLMapper.getMapping(SubCalendarEntity.class);
            Iterables.partition((Iterable)Lists.newArrayList((Object[])subCalendarIds), (int)1000).forEach(batchSubCalendarIds -> {
                LOG.debug("Fetching sub calendar IDs: {}", batchSubCalendarIds);
                List batchResult = this.queryDSLSupplier.executeSQLQuery(query -> ((SQLQuery)((QueryBase)((Object)((SQLQuery)query.from((Expression<?>)SUB_CALENDAR)).select((Expression)SUB_CALENDAR.ID))).where((com.querydsl.core.types.Predicate)SUB_CALENDAR.ID.in(batchSubCalendarIds))).fetch());
                LOG.debug("Query result: {}", (Object)batchResult);
                resultSubCalendarIds.addAll(batchResult);
            });
        }
        catch (Exception e) {
            LOG.error("Error fetching using queryDSL", (Throwable)e);
        }
        if (subCalendarIds.length != 0 && resultSubCalendarIds.size() == 0) {
            List<String> resultSubCalendarIdsUsingAO = this.filterSubCalendarIdsByAO(subCalendarIds);
            if (resultSubCalendarIdsUsingAO.size() > 0) {
                LOG.warn("we couldn't find any calendar records in DB using QueryDSL based on the calendar IDS: {}. \n filterSubCalendarIdsByAO returned: {}.  \n resultSubCalendarIds returned by queryDSL: {}", new Object[]{subCalendarIds, resultSubCalendarIdsUsingAO, resultSubCalendarIds});
            } else {
                LOG.info("Could not find any calendar records in DB with calendar ids: {}", (Object)String.join((CharSequence)", ", subCalendarIds));
            }
            LOG.debug("Returning: {}", resultSubCalendarIdsUsingAO);
            return resultSubCalendarIdsUsingAO;
        }
        LOG.debug("Returning: {}", resultSubCalendarIds);
        return resultSubCalendarIds;
    }

    private List<String> filterSubCalendarIdsByAO(String ... subCalendarIds) {
        SubCalendarEntity[] subCalendarEntities;
        ArrayList<String> resultSubCalendarIds = new ArrayList<String>();
        for (SubCalendarEntity subCalendarEntity : subCalendarEntities = (SubCalendarEntity[])this.getActiveObjects().get(SubCalendarEntity.class, (Object[])subCalendarIds)) {
            if (subCalendarEntity == null) continue;
            resultSubCalendarIds.add(subCalendarEntity.getID());
        }
        return resultSubCalendarIds;
    }

    @Override
    public SubCalendarSummary getSubCalendarSummary(String subCalendarId) {
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(subCalendarId);
        if (subCalendarEntity != null) {
            return this.toSummary(subCalendarEntity);
        }
        return null;
    }

    @Override
    public List<SubCalendarSummary> getSubCalendarSummariesByStoreKey(String storeKey, int limit, int offset) {
        return Arrays.stream((SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("STORE_KEY = ?", new Object[]{storeKey}).order("ID ASC").limit(limit).offset(offset))).map(this::toSummary).collect(Collectors.toList());
    }

    @Override
    public Set<String> getAllParentSubCalendarIds(String spaceKey, int offset, int limit) {
        Query query = spaceKey == null || spaceKey.isEmpty() ? Query.select().where("SUBSCRIPTION_ID IS NULL AND PARENT_ID IS NULL", new Object[0]) : (spaceKey.equalsIgnoreCase("NULL") ? Query.select().where("SUBSCRIPTION_ID IS NULL AND PARENT_ID IS NULL AND SPACE_KEY IS NULL", new Object[0]) : Query.select().where("SUBSCRIPTION_ID IS NULL AND PARENT_ID IS NULL AND SPACE_KEY = ?", new Object[]{spaceKey}));
        HashSet<String> parentIds = new HashSet<String>();
        this.getActiveObjects().stream(SubCalendarEntity.class, query.limit(limit).offset(offset), subCalendarEntity1 -> parentIds.add(subCalendarEntity1.getID()));
        return parentIds;
    }

    @Override
    public Set<String> getAllParentSubCalendarIds() {
        SubCalendarEntity[] subCalendarEntities;
        HashSet<String> subCalendarSummaries = new HashSet<String>();
        for (SubCalendarEntity entity : subCalendarEntities = (SubCalendarEntity[])this.getActiveObjectsServiceWrapper().getActiveObjects().find(SubCalendarEntity.class, Query.select().where("SUBSCRIPTION_ID IS NULL AND PARENT_ID IS NULL", new Object[0]))) {
            subCalendarSummaries.add(entity.getID());
        }
        return subCalendarSummaries;
    }

    protected abstract SubCalendarSummary toSummary(SubCalendarEntity var1);

    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        Calendar subCalendarContent = null;
        String subCalendarId = ((PersistedSubCalendar)subCalendar).getId();
        if (this.hasSubCalendar(subCalendarId)) {
            LOG.debug(String.format("Getting sub-calendar %s events with ActiveObjects.", subCalendarId));
            EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ?", new Object[]{subCalendarId}));
            subCalendarContent = this.createEmptyCalendarForSubCalendar(subCalendar);
            this.addVEventComponents(subCalendarContent, eventEntities);
        }
        return subCalendarContent;
    }

    private void addVEventComponents(Calendar subCalendarContent, EventEntity[] eventEntities) {
        if (eventEntities != null && eventEntities.length > 0) {
            ComponentList<CalendarComponent> subCalendarContentComponents = subCalendarContent.getComponents();
            for (EventEntity eventEntity : eventEntities) {
                VEvent vEvent = this.getvEventMapper().toVEvent(eventEntity);
                this.correctRecurrenceId(eventEntity, vEvent);
                subCalendarContentComponents.add(vEvent);
            }
        }
    }

    protected void correctRecurrenceId(EventEntity eventEntity, VEvent vEvent) {
        RecurrenceId recurrenceId = vEvent.getRecurrenceId();
        if (recurrenceId != null) {
            Date newRecurrenceDate;
            if (eventEntity.isAllDay()) {
                newRecurrenceDate = new Date(eventEntity.getRecurrenceIdTimestamp());
            } else {
                DateTimeZone subCalendarTimeZone = DateTimeZone.forID((String)eventEntity.getSubCalendar().getTimeZoneId());
                DateTime jodaDate = new DateTime((Object)eventEntity.getRecurrenceIdTimestamp(), subCalendarTimeZone);
                newRecurrenceDate = CalendarUtil.toIcal4jDateTime(this.getJodaIcal4jTimeZoneMapper(), jodaDate);
            }
            recurrenceId.setDate(newRecurrenceDate);
        }
    }

    @Override
    public Calendar createEmptyCalendarForSubCalendar(T subCalendar) throws Exception {
        Preconditions.checkNotNull(subCalendar);
        PropertyList<Property> subCalendarProps = new PropertyList<Property>();
        subCalendarProps.add(new ProdId("-//Atlassian Confluence//Calendar Plugin 1.0//EN"));
        subCalendarProps.add(Version.VERSION_2_0);
        subCalendarProps.add(CalScale.GREGORIAN);
        subCalendarProps.add(new WrCalName(new ParameterList(), ((PersistedSubCalendar)subCalendar).getName()));
        subCalendarProps.add(new WrCalDesc(new ParameterList(), StringUtils.defaultString(((PersistedSubCalendar)subCalendar).getDescription()).replaceAll("((\\r\\n)|\\r|\\n)", "\\n")));
        String timezoneId = StringUtils.defaultIfEmpty(((PersistedSubCalendar)subCalendar).getTimeZoneId(), TimeZone.getDefault().getID());
        ComponentList<CalendarComponent> subCalendarComponents = new ComponentList<CalendarComponent>();
        TimeZone ical4jTimeZone = this.getJodaIcal4jTimeZoneMapper().toIcal4jTimeZone(timezoneId);
        if (ical4jTimeZone == null || ical4jTimeZone.getVTimeZone() == null) {
            ical4jTimeZone = this.getJodaIcal4jTimeZoneMapper().toIcal4jTimeZone(DateTimeZone.UTC.getID());
        }
        VTimeZone vTimeZoneComponent = ical4jTimeZone.getVTimeZone();
        subCalendarComponents.add(vTimeZoneComponent);
        TzId timezoneProperty = vTimeZoneComponent.getTimeZoneId();
        if (timezoneProperty != null) {
            timezoneId = ((Content)timezoneProperty).getValue();
        } else {
            LOG.warn("Could not get proper timezone from ical for joda timezone {}", (Object)timezoneId);
        }
        subCalendarProps.add(new XProperty("X-WR-TIMEZONE", new ParameterList(), timezoneId));
        subCalendarProps.add(new XProperty("X-MIGRATED-FOR-USER-KEY", new ParameterList(), Boolean.TRUE.toString()));
        return new Calendar(subCalendarProps, subCalendarComponents);
    }

    @Override
    public List<Message> getSubCalendarWarnings(T subCalendar) {
        return Collections.emptyList();
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarData) throws Exception {
        String subCalendarId = ((PersistedSubCalendar)subCalendar).getId();
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(subCalendarId);
        if (subCalendarEntity != null) {
            this.removeSubCalendarEvents(subCalendarEntity);
            ComponentList vEventComponents = subCalendarData.getComponents("VEVENT");
            if (vEventComponents != null) {
                ActiveObjectsServiceWrapper activeObjectsServiceWrapper = this.getActiveObjectsServiceWrapper();
                for (VEvent vEventComponent : vEventComponents) {
                    EventEntity eventEntity = activeObjectsServiceWrapper.createEventEntity(subCalendarEntity, vEventComponent);
                    activeObjectsServiceWrapper.createInviteeEntity(eventEntity, vEventComponent, this.getUserAccessor());
                    activeObjectsServiceWrapper.createEventRecurrenceExclusionEntity(eventEntity, vEventComponent);
                }
            }
        }
    }

    private static String getPropertyValue(Property property) {
        return property == null ? null : property.getValue();
    }

    protected String getTableName(Class<? extends RawEntity<?>> entityClass) {
        return this.getActiveObjectsServiceWrapper().getTableName(entityClass);
    }

    private boolean isSubCalendarExisted(String subCalendarId) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.isSubCalendarExisted()");
        try {
            boolean bl = this.getActiveObjects().count(SubCalendarEntity.class, Query.select().where("ID = ?", new Object[]{subCalendarId})) > 0;
            return bl;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.isSubCalendarExisted()");
        }
    }

    @Override
    public boolean hasSubCalendar(String subCalendarId) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.hasSubCalendar()");
        try {
            boolean bl = this.getActiveObjects().count(SubCalendarEntity.class, Query.select().where("ID = ? AND STORE_KEY = ?", new Object[]{subCalendarId, this.getStoreKey()})) > 0;
            return bl;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.hasSubCalendar()");
        }
    }

    @Override
    public boolean hasSubCalendar(PersistedSubCalendar subCalendar) {
        if (StringUtils.isEmpty(subCalendar.getStoreKey())) {
            return this.hasSubCalendar(subCalendar.getId());
        }
        return this.getStoreKey().equals(subCalendar.getStoreKey());
    }

    @Override
    public T getSubCalendar(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getSubCalendar()");
        try {
            T t = subCalendarEntity != null ? (T)this.fromStorageFormat(subCalendarEntity) : null;
            return t;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.getSubCalendar()");
        }
    }

    @Override
    public boolean hasSubCalendar(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.hasSubCalendar()");
        try {
            boolean bl = subCalendarEntity != null && this.getStoreKey().equals(subCalendarEntity.getStoreKey());
            return bl;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractCalendarDataStore.hasSubCalendar()");
        }
    }

    protected List<SubCalendarEntity> getSubCalendarEntities(String ... subCalendarIds) {
        ArrayList querySubstitutions = Lists.newArrayList(Arrays.asList(subCalendarIds));
        querySubstitutions.add(this.getStoreKey());
        return Arrays.asList((SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("ID IN (" + StringUtils.repeat("?", ", ", subCalendarIds.length) + ") AND STORE_KEY = ?", querySubstitutions.toArray())));
    }

    @Override
    public int getSubCalendarsCount() {
        return this.getActiveObjects().count(SubCalendarEntity.class, Query.select().where("STORE_KEY = ? ", new Object[]{this.getStoreKey()}));
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(String subCalendarId) {
        return this.getValidUsers(this.getPrivilegedUserKeys(subCalendarId, "EDIT"));
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(T subCalendar) {
        if (this.getUserRestrictionsMap(subCalendar) == null) {
            return this.getEventEditUserRestrictions((T)((PersistedSubCalendar)subCalendar).getId());
        }
        return this.getValidUsers((Collection<String>)this.getUserRestrictionsMap(subCalendar).get("EDIT"));
    }

    private Collection<String> getPrivilegedUserKeys(String subCalendarId, String restrictionType) {
        Object[] restrictionEntities = this.getSubCalendarUserRestrictionEntities(subCalendarId, restrictionType);
        if (restrictionEntities != null) {
            return Collections2.transform((Collection)Lists.newArrayList((Object[])restrictionEntities), SubCalendarUserRestrictionEntity::getUserKey);
        }
        return Collections.emptySet();
    }

    private SubCalendarUserRestrictionEntity[] getSubCalendarUserRestrictionEntities(String subCalendarId, String ... restrictionTypes) {
        return (SubCalendarUserRestrictionEntity[])this.getRestrictionEntities(subCalendarId, SubCalendarUserRestrictionEntity.class, restrictionTypes);
    }

    private <E extends Entity> E[] getRestrictionEntities(String subCalendarId, Class<E> entityClass, String ... restrictionTypes) {
        StringBuilder queryBuilder = new StringBuilder("SUB_CALENDAR_ID = ?");
        ArrayList querySubstitutions = Lists.newArrayList((Object[])new Object[]{subCalendarId});
        if (restrictionTypes != null && restrictionTypes.length > 0) {
            queryBuilder.append(" AND TYPE IN (").append(StringUtils.repeat("?", ", ", restrictionTypes.length)).append(")");
            querySubstitutions.addAll(Lists.newArrayList((Object[])restrictionTypes));
        }
        return (Entity[])this.getActiveObjects().find(entityClass, Query.select().where(queryBuilder.toString(), querySubstitutions.toArray()));
    }

    private Set<ConfluenceUser> getValidUsers(Collection<String> userKeys) {
        return Sets.newHashSet((Iterable)Collections2.filter((Collection)Collections2.transform(userKeys, this.getCachingUserAccessorHelper()::getUser), (com.google.common.base.Predicate)Predicates.notNull()));
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(String subCalendarId) {
        return this.getValidGroupNames(this.getPrivilegedGroupNames(subCalendarId, "EDIT"));
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(T subCalendar) {
        if (this.getGroupRestrictionMap(subCalendar) == null) {
            return this.getEventEditGroupRestrictions((T)((PersistedSubCalendar)subCalendar).getId());
        }
        return this.getValidGroupNames((Collection<String>)this.getGroupRestrictionMap(subCalendar).get("EDIT"));
    }

    private Collection<String> getPrivilegedGroupNames(String subCalendarId, String restrictionType) {
        Object[] restrictionEntities = this.getSubCalendarGroupRestrictionEntities(subCalendarId, restrictionType);
        if (restrictionEntities != null) {
            return this.getValidGroupNames(Collections2.transform((Collection)Lists.newArrayList((Object[])restrictionEntities), SubCalendarGroupRestrictionEntity::getGroupName));
        }
        return Collections.emptySet();
    }

    private SubCalendarGroupRestrictionEntity[] getSubCalendarGroupRestrictionEntities(String subCalendarId, String ... restrictionTypes) {
        return (SubCalendarGroupRestrictionEntity[])this.getRestrictionEntities(subCalendarId, SubCalendarGroupRestrictionEntity.class, restrictionTypes);
    }

    private Set<String> getValidGroupNames(Collection<String> groupNames) {
        return Sets.newHashSet((Iterable)Collections2.filter(groupNames, (com.google.common.base.Predicate)Predicates.and((com.google.common.base.Predicate)Predicates.notNull(), groupName -> this.getCachingUserAccessorHelper().getGroup((String)groupName) != null)));
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(String subCalendarId) {
        return this.getValidUsers(this.getPrivilegedUserKeys(subCalendarId, "VIEW"));
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(T subCalendar) {
        if (this.getUserRestrictionsMap(subCalendar) == null) {
            return this.getEventViewUserRestrictions((T)((PersistedSubCalendar)subCalendar).getId());
        }
        return this.getValidUsers((Collection<String>)this.getUserRestrictionsMap(subCalendar).get("VIEW"));
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(String subCalendarId) {
        return this.getValidGroupNames(this.getPrivilegedGroupNames(subCalendarId, "VIEW"));
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(T subCalendar) {
        if (this.getGroupRestrictionMap(subCalendar) == null) {
            return this.getEventViewGroupRestrictions((T)((PersistedSubCalendar)subCalendar).getId());
        }
        return this.getValidGroupNames((Collection<String>)this.getGroupRestrictionMap(subCalendar).get("VIEW"));
    }

    @Override
    public void restrictEventEditToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.recreateUserRestrictions(subCalendarId, "EDIT", users);
    }

    private void recreateUserRestrictions(String subCalendarId, String restrictionType, Set<ConfluenceUser> users) {
        ActiveObjects activeObjects = this.getActiveObjects();
        SubCalendarEntity subCalendarEntity = ((SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().where("ID = ? ", new Object[]{subCalendarId})))[0];
        DatabaseProvider currentDatabaseProvider = subCalendarEntity.getEntityManager().getProvider();
        activeObjects.deleteWithSQL(SubCalendarUserRestrictionEntity.class, currentDatabaseProvider.quote("SUB_CALENDAR_ID") + " = ? AND " + currentDatabaseProvider.quote("TYPE") + " = ?", new Object[]{subCalendarId, restrictionType});
        for (ConfluenceUser privilegedUser : users) {
            activeObjects.create(SubCalendarUserRestrictionEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("TYPE", (Object)restrictionType), new DBParam("USER_KEY", (Object)privilegedUser.getKey().toString())});
        }
    }

    @Override
    public void restrictEventEditToGroups(String subCalendarId, Set<String> groupNames) {
        this.recreateGroupRestrictions(subCalendarId, "EDIT", groupNames);
    }

    private void recreateGroupRestrictions(String subCalendarId, String restrictionType, Set<String> groupNames) {
        ActiveObjects activeObjects = this.getActiveObjects();
        SubCalendarEntity subCalendarEntity = ((SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().where("ID = ? ", new Object[]{subCalendarId})))[0];
        DatabaseProvider currentDatabaseProvider = subCalendarEntity.getEntityManager().getProvider();
        activeObjects.deleteWithSQL(SubCalendarGroupRestrictionEntity.class, currentDatabaseProvider.quote("SUB_CALENDAR_ID") + " = ? AND " + currentDatabaseProvider.quote("TYPE") + " = ?", new Object[]{subCalendarId, restrictionType});
        for (String groupName : groupNames) {
            activeObjects.create(SubCalendarGroupRestrictionEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("TYPE", (Object)restrictionType), new DBParam("GROUP_NAME", (Object)groupName)});
        }
    }

    @Override
    public void restrictEventViewToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.recreateUserRestrictions(subCalendarId, "VIEW", users);
    }

    @Override
    public void restrictEventViewToGroups(String subCalendarId, Set<String> groupNames) {
        this.recreateGroupRestrictions(subCalendarId, "VIEW", groupNames);
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        if (!this.hasSubCalendar(subCalendarId)) {
            throw new IllegalArgumentException(String.format("Sub-calendar %s is not managed by %s", subCalendarId, this.getClass().getName()));
        }
        return this.hasViewEventPermission(this.getSubCalendar(subCalendarId), user);
    }

    @Override
    public boolean hasViewEventPrivilege(T subCalendar, ConfluenceUser user) {
        if (this.getUserRestrictionsMap(subCalendar) == null || this.getGroupRestrictionMap(subCalendar) == null) {
            return this.hasViewEventPrivilege(((PersistedSubCalendar)subCalendar).getId(), user);
        }
        return this.hasViewEventPermission(subCalendar, user);
    }

    private boolean hasViewEventPermission(T subCalendar, ConfluenceUser user) {
        boolean userHasSpacePermissions;
        Space spaceSubCalendar = null;
        if (StringUtils.isNotEmpty(((PersistedSubCalendar)subCalendar).getSpaceKey())) {
            spaceSubCalendar = this.getSpaceManager().getSpace(((PersistedSubCalendar)subCalendar).getSpaceKey());
        }
        if (!(userHasSpacePermissions = this.getSpacePermissionManager().hasPermission(spaceSubCalendar != null ? "VIEWSPACE" : "USECONFLUENCE", spaceSubCalendar, (User)user))) {
            return false;
        }
        if (this.isUserSubCalendarCreator((PersistedSubCalendar)subCalendar, user) || this.isUserMemberOfOneGroup(user, CONFLUENCE_ADMINISTRATORS_GROUP_NAMES) || this.isUserSiteAdmin(user)) {
            return true;
        }
        Set<ConfluenceUser> eventViewUserRestrictions = this.getEventViewUserRestrictions(subCalendar);
        Set<String> eventViewGroupRestrictions = this.getEventViewGroupRestrictions(subCalendar);
        if (!eventViewUserRestrictions.isEmpty() || !eventViewGroupRestrictions.isEmpty()) {
            Boolean userDefinedOnRestrictions = eventViewUserRestrictions.contains(user) || this.isUserMemberOfOneGroup(user, eventViewGroupRestrictions);
            return userDefinedOnRestrictions;
        }
        return true;
    }

    protected boolean isUserMemberOfOneGroup(ConfluenceUser user, Set<String> groupNames) {
        if (groupNames.isEmpty()) {
            return false;
        }
        return !Collections2.filter(this.getCachingUserAccessorHelper().getUserGroups(user), groupNames::contains).isEmpty();
    }

    protected boolean isUserSiteAdmin(ConfluenceUser user) {
        if (this.getCalendarSettingsManager() == null || !this.getCalendarSettingsManager().areSiteAdminsEnabled()) {
            return false;
        }
        return !Collections2.filter(this.getCachingUserAccessorHelper().getUserGroups(user), "site-admins"::equals).isEmpty();
    }

    protected boolean isUserSubCalendarCreator(String subCalendarId, ConfluenceUser user) {
        if (user == null) {
            return false;
        }
        return this.getActiveObjects().count(SubCalendarEntity.class, Query.select().where("ID = ? AND ( CREATOR = ? OR CREATOR IS NULL )", new Object[]{subCalendarId, user.getKey().toString()})) == 1;
    }

    protected boolean isUserSubCalendarCreator(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        String creator = subCalendar.getCreator();
        if (!"com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore".equals(subCalendar.getStoreKey()) && subCalendar.getParent() != null) {
            LOG.debug("Getting creator for child SubCalendar, with id {}", (Object)subCalendar.getStoreKey());
            creator = subCalendar.getParent().getCreator();
        }
        if (user == null) {
            LOG.debug("Could not check isUserSubCalendarCreator for null user");
            return false;
        }
        if (StringUtils.isEmpty(creator)) {
            LOG.debug("Could not get creator from Calendar {}", (Object)subCalendar.getName());
            return false;
        }
        return creator.equals(user.getKey().toString());
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        boolean permitted = this.hasViewEventPrivilege(subCalendar, user);
        if (permitted) {
            boolean bl = permitted = this.isUserSubCalendarCreator((PersistedSubCalendar)subCalendar, user) || this.isUserMemberOfOneGroup(user, CONFLUENCE_ADMINISTRATORS_GROUP_NAMES) || this.isUserSiteAdmin(user);
            if (!permitted) {
                Set<ConfluenceUser> privilegedUsers = this.getEventEditUserRestrictions(subCalendar);
                Set<String> privilegedGroups = this.getEventEditGroupRestrictions(subCalendar);
                permitted = privilegedUsers.isEmpty() && privilegedGroups.isEmpty();
                if (!permitted) {
                    permitted = privilegedUsers.contains(user) || this.isUserMemberOfOneGroup(user, privilegedGroups);
                }
            }
        }
        return permitted;
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasEditEventPrivilege(subCalendar, user) || this.hasAdminPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasEditEventPrivilege(subCalendar, user);
    }

    protected String getSpaceName(String spaceKey) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map spaceNameThreadLocalCache = (Map)requestCache.computeIfAbsent(SPACE_NAME_CACHE, key -> new HashMap());
        return spaceNameThreadLocalCache.computeIfAbsent(spaceKey, key -> {
            Space space = this.getSpaceManager().getSpace(StringUtils.defaultString(spaceKey));
            return null == space ? spaceKey : space.getName();
        });
    }

    protected Set<String> getDisableEventType(SubCalendarEntity subCalendar) {
        HashSet<String> disableEventTypes = new HashSet<String>();
        if (subCalendar.getDisableEventTypes() != null) {
            for (DisableEventTypeEntity disableEventTypeEntity : subCalendar.getDisableEventTypes()) {
                disableEventTypes.add(disableEventTypeEntity.getEventKey());
            }
        }
        return disableEventTypes;
    }

    protected Set<CustomEventType> getCustomEventType(SubCalendarEntity subCalendarEntity) {
        HashSet<CustomEventType> customEventTypes = new HashSet<CustomEventType>();
        if (subCalendarEntity.getAvailableCustomEventTypes() != null) {
            Map<String, ReminderSettingEntity> reminderSettingEntityMap = this.getReminderSettingForCustomEvent(subCalendarEntity);
            for (CustomEventTypeEntity customEventTypeEntity : subCalendarEntity.getAvailableCustomEventTypes()) {
                long periodInMilis = reminderSettingEntityMap.containsKey(customEventTypeEntity.getID()) ? reminderSettingEntityMap.get(customEventTypeEntity.getID()).getPeriod() : 0L;
                customEventTypes.add(new CustomEventType(String.valueOf(customEventTypeEntity.getID()), customEventTypeEntity.getTitle(), customEventTypeEntity.getIcon(), customEventTypeEntity.getBelongSubCalendar() != null ? customEventTypeEntity.getBelongSubCalendar().getID() : null, customEventTypeEntity.getCreated(), ReminderPeriods.toReminderPeriod(periodInMilis).equals((Object)Option.none()) ? 0 : ((ReminderPeriods)((Object)ReminderPeriods.toReminderPeriod(periodInMilis).get())).getMins()));
            }
        }
        return customEventTypes;
    }

    protected Map<String, ReminderSettingEntity> getReminderSettingForCustomEvent(SubCalendarEntity subCalendarEntity) {
        ReminderSettingEntity[] reminderSettingEntities;
        HashMap reminderSettingMap = Maps.newHashMap();
        if (subCalendarEntity.getAvailableCustomEventTypes() == null || subCalendarEntity.getAvailableCustomEventTypes().length == 0) {
            return null;
        }
        ArrayList<String> customEventTypeIds = new ArrayList<String>();
        for (CustomEventTypeEntity customEventTypeEntity : subCalendarEntity.getAvailableCustomEventTypes()) {
            customEventTypeIds.add(customEventTypeEntity.getID());
        }
        for (ReminderSettingEntity reminderSettingEntity : reminderSettingEntities = (ReminderSettingEntity[])this.getActiveObjects().find(ReminderSettingEntity.class, Query.select().where("CUSTOM_EVENT_TYPE_ID IN (" + StringUtils.repeat("?", ", ", customEventTypeIds.size()) + ")", customEventTypeIds.toArray()))) {
            reminderSettingMap.put(reminderSettingEntity.getCustomEventTypeID(), reminderSettingEntity);
        }
        return reminderSettingMap;
    }

    protected Set<EventTypeReminder> getReminderSettingForSanboxEventType(SubCalendarEntity subCalendarEntity) {
        HashSet<EventTypeReminder> eventTypeReminders = new HashSet<EventTypeReminder>();
        ReminderSettingEntity[] reminderSettingEntities = (ReminderSettingEntity[])this.getActiveObjects().find(ReminderSettingEntity.class, Query.select().where("CUSTOM_EVENT_TYPE_ID IS NULL AND SUB_CALENDAR_ID = ? ", new Object[]{subCalendarEntity.getID()}));
        if (reminderSettingEntities != null && reminderSettingEntities.length > 0) {
            for (ReminderSettingEntity reminderSettingEntity : reminderSettingEntities) {
                eventTypeReminders.add(new EventTypeReminder(CalendarUtil.getEventTypeFromStoreKey(reminderSettingEntity.getStoreKey()), ReminderPeriods.toReminderPeriod(reminderSettingEntity.getPeriod()).equals((Object)Option.none()) ? 0 : ((ReminderPeriods)((Object)ReminderPeriods.toReminderPeriod(reminderSettingEntity.getPeriod()).get())).getMins(), false));
            }
        }
        return eventTypeReminders;
    }

    @Override
    public SubCalendarEvent transform(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.getSubCalendarEventTransformerFactory().getDefaultTransformer().transform(toBeTransformed, currentUser, new SubCalendarEventTransformerFactory.TransformParameters(){

            @Override
            public VEvent getRawEvent() {
                return raw;
            }

            @Override
            public boolean isReadOnly() {
                return !toBeTransformed.isEditable();
            }
        });
    }

    protected void addFieldError(Map<String, List<String>> fieldErrors, String field, String msg) {
        List<String> errorMessages;
        if (fieldErrors.containsKey(field)) {
            errorMessages = fieldErrors.get(field);
        } else {
            errorMessages = new ArrayList<String>();
            fieldErrors.put(field, errorMessages);
        }
        if (!errorMessages.contains(msg)) {
            errorMessages.add(msg);
        }
    }

    @Override
    public Message getTypeSpecificText(T subCalendar, Message originalMessage) {
        return originalMessage;
    }

    protected abstract String getType();

    protected I18NBean getI18NBean() {
        return this.getI18NBean(this.getCurrentUserLocale());
    }

    protected I18NBean getI18NBean(Locale locale) {
        I18NBeanFactory i18NBeanFactory = this.getI18NBeanFactory();
        return locale == null ? i18NBeanFactory.getI18NBean() : i18NBeanFactory.getI18NBean(locale);
    }

    protected Locale getCurrentUserLocale() {
        return this.getLocaleManager().getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    protected Set<String> getChildSubCalendarIds(SubCalendarEntity subCalendarEntity) {
        HashSet childIds = Sets.newHashSet();
        this.getActiveObjects().stream(SubCalendarEntity.class, Query.select().where("PARENT_ID = ?", new Object[]{subCalendarEntity.getID()}), subCalendarEntity1 -> childIds.add(subCalendarEntity1.getID()));
        return childIds;
    }

    protected Set<String> getFilterChildInternalSubscriptionSubCalendarIds(SubCalendarEntity subCalendarEntity, PersistedSubCalendar sourcePersistedSubCalendar) {
        HashSet childIds = Sets.newHashSet();
        this.getActiveObjects().stream(SubCalendarEntity.class, Query.select((String)"ID, SUBSCRIPTION_ID").where("PARENT_ID = ?", new Object[]{subCalendarEntity.getID()}), subCalendarEntity1 -> {
            if (sourcePersistedSubCalendar.getChildSubCalendarIds() != null && sourcePersistedSubCalendar.getChildSubCalendarIds().contains(subCalendarEntity1.getSubscription().getID())) {
                childIds.add(subCalendarEntity1.getID());
            }
        });
        return childIds;
    }

    protected Set<String> getFilterChildSubCalendarIds(SubCalendarEntity subCalendarEntity, Set<String> disableEventTypes) {
        HashSet childIds = Sets.newHashSet();
        this.getActiveObjects().stream(SubCalendarEntity.class, Query.select((String)"ID, STORE_KEY, USING_CUSTOM_EVENT_TYPE_ID").where("PARENT_ID = ?", new Object[]{subCalendarEntity.getID()}), subCalendarEntity1 -> {
            if (disableEventTypes.contains(CalendarUtil.getEventTypeFromStoreKey(subCalendarEntity1.getStoreKey())) || StringUtils.equals(subCalendarEntity1.getStoreKey(), "com.atlassian.confluence.extra.calendar3.calendarstore.generic.CustomSubCalendarDataStore") && disableEventTypes.contains(subCalendarEntity1.getUsingCustomEventTypeId())) {
                return;
            }
            childIds.add(subCalendarEntity1.getID());
        });
        return childIds;
    }

    protected ExtraSubCalendarPropertyEntity createSubCalendarEntityProperty(SubCalendarEntity subCalendarEntity, String key, Object value) {
        return (ExtraSubCalendarPropertyEntity)this.getActiveObjects().create(ExtraSubCalendarPropertyEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarEntity.getID()), new DBParam("KEY", (Object)key), new DBParam("VALUE", value)});
    }

    protected List<String> getSubCalendarEntityPropertyValues(SubCalendarEntity subCalendarEntity, String key) {
        Object[] extraSubCalendarPropertyEntities = subCalendarEntity.getExtraProperties();
        if (extraSubCalendarPropertyEntities != null) {
            return Lists.newArrayList((Iterable)Collections2.transform((Collection)Collections2.filter((Collection)Lists.newArrayList((Object[])extraSubCalendarPropertyEntities), extraSubCalendarPropertyEntity -> StringUtils.equals(extraSubCalendarPropertyEntity.getKey(), key)), ExtraSubCalendarPropertyEntity::getValue));
        }
        return Collections.emptyList();
    }

    protected String getSubCalendarEntityPropertyValue(SubCalendarEntity subCalendarEntity, String key) {
        ExtraSubCalendarPropertyEntity[] extraSubCalendarPropertyEntities = subCalendarEntity.getExtraProperties();
        if (extraSubCalendarPropertyEntities != null) {
            for (ExtraSubCalendarPropertyEntity extraSubCalendarPropertyEntity : extraSubCalendarPropertyEntities) {
                if (!StringUtils.equals(extraSubCalendarPropertyEntity.getKey(), key)) continue;
                return extraSubCalendarPropertyEntity.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<VEvent> query(T subCalendar, FilterBase filter, RecurrenceRetrieval recurrenceRetrieval) throws Exception {
        BooleanBuilder filterEventsPredicate;
        QueryDSLWhereTransformer queryDSLWhereTransformer = this.getQueryDSLWhereTransformer();
        RecurrenceRetrievalMode recurrenceRetrievalMode = recurrenceRetrieval.getRecurrenceRetrievalMode();
        QueryDSLMapper queryDSLMapper = this.getQueryDSLMapper();
        EventTable EVENT = (EventTable)queryDSLMapper.getMapping(EventEntity.class);
        SubCalendarTable SUB_CALENDAR = (SubCalendarTable)queryDSLMapper.getMapping(SubCalendarEntity.class);
        CustomEventTypeTable CUSTOM_EVENT_TYPE = (CustomEventTypeTable)queryDSLMapper.getMapping(CustomEventTypeEntity.class);
        Optional<BooleanBuilder> whereConditionOptional = queryDSLWhereTransformer.transform(filter);
        Preconditions.checkArgument((boolean)whereConditionOptional.isPresent());
        BooleanBuilder wherePredicate = filterEventsPredicate = whereConditionOptional.get();
        if (recurrenceRetrievalMode == RecurrenceRetrievalMode.EXPAND) {
            wherePredicate = wherePredicate.or(EVENT.RECURRENCE_RULE.isNotNull());
        }
        if (recurrenceRetrieval.getRecurrenceRetrievalMode() == RecurrenceRetrievalMode.OVERRIDE) {
            BooleanBuilder rescheduleEventPredicate = filterEventsPredicate.clone();
            rescheduleEventPredicate.and(EVENT.RECURRENCE_ID_TIMESTAMP.isNotNull());
            if (recurrenceRetrieval.getTimeRange().isPresent()) {
                EntityTimeRangeFilter entityTimeRangeFilter = new EntityTimeRangeFilter(recurrenceRetrieval.getTimeRange().get());
                EntityTimeRangeOperationMapper entityTimeRangeOperationMapper = new EntityTimeRangeOperationMapper(this.getPropertyToDBFieldMapperSupplier());
                BooleanBuilder externalTimeRangeRescheduleEventPredicate = entityTimeRangeOperationMapper.apply(entityTimeRangeFilter);
                externalTimeRangeRescheduleEventPredicate.and(EVENT.RECURRENCE_ID_TIMESTAMP.isNotNull());
                rescheduleEventPredicate = rescheduleEventPredicate.or(externalTimeRangeRescheduleEventPredicate);
                wherePredicate = filterEventsPredicate.clone().and(EVENT.RECURRENCE_ID_TIMESTAMP.isNull());
                wherePredicate.or(rescheduleEventPredicate);
            }
            SQLQuery rescheduleEventsQuery = (SQLQuery)((SQLQuery)SQLExpressions.select(EVENT.VEVENT_UID).from((Expression<?>)EVENT)).where(rescheduleEventPredicate);
            BooleanExpression originalEventsPredicate = EVENT.VEVENT_UID.in(rescheduleEventsQuery).and(EVENT.RECURRENCE_ID_TIMESTAMP.isNull());
            wherePredicate.or(originalEventsPredicate);
        }
        wherePredicate.and(EVENT.SUB_CALENDAR_ID.eq(((PersistedSubCalendar)subCalendar).getId()));
        BooleanBuilder finalWherePredicate = wherePredicate;
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            SQLQuery finalSQLQuery = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)EVENT)).innerJoin((EntityPath)SUB_CALENDAR)).on((com.querydsl.core.types.Predicate)EVENT.SUB_CALENDAR_ID.eq(SUB_CALENDAR.ID))).leftJoin((EntityPath)CUSTOM_EVENT_TYPE)).on((com.querydsl.core.types.Predicate)CUSTOM_EVENT_TYPE.ID.eq(SUB_CALENDAR.USING_CUSTOM_EVENT_TYPE_ID))).where(finalWherePredicate);
            ConstructorExpression<EventDTO> constructorExpression = Projections.constructor(EventDTO.class, new Class[]{Integer.class, String.class, Long.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class, Long.class, Long.class, Long.class, Integer.class, String.class, Boolean.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class}, EVENT.ID, EVENT.SUB_CALENDAR_ID, EVENT.UTC_START, EVENT.UTC_END, Expressions.nullExpression(Long.class), EVENT.RECURRENCE_RULE, EVENT.SUMMARY, EVENT.DESCRIPTION, EVENT.LOCATION, EVENT.URL, EVENT.ORGANISER, EVENT.RECURRENCE_ID_TIMESTAMP, EVENT.CREATED, EVENT.LAST_MODIFIED, EVENT.SEQUENCE, Expressions.nullExpression(String.class), EVENT.ALL_DAY, EVENT.START, EVENT.END, SUB_CALENDAR.TIME_ZONE_ID, EVENT.VEVENT_UID, SUB_CALENDAR.NAME, SUB_CALENDAR.PARENT_ID, SUB_CALENDAR.USING_CUSTOM_EVENT_TYPE_ID, CUSTOM_EVENT_TYPE.TITLE, SUB_CALENDAR.SUBSCRIPTION_ID);
            List eventDTOS = ((AbstractSQLQuery)finalSQLQuery.select(constructorExpression)).fetch();
            Collection vEvents = eventDTOS.stream().map(eventDTO -> {
                DateTimeZone subCalendarTimezone = recurrenceRetrievalMode == RecurrenceRetrievalMode.EXPAND ? DateTimeZone.forID((String)DateTimeZone.UTC.getID()) : DateTimeZone.forID((String)eventDTO.getSubCalendarTimeZoneId());
                if (StringUtils.isEmpty(eventDTO.getEventTypeName())) {
                    eventDTO.setEventTypeName((String)KeyStoreToEventTypeMapper.mapper.get((Object)subCalendar.getStoreKey()));
                }
                VEvent vEvent = this.getvEventMapper().toVEvent(subCalendarTimezone, (EventDTO)eventDTO);
                return vEvent;
            }).collect(Collectors.toList());
            if (LOG.isDebugEnabled()) {
                String sql = finalSQLQuery.getSQL().getSQL();
                LOG.debug("SQL query generate for query event method :{}", (Object)sql);
                LOG.debug("Number of event has been found -> {}", (Object)(vEvents == null ? 0 : vEvents.size()));
            }
            return vEvents;
        });
    }

    private List<VEvent> toVEventList(EventEntity[] eventEntities) throws Exception {
        if (eventEntities == null) {
            return Collections.emptyList();
        }
        EventEntitiesToEventsTransformer eventEntitiesToEventsTransformer = new EventEntitiesToEventsTransformer(this.getTransactionTemplate(), this.getvEventMapper());
        return eventEntitiesToEventsTransformer.transform(Arrays.asList(eventEntities));
    }

    @Override
    public List<VEvent> getEvents(T subCalendar) throws Exception {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getEvents");
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getEvents - from DB");
        EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ?", new Object[]{((PersistedSubCalendar)subCalendar).getId()}));
        UtilTimerStack.pop((String)"AbstractCalendarDataStore.getEvents - from DB");
        List<VEvent> list = this.toVEventList(eventEntities);
        UtilTimerStack.pop((String)"AbstractCalendarDataStore.getEvents");
        return list;
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, DateTime startTime, DateTime endTime) throws Exception {
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getEvents");
        long startMs = startTime.getMillis();
        long endMs = endTime.getMillis();
        UtilTimerStack.push((String)"AbstractCalendarDataStore.getEvents - from DB");
        EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ? AND ( ( END >= ? AND END <= ? ) OR ( START >= ? AND START <= ? ) OR (END >= ? AND START <= ? ) OR RECURRENCE_RULE IS NOT NULL)", new Object[]{((PersistedSubCalendar)subCalendar).getId(), startMs, endMs, startMs, endMs, startMs, endMs}));
        UtilTimerStack.pop((String)"AbstractCalendarDataStore.getEvents - from DB");
        List<VEvent> list = this.toVEventList(eventEntities);
        UtilTimerStack.pop((String)"AbstractCalendarDataStore.getEvents");
        return list;
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) {
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(this.getSubCalendarEntity(((PersistedSubCalendar)subCalendar).getId()));
        params.addAll(Arrays.asList(vEventUids));
        EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ? AND VEVENT_UID IN (" + StringUtils.repeat("?", ", ", vEventUids.length) + ")", params.toArray()));
        if (eventEntities != null && eventEntities.length > 0) {
            Collection<VEvent> entityCollection = this.getvEventMapper().toVEvents(eventEntities);
            return entityCollection.stream().filter(vEventPredicate == null ? vEvent -> true : vEventPredicate).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public VEvent getEvent(T subCalendar, String vEventUid, String recurrenceId) {
        List<VEvent> vEvents = this.getEvents(subCalendar, new ByRecurrenceIdPredicate(this.getJodaIcal4jTimeZoneMapper().toIcal4jTimeZone(((PersistedSubCalendar)subCalendar).getTimeZoneId()), recurrenceId, this.getJodaIcal4jDateTimeConverter()), vEventUid);
        if (vEvents.isEmpty()) {
            LOG.debug("Could not load event with following predicate: subcal id = {}, vEventUid = {}, recurrenceId = {}", new Object[]{subCalendar == null ? "" : ((PersistedSubCalendar)subCalendar).getId(), vEventUid, recurrenceId});
        }
        return vEvents.isEmpty() ? null : vEvents.iterator().next();
    }

    @Override
    public VEvent addEvent(T subCalendar, VEvent newEventDetails) {
        EventEntity eventEntity = this.getActiveObjectsServiceWrapper().createEventEntity((PersistedSubCalendar)subCalendar, newEventDetails);
        this.getActiveObjectsServiceWrapper().createInviteeEntity(eventEntity, newEventDetails, this.getUserAccessor());
        return this.getvEventMapper().toVEvent((EventEntity)this.getActiveObjects().get(EventEntity.class, (Object)eventEntity.getID()));
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        long newUtcEnd;
        long newUtcStart;
        boolean isAllDay;
        RecurrenceId recurrenceId = newEventDetails.getRecurrenceId();
        if (recurrenceId != null) {
            TimeZone recurrenceTimezone = recurrenceId.getTimeZone();
            TimeZone subCalendarTimezone = this.getJodaIcal4jTimeZoneMapper().toIcal4jTimeZone(((PersistedSubCalendar)subCalendar).getTimeZoneId());
            if (recurrenceTimezone != null && !recurrenceTimezone.getID().equals(subCalendarTimezone.getID())) {
                recurrenceId.setTimeZone(subCalendarTimezone);
            }
        }
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(((PersistedSubCalendar)subCalendar).getId());
        EventEntity eventEntity = this.getEventEntityEx(subCalendar, subCalendarEntity, AbstractCalendarDataStore.getPropertyValue(newEventDetails.getUid()), AbstractCalendarDataStore.getPropertyValue(recurrenceId));
        DtStart startDateProperty = newEventDetails.getStartDate();
        Date startDate = startDateProperty.getDate();
        long newStart = startDate.getTime();
        DtEnd endDateProperty = newEventDetails.getEndDate();
        Date endDate = endDateProperty.getDate();
        long newEnd = endDate.getTime();
        boolean bl = isAllDay = !(startDate instanceof net.fortuna.ical4j.model.DateTime) && !(endDate instanceof net.fortuna.ical4j.model.DateTime);
        if (isAllDay) {
            newUtcStart = CalendarUtil.getUtcDateTimeWithAllDay(startDate).getMillis();
            newUtcEnd = CalendarUtil.getUtcDateTimeWithAllDay(endDate).getMillis();
        } else {
            newUtcStart = CalendarUtil.getUtcTime(startDateProperty).getTime();
            newUtcEnd = CalendarUtil.getUtcTime(endDateProperty).getTime();
        }
        long prevStart = eventEntity.getStart();
        long prevEnd = eventEntity.getEnd();
        eventEntity.setStart(newStart);
        eventEntity.setEnd(newEnd);
        eventEntity.setUtcStart(newUtcStart);
        eventEntity.setUtcEnd(newUtcEnd);
        eventEntity.setAllDay(isAllDay);
        eventEntity.setSummary(AbstractCalendarDataStore.getPropertyValue(newEventDetails.getSummary()));
        eventEntity.setDescription(AbstractCalendarDataStore.getPropertyValue(newEventDetails.getDescription()));
        eventEntity.setLocation(AbstractCalendarDataStore.getPropertyValue(newEventDetails.getLocation()));
        eventEntity.setUrl(AbstractCalendarDataStore.getPropertyValue(newEventDetails.getUrl()));
        String newRecurrenceRule = AbstractCalendarDataStore.getPropertyValue(newEventDetails.getProperty("RRULE"));
        String prevRecurrenceRule = eventEntity.getRecurrenceRule();
        eventEntity.setRecurrenceRule(newRecurrenceRule);
        eventEntity.setRecurrenceIdTimestamp(recurrenceId == null ? null : Long.valueOf(recurrenceId.getDate().getTime()));
        eventEntity.setLastModified(System.currentTimeMillis());
        eventEntity.setSequence(eventEntity.getSequence() + 1);
        eventEntity.save();
        ActiveObjects activeObjects = this.getActiveObjects();
        this.getActiveObjectsServiceWrapper().deleteInvitees(eventEntity);
        this.getActiveObjectsServiceWrapper().createInviteeEntity(eventEntity, newEventDetails, this.getUserAccessor());
        if (recurrenceId == null) {
            if (prevStart != newStart || prevEnd != newEnd || this.hasRecurrenceChanged(prevRecurrenceRule, newRecurrenceRule)) {
                this.getActiveObjectsServiceWrapper().deleteEventRecurrenceExclusionEntities(eventEntity);
                EventEntity[] rescheduledRecurrenceEntities = (EventEntity[])activeObjects.find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ? AND VEVENT_UID = ? AND RECURRENCE_ID_TIMESTAMP IS NOT NULL", new Object[]{subCalendarEntity.getID(), eventEntity.getVeventUid()}));
                if (rescheduledRecurrenceEntities != null) {
                    for (EventEntity rescheduledRecurrenceEntity : rescheduledRecurrenceEntities) {
                        this.getActiveObjectsServiceWrapper().deleteInvitees(rescheduledRecurrenceEntity);
                        this.getActiveObjectsServiceWrapper().deleteEventRecurrenceExclusionEntities(rescheduledRecurrenceEntity);
                        activeObjects.delete(new RawEntity[]{rescheduledRecurrenceEntity});
                    }
                }
            } else {
                this.getActiveObjectsServiceWrapper().createEventRecurrenceExclusionEntity(eventEntity, newEventDetails);
            }
        }
        return this.getvEventMapper().toVEvent((EventEntity)activeObjects.get(EventEntity.class, (Object)eventEntity.getID()));
    }

    private boolean hasRecurrenceChanged(String oldRecurrenceRule, String newRecurrenceRule) {
        return !this.getRecurrenceParamsToCompare(oldRecurrenceRule, RECURRENCE_RULE_COMPARISON_CRITERIA).equals(this.getRecurrenceParamsToCompare(newRecurrenceRule, RECURRENCE_RULE_COMPARISON_CRITERIA));
    }

    private Map<String, String> getRecurrenceParamsToCompare(String recurrenceRule, String ... paramName) {
        HashSet significantRecurrenceParams = Sets.newHashSet((Iterable)Collections2.filter((Collection)Collections2.transform((Collection)Sets.newHashSet((Object[])StringUtils.split(StringUtils.trim(StringUtils.defaultString(recurrenceRule)), ";")), paramPairStr -> StringUtils.split(paramPairStr, "=", 2)), paramPair -> ((String[])paramPair).length == 2 && ArrayUtils.contains(paramName, paramPair[0])));
        HashMap recurrenceParametersMap = Maps.newHashMap();
        for (String[] paramPair2 : significantRecurrenceParams) {
            recurrenceParametersMap.put(paramPair2[0], paramPair2[1]);
        }
        return recurrenceParametersMap;
    }

    private EventEntity getEventEntityEx(T subCalendar, SubCalendarEntity subCalendarEntity, String vEventUid, String recurrenceId) {
        EventEntity eventEntity = this.getEventEntity(subCalendarEntity, vEventUid, recurrenceId);
        if (eventEntity == null) {
            VEvent vEvent = this.getEvent(subCalendar, vEventUid, recurrenceId);
            Object property = vEvent.getProperty("EVENT-ID");
            eventEntity = (EventEntity)this.getActiveObjects().get(EventEntity.class, (Object)Integer.parseInt(((Content)property).getValue()));
        }
        return eventEntity;
    }

    private EventEntity getEventEntity(SubCalendarEntity subCalendarEntity, String vEventUid, String recurrenceId) {
        ArrayList querySubstitutions = Lists.newArrayList((Object[])new Object[]{subCalendarEntity.getID(), vEventUid});
        List<Object> recurrenceIdTimeStamps = new ArrayList();
        if (StringUtils.isNotBlank(recurrenceId)) {
            recurrenceIdTimeStamps = this.getRecurrenceIdTimeStamp(subCalendarEntity, recurrenceId);
            querySubstitutions.addAll(recurrenceIdTimeStamps);
        }
        EventEntity[] eventEntitites = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where((String)(StringUtils.isBlank(recurrenceId) ? "SUB_CALENDAR_ID = ? AND VEVENT_UID = ? AND RECURRENCE_ID_TIMESTAMP IS NULL" : "SUB_CALENDAR_ID = ? AND VEVENT_UID = ? AND RECURRENCE_ID_TIMESTAMP IN (" + recurrenceIdTimeStamps.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")"), querySubstitutions.toArray()));
        return eventEntitites == null || eventEntitites.length == 0 ? null : eventEntitites[0];
    }

    private List<Long> getRecurrenceIdTimeStamp(SubCalendarEntity subCalendarEntity, String recurrenceId) {
        ArrayList<Long> recurrenceIdTimeStamps = new ArrayList<Long>();
        try {
            recurrenceIdTimeStamps.add(new RecurrenceId(new Date(recurrenceId)).getDate().getTime());
        }
        catch (ParseException invalidRecurrenceId) {
            LOG.error(String.format("Cannot calculate recurrence ID timestamp for ID: %s", recurrenceId), (Throwable)invalidRecurrenceId);
            recurrenceIdTimeStamps.add(0L);
        }
        try {
            DateTimeZone dateTimeZone = DateTimeZone.forID((String)subCalendarEntity.getTimeZoneId());
            recurrenceIdTimeStamps.add(new RecurrenceId(recurrenceId, this.getJodaIcal4jTimeZoneMapper().toIcal4jTimeZone(dateTimeZone.getID())).getDate().getTime());
        }
        catch (ParseException invalidRecurrenceId) {
            LOG.error(String.format("Cannot calculate recurrence ID timestamp with timezone for ID: %s", recurrenceId), (Throwable)invalidRecurrenceId);
            recurrenceIdTimeStamps.add(0L);
        }
        return recurrenceIdTimeStamps;
    }

    @Override
    public void deleteEvent(T subCalendar, String vEventUid, String recurrenceId) {
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(((PersistedSubCalendar)subCalendar).getId());
        ActiveObjects activeObjects = this.getActiveObjects();
        EventEntity eventEntity = this.getEventEntityEx(subCalendar, subCalendarEntity, vEventUid, recurrenceId);
        this.getActiveObjectsServiceWrapper().deleteInvitees(eventEntity);
        this.getActiveObjectsServiceWrapper().deleteEventRecurrenceExclusionEntities(eventEntity);
        activeObjects.delete(new RawEntity[]{eventEntity});
    }

    @Override
    public void moveEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ? AND VEVENT_UID = ?", new Object[]{((PersistedSubCalendar)subCalendar).getId(), vEventUid}));
        if (eventEntities != null) {
            SubCalendarEntity dstSubcCalendarEntity = this.getSubCalendarEntity(destinationSubCalendar.getId());
            for (EventEntity eventEntity : eventEntities) {
                eventEntity.setSubCalendar(dstSubcCalendarEntity);
                eventEntity.save();
            }
        }
    }

    @Override
    public void changeEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        EventEntity[] eventEntities = (EventEntity[])this.getActiveObjects().find(EventEntity.class, Query.select().where("SUB_CALENDAR_ID = ? AND VEVENT_UID = ?", new Object[]{((PersistedSubCalendar)subCalendar).getId(), vEventUid}));
        if (eventEntities != null) {
            SubCalendarEntity dstSubcCalendarEntity = this.getSubCalendarEntity(destinationSubCalendar.getId());
            for (EventEntity eventEntity : eventEntities) {
                eventEntity.setSubCalendar(dstSubcCalendarEntity);
                eventEntity.save();
                if (StringUtils.equals(destinationSubCalendar.getType(), "custom") || destinationSubCalendar.getType().equals("other") || eventEntity.getInvitees().length != 0) continue;
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                this.getActiveObjects().create(InviteeEntity.class, new DBParam[]{new DBParam("EVENT_ID", (Object)eventEntity.getID()), new DBParam("INVITEE_ID", (Object)currentUser.getKey().toString())});
            }
        }
    }

    @Override
    public boolean hasReminderFor(T subCalendar, ConfluenceUser user) {
        Objects.requireNonNull(subCalendar, "Sub Calendar should not be null");
        Objects.requireNonNull(user, "Confluence user could not be null");
        ReminderUsersEntity[] existedEntities = (ReminderUsersEntity[])this.getActiveObjects().find(ReminderUsersEntity.class, Query.select().where("USER_KEY = ? AND SUB_CALENDAR_ID = ?", new Object[]{user.getKey().getStringValue(), ((PersistedSubCalendar)subCalendar).getId()}));
        return existedEntities != null && existedEntities.length > 0;
    }

    @Override
    public boolean setReminderFor(T subCalendar, ConfluenceUser user, boolean isReminder) {
        Preconditions.checkNotNull(subCalendar, (Object)"Sub Calendar should not be null");
        Preconditions.checkNotNull((Object)user, (Object)"Confluence user could not be null");
        return this.setReminderFor(((PersistedSubCalendar)subCalendar).getId(), user, isReminder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    private boolean setReminderFor(String subCalendarId, ConfluenceUser user, boolean isReminder) {
        ActiveObjects ao = this.getActiveObjects();
        String userKey = user.getKey().getStringValue();
        boolean returnStatus = true;
        ActiveObjects activeObjects = ao;
        synchronized (activeObjects) {
            ReminderUsersEntity[] existedEntitys = (ReminderUsersEntity[])ao.find(ReminderUsersEntity.class, Query.select().where("USER_KEY = ? AND SUB_CALENDAR_ID = ?", new Object[]{userKey, subCalendarId}));
            if (isReminder && (existedEntitys == null || existedEntitys.length == 0)) {
                ao.create(ReminderUsersEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("USER_KEY", (Object)userKey)});
            }
            if (!isReminder && existedEntitys != null && existedEntitys.length > 0) {
                ao.delete((RawEntity[])existedEntitys);
                returnStatus = false;
            }
        }
        return returnStatus;
    }

    @Override
    public void disableEventTypes(T subCalendar, List<String> disableEventTypes) {
        DisableEventTypeEntity[] disableEventTypeEntities = (DisableEventTypeEntity[])this.getActiveObjects().find(DisableEventTypeEntity.class, Query.select().where("SUB_CALENDAR_ID = ?", new Object[]{((PersistedSubCalendar)subCalendar).getId()}));
        if (disableEventTypeEntities != null && disableEventTypeEntities.length > 0) {
            this.getActiveObjects().delete((RawEntity[])disableEventTypeEntities);
        }
        for (String event : disableEventTypes) {
            this.getActiveObjects().create(DisableEventTypeEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)((PersistedSubCalendar)subCalendar).getId()), new DBParam("EVENT_KEY", (Object)event)});
        }
    }

    @Override
    public CustomEventTypeEntity updateCustomEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String customEventTypeId, String title, String icon, int periodInMins) {
        CustomEventTypeEntity[] customEventTypeEntities;
        CustomEventTypeEntity customEventTypeEntity = null;
        if (StringUtils.isNotBlank(customEventTypeId) && (customEventTypeEntities = (CustomEventTypeEntity[])this.getActiveObjects().find(CustomEventTypeEntity.class, Query.select().where("ID = ?", new Object[]{customEventTypeId}))) != null && customEventTypeEntities.length > 0) {
            customEventTypeEntity = customEventTypeEntities[0];
            customEventTypeEntity.setIcon(icon);
            customEventTypeEntity.setTitle(title);
            customEventTypeEntity.setCreated(String.valueOf(System.currentTimeMillis()));
            customEventTypeEntity.save();
        }
        if (customEventTypeEntity == null) {
            customEventTypeEntity = (CustomEventTypeEntity)this.getActiveObjects().create(CustomEventTypeEntity.class, new DBParam[]{new DBParam("BELONG_SUB_CALENDAR_ID", (Object)((PersistedSubCalendar)subCalendar).getId()), new DBParam("ID", (Object)UUIDGenerate.generate()), new DBParam("TITLE", (Object)title), new DBParam("ICON", (Object)icon), new DBParam("CREATED", (Object)String.valueOf(System.currentTimeMillis()))});
        }
        if (periodInMins >= 0) {
            this.updateReminderSetting(reminderSettingCallbacks, subCalendar, customEventTypeEntity.getID(), "com.atlassian.confluence.extra.calendar3.calendarstore.generic.CustomSubCalendarDataStore", periodInMins);
        }
        return customEventTypeEntity;
    }

    @Override
    public ReminderSettingEntity updateReminderForSanboxEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String eventTypeId, int periodInMins) {
        return this.updateReminderSetting(reminderSettingCallbacks, subCalendar, null, CalendarUtil.getStoreKeyFromEventType(eventTypeId), periodInMins);
    }

    private ReminderSettingEntity updateReminderSetting(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String customEventTypeId, String storeKey, int periodInMins) {
        ReminderSettingEntity reminderSettingEntity;
        long periodInMilis = ReminderPeriods.toReminderPeriod(periodInMins).equals((Object)Option.none()) ? 0L : ((ReminderPeriods)((Object)ReminderPeriods.toReminderPeriod(periodInMins).get())).getMilisecond();
        ReminderSettingEntity[] reminderSettingEntities = this.findReminderSettingEntities(((PersistedSubCalendar)subCalendar).getId(), storeKey, customEventTypeId);
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        String lastModifierUserKey = currentUser.getKey().toString();
        if (reminderSettingEntities != null && reminderSettingEntities.length > 0) {
            reminderSettingEntity = reminderSettingEntities[0];
            Option<ReminderPeriods> oldPeriod = ReminderPeriods.toReminderPeriod(reminderSettingEntity.getPeriod());
            if (reminderSettingEntity.getPeriod() != periodInMilis) {
                reminderSettingEntity.setPeriod(periodInMilis);
                reminderSettingEntity.setLastModifier(lastModifierUserKey);
                reminderSettingEntity.save();
            }
            if (reminderSettingCallbacks.isDefined()) {
                ((ReminderSettingCallback)reminderSettingCallbacks.get()).updateReminderSetting(new ReminderSettingCallback.ReminderSettingChange(storeKey, customEventTypeId, oldPeriod.isEmpty() ? 0 : ((ReminderPeriods)((Object)oldPeriod.get())).getMins(), periodInMins), (PersistedSubCalendar)subCalendar);
            }
        } else {
            reminderSettingEntity = (ReminderSettingEntity)this.getActiveObjects().create(ReminderSettingEntity.class, new DBParam[]{new DBParam("ID", (Object)UUIDGenerate.generate()), new DBParam("STORE_KEY", (Object)storeKey), new DBParam("SUB_CALENDAR_ID", (Object)((PersistedSubCalendar)subCalendar).getId()), new DBParam("CUSTOM_EVENT_TYPE_ID", (Object)customEventTypeId), new DBParam("PERIOD", (Object)periodInMilis), new DBParam("LAST_MODIFIER", (Object)lastModifierUserKey)});
            if (reminderSettingCallbacks.isDefined()) {
                ((ReminderSettingCallback)reminderSettingCallbacks.get()).createReminderSetting(new ReminderSettingCallback.ReminderSettingChange(storeKey, customEventTypeId, 0, periodInMins), (PersistedSubCalendar)subCalendar);
            }
        }
        SubCalendarEntity[] subCalendarEntities = this.findSubCalendarEntities(((PersistedSubCalendar)subCalendar).getId(), storeKey, customEventTypeId);
        if (subCalendarEntities != null && subCalendarEntities.length > 0) {
            this.setReminderFor(subCalendarEntities[0].getID(), currentUser, periodInMilis != 0L);
        }
        return reminderSettingEntity;
    }

    @Override
    public ReminderSettingEntity getReminderSetting(String subCalendarId, String storeKey, String customEventTypeId) {
        ReminderSettingEntity[] reminderSettingEntities = this.findReminderSettingEntities(subCalendarId, storeKey, customEventTypeId);
        if (reminderSettingEntities == null || reminderSettingEntities.length == 0) {
            return null;
        }
        return reminderSettingEntities[0];
    }

    private ReminderSettingEntity[] findReminderSettingEntities(String subCalendarId, String storeKey, String customEventTypeId) {
        Query query = null;
        query = StringUtils.isNotEmpty(customEventTypeId) ? Query.select().where("CUSTOM_EVENT_TYPE_ID = ?", new Object[]{customEventTypeId}) : Query.select().where("SUB_CALENDAR_ID = ? AND STORE_KEY = ?", new Object[]{subCalendarId, storeKey});
        return (ReminderSettingEntity[])this.getActiveObjects().find(ReminderSettingEntity.class, query);
    }

    private SubCalendarEntity[] findSubCalendarEntities(String subCalendarId, String storeKey, String customEventTypeId) {
        Query query = null;
        query = StringUtils.isNotEmpty(customEventTypeId) ? Query.select().where("PARENT_ID = ? AND USING_CUSTOM_EVENT_TYPE_ID = ?", new Object[]{subCalendarId, customEventTypeId}) : Query.select().where("PARENT_ID = ? AND STORE_KEY = ?", new Object[]{subCalendarId, storeKey});
        return (SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, query);
    }

    @Override
    public CustomEventTypeEntity getCustomEventType(T subCalendar, String customEventTypeId) {
        CustomEventTypeEntity[] customEventTypeEntities;
        if (StringUtils.isNotBlank(customEventTypeId) && (customEventTypeEntities = (CustomEventTypeEntity[])this.getActiveObjects().find(CustomEventTypeEntity.class, Query.select().where("ID = ?", new Object[]{customEventTypeId}))) != null && customEventTypeEntities.length > 0) {
            return customEventTypeEntities[0];
        }
        return null;
    }

    @Override
    public List<CustomEventTypeEntity> getCustomEventTypes(String ... customEventTypeId) {
        if (customEventTypeId == null || customEventTypeId.length == 0) {
            return Lists.newArrayList();
        }
        ArrayList querySubstitutions = Lists.newArrayList(Arrays.asList(customEventTypeId));
        Object[] customEventTypeEntities = (CustomEventTypeEntity[])this.getActiveObjects().find(CustomEventTypeEntity.class, Query.select().where("ID IN (" + StringUtils.repeat("?", ", ", customEventTypeId.length) + ")", querySubstitutions.toArray()));
        return Lists.newArrayList((Object[])customEventTypeEntities);
    }

    @Override
    public void deleteDisableEventType(String subCalendarId, String eventType) {
        SubCalendarEntity[] subCalendarEntities = (SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("ID = ? ", new Object[]{subCalendarId}));
        if (subCalendarEntities != null) {
            for (SubCalendarEntity subCalendarEntity : subCalendarEntities) {
                this.deleteSubCalendarDisableEvent(subCalendarEntity, eventType);
            }
        }
    }

    @Override
    public void deleteCustomEventType(String subCalendarId, String customEventTypeId) {
        RawEntity[] customEventTypeEntities = (CustomEventTypeEntity[])this.getActiveObjects().find(CustomEventTypeEntity.class, Query.select().where("ID = ? ", new Object[]{customEventTypeId}));
        if (customEventTypeEntities != null && customEventTypeEntities.length > 0) {
            SubCalendarEntity[] subCalendarEntities;
            this.getActiveObjects().delete(customEventTypeEntities);
            this.removeReminderSetting(customEventTypeEntities[0]);
            for (SubCalendarEntity subCalendarEntity : subCalendarEntities = (SubCalendarEntity[])this.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("USING_CUSTOM_EVENT_TYPE_ID = ?", new Object[]{customEventTypeId}))) {
                this.removeSubCalendar(subCalendarEntity);
            }
        }
    }

    @Override
    public List<ReminderEvent> getSingleEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        List<EventDTO> eventDTOS = this.getEventUpComingForReminder(true, startDateSystemUTC, schedulerTime);
        Collection reminderEventCollection = eventDTOS.stream().map(ReminderEvent::toReminderEvent).collect(Collectors.toSet());
        return Lists.newArrayList((Iterable)reminderEventCollection);
    }

    private List<EventDTO> getEventUpComingForReminder(boolean isSingle, long startDateSystemUTC, long schedulerTime) {
        QueryDSLMapper queryDSLMapper = this.getQueryDSLMapper();
        EventTable EVENT = (EventTable)queryDSLMapper.getMapping(EventEntity.class);
        ReminderSettingTable REMINDER_SETTING = (ReminderSettingTable)queryDSLMapper.getMapping(ReminderSettingEntity.class);
        SubCalendarTable SUB_CALENDAR = (SubCalendarTable)queryDSLMapper.getMapping(SubCalendarEntity.class);
        BooleanBuilder selectCondition = null;
        selectCondition = isSingle ? new BooleanBuilder(EVENT.RECURRENCE_RULE.isNull()).and(EVENT.RECURRENCE_ID_TIMESTAMP.isNull()).and(REMINDER_SETTING.PERIOD.gt(0)).and(EVENT.UTC_START.subtract(startDateSystemUTC).loe(REMINDER_SETTING.PERIOD)).and(EVENT.UTC_START.subtract(startDateSystemUTC).gt(REMINDER_SETTING.PERIOD.subtract(schedulerTime))) : new BooleanBuilder(EVENT.RECURRENCE_RULE.isNotNull().or(EVENT.RECURRENCE_ID_TIMESTAMP.isNotNull()).and(REMINDER_SETTING.PERIOD.gt(0)));
        BooleanBuilder reminderSettingCondition = new BooleanBuilder(SUB_CALENDAR.PARENT_ID.eq(REMINDER_SETTING.SUB_CALENDAR_ID).and(SUB_CALENDAR.USING_CUSTOM_EVENT_TYPE_ID.eq(REMINDER_SETTING.CUSTOM_EVENT_TYPE_ID).or(SUB_CALENDAR.STORE_KEY.eq(REMINDER_SETTING.STORE_KEY).and(SUB_CALENDAR.USING_CUSTOM_EVENT_TYPE_ID.isNull()))));
        BooleanBuilder finalSelectCondition = selectCondition;
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            SQLQuery sql = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)EVENT)).innerJoin((EntityPath)SUB_CALENDAR)).on((com.querydsl.core.types.Predicate)EVENT.SUB_CALENDAR_ID.eq(SUB_CALENDAR.ID))).innerJoin((EntityPath)REMINDER_SETTING)).on((com.querydsl.core.types.Predicate)reminderSettingCondition)).where(finalSelectCondition)).orderBy((OrderSpecifier<?>)EVENT.CREATED.desc())).limit(UPCOMING_EVENT_REMINDER_LIMIT);
            List eventDTOS = ((AbstractSQLQuery)sql.select(Projections.constructor(EventDTO.class, new Class[]{Integer.class, String.class, Long.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class, Long.class, Long.class, Long.class, Integer.class, String.class, Boolean.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class}, EVENT.ID, EVENT.SUB_CALENDAR_ID, EVENT.UTC_START, EVENT.UTC_END, REMINDER_SETTING.PERIOD, EVENT.RECURRENCE_RULE, EVENT.SUMMARY, EVENT.DESCRIPTION, EVENT.LOCATION, EVENT.URL, EVENT.ORGANISER, EVENT.RECURRENCE_ID_TIMESTAMP, EVENT.CREATED, EVENT.LAST_MODIFIED, EVENT.SEQUENCE, REMINDER_SETTING.STORE_KEY, EVENT.ALL_DAY, EVENT.START, EVENT.END, SUB_CALENDAR.TIME_ZONE_ID, EVENT.VEVENT_UID, SUB_CALENDAR.NAME, SUB_CALENDAR.PARENT_ID, SUB_CALENDAR.USING_CUSTOM_EVENT_TYPE_ID, Expressions.nullExpression(String.class), SUB_CALENDAR.SUBSCRIPTION_ID))).fetch();
            for (EventDTO eventDTO : eventDTOS) {
                eventDTO.setParentCalendarName(this.getParentCalendarNameFromId(eventDTO.getParentSubCalendarId(), SUB_CALENDAR, eventDTO.getCalendarName()));
            }
            return eventDTOS;
        });
    }

    private String getParentCalendarNameFromId(String parentCalendarId, SubCalendarTable subCalendarTable, String defaultNameIfEmpty) {
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            List calendarNames = ((AbstractSQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)subCalendarTable)).where(subCalendarTable.ID.eq(parentCalendarId))).select((Expression)subCalendarTable.NAME)).fetch();
            if (calendarNames != null && calendarNames.size() > 0) {
                return (String)calendarNames.get(0);
            }
            return defaultNameIfEmpty;
        });
    }

    @Override
    public List<VEvent> getRepeatEventUpComingReminder() {
        ArrayList<VEvent> repeatVEvents = new ArrayList<VEvent>();
        List<EventDTO> eventDTOS = this.getEventUpComingForReminder(false, 0L, 0L);
        for (EventDTO eventDTO : eventDTOS) {
            DateTimeZone subCalendarTimeZone = DateTimeZone.forID((String)eventDTO.getSubCalendarTimeZoneId());
            repeatVEvents.add(this.getvEventMapper().toVEvent(subCalendarTimeZone, eventDTO));
        }
        return repeatVEvents;
    }

    @Override
    public List<ReminderEvent> getJiraEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        QueryDSLMapper queryDSLMapper = this.getQueryDSLMapper();
        JiraReminderEventTable JIRA_REMINDER_EVENT = (JiraReminderEventTable)queryDSLMapper.getMapping(JiraReminderEventEntity.class);
        ReminderSettingTable REMINDER_SETTING = (ReminderSettingTable)queryDSLMapper.getMapping(ReminderSettingEntity.class);
        SubCalendarTable SUB_CALENDAR = (SubCalendarTable)queryDSLMapper.getMapping(SubCalendarEntity.class);
        BooleanBuilder selectCondition = new BooleanBuilder().and(REMINDER_SETTING.PERIOD.gt(0)).and(JIRA_REMINDER_EVENT.UTC_START.subtract(startDateSystemUTC).loe(REMINDER_SETTING.PERIOD)).and(JIRA_REMINDER_EVENT.UTC_START.subtract(startDateSystemUTC).goe(REMINDER_SETTING.PERIOD.subtract(schedulerTime)));
        BooleanBuilder reminderSettingCondition = new BooleanBuilder(SUB_CALENDAR.PARENT_ID.eq(REMINDER_SETTING.SUB_CALENDAR_ID).and(SUB_CALENDAR.STORE_KEY.eq(REMINDER_SETTING.STORE_KEY)));
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            SQLQuery sql = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)JIRA_REMINDER_EVENT)).innerJoin((EntityPath)SUB_CALENDAR)).on((com.querydsl.core.types.Predicate)JIRA_REMINDER_EVENT.SUB_CALENDAR_ID.eq(SUB_CALENDAR.ID))).innerJoin((EntityPath)REMINDER_SETTING)).on((com.querydsl.core.types.Predicate)reminderSettingCondition)).where(selectCondition)).limit(UPCOMING_EVENT_REMINDER_LIMIT);
            List jiraReminderEventDTOList = ((AbstractSQLQuery)sql.select(Projections.constructor(JiraReminderEventDTO.class, JIRA_REMINDER_EVENT.ID, JIRA_REMINDER_EVENT.SUB_CALENDAR_ID, JIRA_REMINDER_EVENT.UTC_START, JIRA_REMINDER_EVENT.UTC_END, REMINDER_SETTING.PERIOD, JIRA_REMINDER_EVENT.TITLE, JIRA_REMINDER_EVENT.DESCRIPTION, REMINDER_SETTING.STORE_KEY, JIRA_REMINDER_EVENT.ALL_DAY, SUB_CALENDAR.NAME, SUB_CALENDAR.PARENT_ID, JIRA_REMINDER_EVENT.KEY_ID, JIRA_REMINDER_EVENT.USER_ID, JIRA_REMINDER_EVENT.JQL, JIRA_REMINDER_EVENT.TICKET_ID, JIRA_REMINDER_EVENT.ASSIGNEE, JIRA_REMINDER_EVENT.STATUS, JIRA_REMINDER_EVENT.EVENT_TYPE, JIRA_REMINDER_EVENT.ISSUE_LINK, JIRA_REMINDER_EVENT.ISSUE_ICON_URL))).fetch();
            Collection reminderEventCollection = Collections2.transform(jiraReminderEventDTOList, jiraReminderEventDTO -> {
                jiraReminderEventDTO.setParentCalendarName(this.getParentCalendarNameFromId(jiraReminderEventDTO.getParentSubCalendarId(), SUB_CALENDAR, jiraReminderEventDTO.getCalendarName()));
                return ReminderEvent.toReminderEvent(jiraReminderEventDTO);
            });
            return Lists.newArrayList((Iterable)reminderEventCollection);
        });
    }

    @Override
    public <T> Option<T> getReminderListFor(Function<Map<String, Collection<String>>, T> callback, String ... subCalendarIds) {
        if (subCalendarIds == null || subCalendarIds.length <= 0) {
            return Option.none();
        }
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            ReminderUserTable reminderUser = (ReminderUserTable)this.getQueryDSLMapper().getMapping(ReminderUsersEntity.class);
            Map returnMap = null;
            SQLQuery sql = (SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)reminderUser)).where(reminderUser.SUB_CALENDAR_ID.in(subCalendarIds))).orderBy((OrderSpecifier<?>)reminderUser.USER_KEY.desc());
            List results = ((AbstractSQLQuery)sql.select(new Expression[]{reminderUser.USER_KEY, reminderUser.SUB_CALENDAR_ID})).fetch();
            returnMap = Maps.transformValues((Map)Multimaps.index(results, tuple -> tuple.get(reminderUser.USER_KEY)).asMap(), tuples -> Sets.newHashSet((Iterable)Collections2.transform((Collection)tuples, tuple -> tuple.get(reminderUser.SUB_CALENDAR_ID))));
            return Option.option((Object)callback.apply((Object)returnMap));
        });
    }

    @Override
    public Map<Integer, Collection<String>> getInviteesFor(Integer ... eventIds) {
        if (eventIds == null || eventIds.length == 0) {
            return new HashMap<Integer, Collection<String>>();
        }
        InviteeTable inviteeTable = (InviteeTable)this.getQueryDSLMapper().getMapping(InviteeEntity.class);
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            Map returnMap = null;
            SQLQuery sql = (SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).where(inviteeTable.EVENT_ID.in((Object[])eventIds))).orderBy((OrderSpecifier<?>)inviteeTable.EVENT_ID.desc());
            List results = ((AbstractSQLQuery)sql.select(new Expression[]{inviteeTable.EVENT_ID, inviteeTable.INVITEE_ID})).fetch();
            returnMap = Maps.transformValues((Map)Multimaps.index(results, tuple -> tuple.get(inviteeTable.EVENT_ID)).asMap(), tuples -> Sets.newHashSet((Iterable)Collections2.transform((Collection)tuples, tuple -> tuple.get(inviteeTable.INVITEE_ID))));
            return returnMap;
        });
    }

    @Override
    public List<String> getInviteesFor(String eventUid) {
        if (eventUid == null) {
            return new ArrayList<String>();
        }
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            InviteeTable inviteeTable = this.getQueryDSLMapper().getInviteeTable();
            EventTable eventTable = this.getQueryDSLMapper().getEventsTable();
            SQLQuery sql = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)inviteeTable)).leftJoin((EntityPath)eventTable)).on((com.querydsl.core.types.Predicate)inviteeTable.EVENT_ID.eq(eventTable.ID))).where(eventTable.VEVENT_UID.eq(eventUid))).orderBy((OrderSpecifier<?>)inviteeTable.INVITEE_ID.desc());
            List results = ((AbstractSQLQuery)sql.select((Expression)inviteeTable.INVITEE_ID)).fetch();
            return results;
        });
    }

    @Override
    public Map<String, Set<String>> getVEventUidsForUserBySubCalendar(ConfluenceUser confluenceUser) {
        EventTable eventsTable = this.getQueryDSLMapper().getEventsTable();
        InviteeTable inviteeTable = this.getQueryDSLMapper().getInviteeTable();
        return this.queryDSLSupplier.executeSQLQuery(query -> {
            SQLQuery userEventsQuery = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)query.from((Expression<?>)eventsTable)).join((EntityPath)inviteeTable)).on((com.querydsl.core.types.Predicate)eventsTable.ID.eq(inviteeTable.EVENT_ID))).where(inviteeTable.INVITEE_ID.eq(confluenceUser.getKey().getStringValue()));
            HashMap<String, Set> eventIdsBySubCalendar = new HashMap<String, Set>();
            List results = ((AbstractSQLQuery)userEventsQuery.select(new Expression[]{eventsTable.VEVENT_UID, eventsTable.SUB_CALENDAR_ID})).fetch();
            for (Tuple result : results) {
                String subCalendarId = result.get(eventsTable.SUB_CALENDAR_ID);
                eventIdsBySubCalendar.computeIfAbsent(subCalendarId, k -> new HashSet()).add(result.get(eventsTable.VEVENT_UID));
            }
            return eventIdsBySubCalendar;
        });
    }

    protected void deleteSubCalendarDisableEvent(SubCalendarEntity subCalendarEntity, String eventType) {
        DatabaseProvider currentDatabaseProvider = subCalendarEntity.getEntityManager().getProvider();
        String subCalendarId = subCalendarEntity.getSubscription() != null ? subCalendarEntity.getSubscription().getID() : subCalendarEntity.getID();
        this.getActiveObjects().deleteWithSQL(DisableEventTypeEntity.class, currentDatabaseProvider.quote("SUB_CALENDAR_ID") + " = ? AND " + currentDatabaseProvider.quote("EVENT_KEY") + " = ?", new Object[]{subCalendarId, eventType});
    }

    protected Map<String, List<String>> getUserRestrictionsMap(T subCalendar) {
        if (((SubCalendar)subCalendar).getParent() == null) {
            return ((SubCalendar)subCalendar).getUserRestrictionMap();
        }
        return ((SubCalendar)subCalendar).getParent().getUserRestrictionMap();
    }

    protected Map<String, List<String>> getGroupRestrictionMap(T subCalendar) {
        if (((SubCalendar)subCalendar).getParent() == null) {
            return ((SubCalendar)subCalendar).getGroupRestrictionMap();
        }
        return ((SubCalendar)subCalendar).getParent().getGroupRestrictionMap();
    }

    @Override
    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser user, String ... childSubCalendars) {
        HashSet<String> childSubCalendarHasReminders = new HashSet<String>();
        if (childSubCalendars.length > 0) {
            ReminderUsersEntity[] reminderUsersEntities;
            ArrayList querySubstitutions = Lists.newArrayList(Arrays.asList(childSubCalendars));
            querySubstitutions.add(user.getKey().getStringValue());
            for (ReminderUsersEntity reminderUsersEntity : reminderUsersEntities = (ReminderUsersEntity[])this.getActiveObjects().find(ReminderUsersEntity.class, Query.select().where("SUB_CALENDAR_ID IN (" + StringUtils.repeat("?", ", ", childSubCalendars.length) + ") AND USER_KEY = ?", querySubstitutions.toArray()))) {
                childSubCalendarHasReminders.add(reminderUsersEntity.getSubCalendar().getID());
            }
        }
        return childSubCalendarHasReminders;
    }

    @Override
    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser user) {
        ReminderUserTable reminderUser = (ReminderUserTable)this.getQueryDSLMapper().getMapping(ReminderUsersEntity.class);
        List finalResults = this.queryDSLSupplier.executeSQLQuery(query -> {
            SQLQuery sql = (SQLQuery)((SQLQuery)query.from((Expression<?>)reminderUser)).where(reminderUser.USER_KEY.in(user.getKey().getStringValue()));
            List results = ((AbstractSQLQuery)sql.select((Expression)reminderUser.SUB_CALENDAR_ID)).fetch();
            return results;
        });
        return ImmutableSet.copyOf((Collection)finalResults);
    }

    @Override
    public Set<String> filterExistSubCalendarIds(String ... subCalendarIds) {
        ArrayList querySubstitutions = Lists.newArrayList(Arrays.asList(subCalendarIds));
        ArrayList results = new ArrayList();
        this.getActiveObjects().stream(SubCalendarEntity.class, Query.select((String)"ID").where("ID IN (" + StringUtils.repeat("?", ", ", subCalendarIds.length) + ")", querySubstitutions.toArray()), subCalendarEntity -> results.add(subCalendarEntity.getID()));
        return ImmutableSet.copyOf(results);
    }

    @Override
    public boolean checkExistCalendarDataStoreFromCache(String subCalendarId) {
        return false;
    }

    @Override
    public void updateJiraReminderEvents(T subCalendar, Calendar subCalendarContent) {
    }

    @Override
    public Set<String> getSubCalendarIdsOnSpace(String spaceKey) {
        HashSet<String> subCalendarIds = new HashSet<String>();
        if (StringUtils.isNotEmpty(spaceKey)) {
            SubCalendarInSpaceEntity[] subCalendarInSpaceEntities;
            for (SubCalendarInSpaceEntity subCalendarInSpaceEntity : subCalendarInSpaceEntities = (SubCalendarInSpaceEntity[])this.getActiveObjects().find(SubCalendarInSpaceEntity.class, Query.select().where("SPACE_KEY = ?", new Object[]{spaceKey}))) {
                subCalendarIds.add(subCalendarInSpaceEntity.getSubCalendar().getID());
            }
        }
        return subCalendarIds;
    }

    @Override
    public void removeSubCalendarFromSpaceView(T subCalendar, String spaceKey) {
        SubCalendarInSpaceEntity[] subCalendarInSpaceEntities;
        if (StringUtils.isNotEmpty(spaceKey) && subCalendar != null && (subCalendarInSpaceEntities = (SubCalendarInSpaceEntity[])this.getActiveObjects().find(SubCalendarInSpaceEntity.class, Query.select().where("SPACE_KEY = ? AND SUB_CALENDAR_ID = ?", new Object[]{spaceKey, ((PersistedSubCalendar)subCalendar).getId()}))) != null && subCalendarInSpaceEntities.length > 0) {
            this.getActiveObjects().delete((RawEntity[])subCalendarInSpaceEntities);
        }
    }

    @Override
    public EventTypeReminder getEventTypeReminder(T subCalendar) {
        String eventType = CalendarUtil.getEventTypeFromStoreKey(((SubCalendar)subCalendar).getStoreKey());
        if (((SubCalendar)subCalendar).getParent() != null && ((SubCalendar)subCalendar).getParent().getEventTypeReminders().size() > 0 && StringUtils.isNotBlank(eventType)) {
            Set<EventTypeReminder> eventTypeReminders = ((SubCalendar)subCalendar).getParent().getEventTypeReminders();
            for (EventTypeReminder eventTypeReminder : eventTypeReminders) {
                if (!eventTypeReminder.getEventTypeId().equals(eventType) || eventTypeReminder.getPeriodInMins() <= 0) continue;
                return eventTypeReminder;
            }
        }
        return null;
    }

    @Override
    public boolean checkExistJiraReminderEvent(String keyId) {
        return this.getActiveObjects().count(JiraReminderEventEntity.class, Query.select().where("KEY_ID = ? ", new Object[]{keyId})) > 0;
    }

    @Override
    public Message getSubCalendarEventWarning(T subCalendar) {
        return null;
    }

    protected boolean isParentCalendar(String parentCalendarId) {
        return StringUtils.isEmpty(parentCalendarId);
    }

    private static class ByRecurrenceIdPredicate
    implements Predicate<VEvent> {
        private final String recurrenceId;
        private final TimeZone calendarTimezone;
        private final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;

        private ByRecurrenceIdPredicate(TimeZone calendarTimezone, @Nullable String recurrenceId, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter) {
            Objects.nonNull(calendarTimezone);
            this.calendarTimezone = calendarTimezone;
            this.recurrenceId = recurrenceId;
            this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
        }

        @Override
        public boolean test(VEvent vEvent) {
            try {
                int comparedValue;
                RecurrenceId recurrenceIdProperty = vEvent.getRecurrenceId();
                if (recurrenceIdProperty == null) {
                    return StringUtils.isBlank(this.recurrenceId);
                }
                if (StringUtils.isBlank(this.recurrenceId)) {
                    return false;
                }
                Date recurrenceDate = recurrenceIdProperty.getDate();
                RecurrenceId requestRecurrentId = new RecurrenceId(this.recurrenceId, recurrenceIdProperty.getTimeZone());
                Date requestedDate = requestRecurrentId.getDate();
                try {
                    DateTime requestedDateJoda = RecurrenceIdJodaTimeHelper.getJodaDateTimeFromRecurrenceId(this.recurrenceId, DateTimeZone.forID((String)this.calendarTimezone.getID()));
                    DateTime recurrenceDateJoda = this.jodaIcal4jDateTimeConverter.toJodaTime(recurrenceDate, this.calendarTimezone);
                    comparedValue = recurrenceDateJoda.toLocalDate().compareTo((ReadablePartial)requestedDateJoda.toLocalDate());
                }
                catch (RuntimeException e) {
                    LOG.warn("Unable to parse recurrence id for comparison when rescheduling event, falling back to daysBetween comparison without timezone");
                    comparedValue = Days.daysBetween((ReadableInstant)new DateTime((Object)recurrenceDate), (ReadableInstant)new DateTime((Object)requestedDate)).getDays();
                }
                LOG.debug("Compare recurrence id: request {} with db {} with result {}", new Object[]{requestedDate, recurrenceDate, comparedValue});
                return comparedValue == 0;
            }
            catch (ParseException e) {
                LOG.error("Could not parse recurrence id from string with this value: {}", (Object)this.recurrenceId);
                return false;
            }
            catch (IllegalArgumentException e) {
                LOG.error("Could not compare recurrence with unknown error", (Throwable)e);
                return false;
            }
        }
    }
}

