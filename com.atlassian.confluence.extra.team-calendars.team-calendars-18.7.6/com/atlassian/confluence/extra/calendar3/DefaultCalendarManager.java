/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.event.events.user.UserRemoveEvent
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.base.Optional
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.ISODateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarPreferenceManager;
import com.atlassian.confluence.extra.calendar3.CalendarUserPreferenceStore;
import com.atlassian.confluence.extra.calendar3.DefaultICalendarExporter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.SubCalendarEventConverter;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.SubCalendarUpdateTracker;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrievalMode;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DefaultReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.RequiresInvitees;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreatedOnEventCreation;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventCreated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventExcluded;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventMoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventRecurrenceRescheduled;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventRemoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventUpdated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventsLoaded;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarLoaded;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRemoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarTrackChangeEvent;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarUpdated;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.exception.ForbiddenCalendarException;
import com.atlassian.confluence.extra.calendar3.exception.NoInviteesException;
import com.atlassian.confluence.extra.calendar3.ical4j.RecurrenceRuleProcessor;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.LightweightPersistentSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.search.impl.DefaultCalendarSearcher;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.CalendarExportTransformer;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.HostInfo;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.bedework.caldav.util.TimeRange;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class DefaultCalendarManager
implements CalendarManager,
SubCalendarEventConverter<PersistedSubCalendar>,
InitializingBean,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCalendarManager.class);
    private final String CALENDAR_MODULE_KEY;
    private final SettingsManager settingsManager;
    private final UidGenerator uidGenerator;
    private final CalendarDataStore<PersistedSubCalendar> calendarDataStore;
    private final CalendarPermissionManager calendarPermissionManager;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;
    private final SubCalendarColorRegistry subCalendarColorRegistry;
    private final CalendarPreferenceManager calendarPreferenceManager;
    private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
    private final UserAccessor userAccessor;
    private final EventPublisher eventPublisher;
    private final NotificationManager notificationManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final DefaultCalendarSearcher calendarSearcher;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final CalendarUserPreferenceStore calendarUserPreferenceStore;
    private final RecurrenceRuleProcessor recurrenceRuleProcessor;
    private final VEventMapper vEventMapper;
    private final SpacePermissionManager spacePermissionManager;
    private final SubCalendarUpdateTracker subCalendarUpdateTracker;
    private final CalendarHelper calendarHelper;

    @Autowired
    public DefaultCalendarManager(@ComponentImport SettingsManager settingsManager, BuildInformationManager buildInformationManager, @Qualifier(value="calendarDataStore") CalendarDataStore<PersistedSubCalendar> calendarDataStore, CalendarPermissionManager calendarPermissionManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, SubCalendarColorRegistry subCalendarColorRegistry, @Qualifier(value="calendarPreferenceManager") CalendarPreferenceManager calendarPreferenceManager, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, @ComponentImport UserAccessor userAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport NotificationManager notificationManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, DefaultCalendarSearcher calendarSearcher, @ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, CalendarUserPreferenceStore calendarUserPreferenceStore, RecurrenceRuleProcessor recurrenceRuleProcessor, @Qualifier(value="vEventMapper") VEventMapper vEventMapper, @ComponentImport SpacePermissionManager spacePermissionManager, SubCalendarUpdateTracker subCalendarUpdateTracker, CalendarHelper calendarHelper) {
        this.settingsManager = settingsManager;
        this.calendarHelper = calendarHelper;
        this.uidGenerator = new FixedUidGenerator(new BaseUrlHostInfo(this.settingsManager), String.valueOf(new Random().nextInt()));
        this.calendarDataStore = calendarDataStore;
        this.calendarPermissionManager = calendarPermissionManager;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
        this.subCalendarColorRegistry = subCalendarColorRegistry;
        this.calendarPreferenceManager = calendarPreferenceManager;
        this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
        this.userAccessor = userAccessor;
        this.eventPublisher = eventPublisher;
        this.notificationManager = notificationManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.calendarSearcher = calendarSearcher;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.calendarUserPreferenceStore = calendarUserPreferenceStore;
        this.recurrenceRuleProcessor = recurrenceRuleProcessor;
        this.vEventMapper = vEventMapper;
        this.spacePermissionManager = spacePermissionManager;
        this.subCalendarUpdateTracker = subCalendarUpdateTracker;
        this.CALENDAR_MODULE_KEY = buildInformationManager.getPluginKey() + ":calendar-resources";
    }

    @Override
    public boolean hasSubCalendar(String subCalendarId) {
        return this.calendarDataStore.hasSubCalendar(subCalendarId);
    }

    @Override
    public List<String> getAllCalendarUsers(long start, long limit) throws Exception {
        return this.calendarUserPreferenceStore.list(start, limit);
    }

    @Override
    public List<String> filterSubCalendarIds(String ... subCalendarIds) {
        UtilTimerStack.push((String)("CalendarManager.hasSubCalendar() -- size " + subCalendarIds.length));
        try {
            List<String> list = this.calendarDataStore.filterSubCalendarIds(subCalendarIds);
            return list;
        }
        finally {
            UtilTimerStack.pop((String)("CalendarManager.hasSubCalendar() -- size " + subCalendarIds.length));
        }
    }

    @Override
    public PersistedSubCalendar getSubCalendar(String subCalendarId) {
        long loadStart = System.currentTimeMillis();
        List<PersistedSubCalendar> persistedSubCalendars = this.calendarDataStore.getSubCalendarsWithRestriction(subCalendarId);
        if (persistedSubCalendars.size() == 0) {
            throw new CalendarException("Unable to get subcalendar from data store with id: " + subCalendarId);
        }
        PersistedSubCalendar persistedSubCalendar = persistedSubCalendars.get(0);
        this.eventPublisher.publish((Object)new SubCalendarLoaded(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, System.currentTimeMillis() - loadStart));
        return persistedSubCalendar;
    }

    @Override
    public com.google.common.base.Optional<PersistedSubCalendar> getPersistedSubCalendar(String subCalendarId) {
        long loadStart = System.currentTimeMillis();
        List<PersistedSubCalendar> persistedSubCalendars = this.calendarDataStore.getSubCalendarsWithRestriction(subCalendarId);
        if (persistedSubCalendars.size() == 0) {
            return com.google.common.base.Optional.fromNullable(null);
        }
        PersistedSubCalendar persistedSubCalendar = persistedSubCalendars.get(0);
        this.eventPublisher.publish((Object)new SubCalendarLoaded(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, System.currentTimeMillis() - loadStart));
        return com.google.common.base.Optional.of((Object)persistedSubCalendar);
    }

    @Override
    public List<PersistedSubCalendar> getSubCalendarsWithRestriction(String ... subCalendarIds) {
        long loadStart = System.currentTimeMillis();
        List<PersistedSubCalendar> persistedSubCalendars = this.calendarDataStore.getSubCalendarsWithRestriction(subCalendarIds);
        long loadEnd = System.currentTimeMillis();
        for (PersistedSubCalendar persistedSubCalendar : persistedSubCalendars) {
            this.eventPublisher.publish((Object)new SubCalendarLoaded(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, loadEnd - loadStart));
        }
        return persistedSubCalendars;
    }

    @Override
    public Set<Message> getSubCalendarWarnings(PersistedSubCalendar persistedSubCalendar) {
        return new HashSet<Message>(this.calendarDataStore.getSubCalendarWarnings(persistedSubCalendar));
    }

    @Override
    public SubCalendarSummary getSubCalendarSummary(String subCalendarId) {
        return this.calendarDataStore.getSubCalendarSummary(subCalendarId);
    }

    @Override
    public void validateSubCalendar(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        this.calendarDataStore.validate(subCalendar, fieldErrors);
    }

    @Override
    public PersistedSubCalendar save(SubCalendar subCalendar) throws Exception {
        PersistedSubCalendar persistedSubCalendar = this.calendarDataStore.save(subCalendar);
        if (!(subCalendar instanceof PersistedSubCalendar) && subCalendar.getParent() == null) {
            this.calendarPreferenceManager.addToView(AuthenticatedUserThreadLocal.get(), persistedSubCalendar);
        }
        for (PersistedSubCalendar childSubCalendar : this.getChildSubCalendars(persistedSubCalendar, null)) {
            this.updateSubCalendarRecursively(childSubCalendar, persistedSubCalendar.getTimeZoneId(), persistedSubCalendar.getSpaceKey());
        }
        this.eventPublisher.publish((Object)(subCalendar instanceof PersistedSubCalendar ? new SubCalendarUpdated((Object)this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar) : new SubCalendarCreated((Object)this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar)));
        return persistedSubCalendar;
    }

    @Override
    public Collection<PersistedSubCalendar> getRemindedChildSubCalendar(PersistedSubCalendar parentCalendar) {
        if (parentCalendar instanceof SubscribingSubCalendar) {
            parentCalendar = this.getSubCalendar(((SubscribingSubCalendar)parentCalendar).getSubscriptionId());
        }
        PersistedSubCalendar processParentCalendar = parentCalendar;
        Collection<PersistedSubCalendar> childSubCalendars = this.getChildSubCalendars(parentCalendar, null);
        Collection childSubCalendarHasReminder = Collections2.filter(childSubCalendars, childSubCalendar -> {
            for (EventTypeReminder eventTypeReminder : processParentCalendar.getEventTypeReminders()) {
                if (!eventTypeReminder.getEventTypeId().equalsIgnoreCase(childSubCalendar.getType()) && !eventTypeReminder.getEventTypeId().equalsIgnoreCase(childSubCalendar.getCustomEventTypeId()) && (!CalendarUtil.isJiraSubCalendarType(childSubCalendar.getType()) || !eventTypeReminder.getEventTypeId().equalsIgnoreCase(CalendarUtil.getEventTypeFromStoreKey(childSubCalendar.getStoreKey())))) continue;
                return true;
            }
            return false;
        });
        return childSubCalendarHasReminder;
    }

    private void updateSubCalendarRecursively(PersistedSubCalendar aSubCalendar, String timeZoneId, String spaceKey) throws Exception {
        for (PersistedSubCalendar childSubCalendar : this.getChildSubCalendars(aSubCalendar, null)) {
            this.updateSubCalendarRecursively(childSubCalendar, timeZoneId, spaceKey);
        }
        aSubCalendar.setTimeZoneId(timeZoneId);
        aSubCalendar.setSpaceKey(spaceKey);
        this.calendarDataStore.save(aSubCalendar);
    }

    private Collection<PersistedSubCalendar> getChildSubCalendars(PersistedSubCalendar parent, com.google.common.base.Predicate<PersistedSubCalendar> predicate) {
        Collection<PersistedSubCalendar> childSubCalendars = Collections.emptySet();
        if (parent.getChildSubCalendarIds() != null) {
            predicate = predicate != null ? Predicates.and((com.google.common.base.Predicate)Predicates.notNull(), (com.google.common.base.Predicate)predicate) : Predicates.notNull();
            childSubCalendars = Lists.newArrayList((Iterable)Collections2.filter((Collection)Collections2.transform(parent.getChildSubCalendarIds(), this::getSubCalendar), (com.google.common.base.Predicate)predicate));
        }
        return childSubCalendars;
    }

    @Override
    public Calendar transform(PersistedSubCalendar persistedSubCalendar, Calendar source) throws Exception {
        return new CalendarExportTransformer(this.settingsManager, this.userAccessor, persistedSubCalendar, this.jodaIcal4jTimeZoneMapper, this.uidGenerator, this.calendarHelper).transform(source);
    }

    private Calendar getTransformedSubCalendarContent(PersistedSubCalendar persistedSubCalendar) throws Exception {
        return new CalendarExportTransformer(this.settingsManager, this.userAccessor, persistedSubCalendar, this.jodaIcal4jTimeZoneMapper, this.uidGenerator, this.calendarHelper).transform(this.getSubCalendarContent(persistedSubCalendar));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSubCalendar(PersistedSubCalendar subCalendar) {
        Calendar subCalendarData = null;
        HashSet usersSubscribedToSubCalendar = new HashSet();
        ArrayList<PersistedSubCalendar> subscriptions = new ArrayList<PersistedSubCalendar>();
        if (!this.isSubscribingSubCalendar(subCalendar)) {
            if (subCalendar instanceof LocallyManagedSubCalendar) {
                try {
                    subCalendarData = this.getTransformedSubCalendarContent(subCalendar);
                    if (subCalendar.getChildSubCalendarIds() != null && !subCalendar.getChildSubCalendarIds().isEmpty()) {
                        ComponentList<CalendarComponent> subCalendarContentComponents = subCalendarData.getComponents();
                        Collection<PersistedSubCalendar> childSubCalendars = this.getChildSubCalendars(subCalendar, null);
                        for (PersistedSubCalendar childSubCalendar : Collections2.filter(childSubCalendars, (com.google.common.base.Predicate)Predicates.instanceOf(LocallyManagedSubCalendar.class))) {
                            Calendar childSubCalendarContent = this.getTransformedSubCalendarContent(childSubCalendar);
                            ComponentList vEventList = childSubCalendarContent.getComponents("VEVENT");
                            subCalendarContentComponents.addAll(vEventList);
                        }
                        DefaultICalendarExporter.exportExternallySourcedSubCalendars(subCalendarContentComponents, childSubCalendars);
                    }
                }
                catch (Exception e) {
                    LOG.error(String.format("Unable to retrieve events of sub-calendar %s", subCalendar.getId()), (Throwable)e);
                }
            }
            usersSubscribedToSubCalendar = new HashSet(Collections2.filter(this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar(subCalendar, true), user -> this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, (ConfluenceUser)user)));
            subscriptions.addAll(Collections2.filter((Collection)Collections2.transform(this.subCalendarSubscriptionStatisticsAccessor.getSubscribingSubCalendarIds(subCalendar), this.calendarDataStore::getSubCalendar), Objects::nonNull));
            for (PersistedSubCalendar subscriptionSubCalendar : subscriptions) {
                for (ConfluenceUser subscriber : usersSubscribedToSubCalendar) {
                    this.calendarPreferenceManager.removeFromView(subscriber, subscriptionSubCalendar);
                }
            }
        }
        this.calendarPreferenceManager.removeFromView(AuthenticatedUserThreadLocal.get(), subCalendar);
        this.calendarDataStore.remove(subCalendar);
        try {
            this.eventPublisher.publish((Object)new SubCalendarRemoved(this, AuthenticatedUserThreadLocal.get(), subCalendar, subCalendarData, new HashSet<String>(Collections2.transform(usersSubscribedToSubCalendar, user -> user.getKey().toString())), subscriptions));
        }
        catch (Throwable throwable) {
            for (ConfluenceUser subscriber : usersSubscribedToSubCalendar) {
                for (PersistedSubCalendar subscription : this.filterSubCalendarsByCreator(subscriptions, subscriber)) {
                    this.unwatchSubCalendar(subscription, subscriber);
                }
            }
            ConfluenceUser user2 = this.userAccessor.getUserByKey(new UserKey(StringUtils.defaultString((String)subCalendar.getCreator())));
            if (null != user2) {
                this.unwatchSubCalendar(subCalendar, user2);
            }
            throw throwable;
        }
        for (ConfluenceUser subscriber : usersSubscribedToSubCalendar) {
            for (PersistedSubCalendar subscription : this.filterSubCalendarsByCreator(subscriptions, subscriber)) {
                this.unwatchSubCalendar(subscription, subscriber);
            }
        }
        ConfluenceUser user3 = this.userAccessor.getUserByKey(new UserKey(StringUtils.defaultString((String)subCalendar.getCreator())));
        if (null != user3) {
            this.unwatchSubCalendar(subCalendar, user3);
        }
    }

    private Collection<PersistedSubCalendar> filterSubCalendarsByCreator(Collection<PersistedSubCalendar> subCalendars, ConfluenceUser creator) {
        return subCalendars.stream().filter(subCalendar -> StringUtils.equals((CharSequence)subCalendar.getCreator(), (CharSequence)creator.getKey().getStringValue())).collect(Collectors.toList());
    }

    private boolean isSubscribingSubCalendar(PersistedSubCalendar persistedSubCalendar) {
        return persistedSubCalendar instanceof SubscribingSubCalendar;
    }

    @Override
    public Collection<SubCalendarEvent> query(ConfluenceUser user, PersistedSubCalendar subCalendar, FilterBase filter, RecurrenceRetrieval recurrenceRetrieval) throws Exception {
        RecurrenceRetrievalMode recurrenceRetrievalMode = recurrenceRetrieval.getRecurrenceRetrievalMode();
        Set<SubCalendarEvent> subCalendarEvents = this.executeActionOnHierachy(subCalendar, persistedSubCalendar -> {
            try {
                Function<Void, Boolean> eventEditPermissionChecker = this.getEventPermisionChecker((PersistedSubCalendar)persistedSubCalendar);
                TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(persistedSubCalendar.getTimeZoneId());
                Collection<VEvent> vEvents = this.calendarDataStore.query((PersistedSubCalendar)persistedSubCalendar, filter, recurrenceRetrieval);
                if (recurrenceRetrievalMode == RecurrenceRetrievalMode.OVERRIDE) {
                    Collection returnEvents = vEvents.stream().map(vEvent -> this.toSubCalendarEvent((VEvent)vEvent, (PersistedSubCalendar)persistedSubCalendar, subCalendarTimeZone, eventEditPermissionChecker)).collect(Collectors.toList());
                    return Either.right(new LinkedHashSet(returnEvents));
                }
                TimeRange expandTimeRange = recurrenceRetrieval.getTimeRange().get();
                DateTime startExpandTimeRange = new DateTime((Object)expandTimeRange.getStart());
                DateTime endExpandTimeRange = new DateTime((Object)expandTimeRange.getEnd());
                Collection<SubCalendarEvent> returnEvents = this.recurrenceRuleProcessor.getRecurrenceEvents(this, (List<VEvent>)new ArrayList<VEvent>(vEvents), subCalendarTimeZone, (PersistedSubCalendar)persistedSubCalendar, startExpandTimeRange, endExpandTimeRange, eventEditPermissionChecker);
                return Either.right(new LinkedHashSet<SubCalendarEvent>(returnEvents));
            }
            catch (Exception e) {
                return Either.left((Object)e);
            }
        });
        return subCalendarEvents;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar subCalendar) throws Exception {
        String methodSignature = "getEvents(PersistedSubCalendar subCalendar)";
        UtilTimerStack.push((String)"getEvents(PersistedSubCalendar subCalendar)");
        LinkedHashSet<SubCalendarEvent> result = new LinkedHashSet<SubCalendarEvent>();
        try {
            Set subResult = this.executeActionOnHierachy(subCalendar, persistedSubCalendar -> {
                try {
                    LinkedHashSet events = new LinkedHashSet();
                    List<VEvent> eventComponents = null;
                    eventComponents = this.calendarDataStore.getEvents((PersistedSubCalendar)persistedSubCalendar);
                    TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
                    Function<Void, Boolean> eventPermissionChecker = this.getEventPermisionChecker(subCalendar);
                    List subCalendarEvents = eventComponents.stream().map(vEvent -> this.toSubCalendarEvent((VEvent)vEvent, subCalendar, subCalendarTimeZone, eventPermissionChecker)).collect(Collectors.toList());
                    events.addAll(subCalendarEvents);
                    return Either.right(events);
                }
                catch (Exception e) {
                    return Either.left((Object)e);
                }
            });
            result.addAll(subResult);
        }
        finally {
            UtilTimerStack.pop((String)"getEvents(PersistedSubCalendar subCalendar)");
        }
        return result;
    }

    private <T> Set<T> executeActionOnHierachy(PersistedSubCalendar subCalendar, Function<PersistedSubCalendar, Either<Exception, Collection<T>>> function) throws Exception {
        LinkedHashSet returnSet = new LinkedHashSet();
        List<PersistedSubCalendar> calendars = Collections.singletonList(subCalendar);
        if ("parent".equals(subCalendar.getType())) {
            LOG.debug("ExecuteActionOnHierachy on parent calendar");
            ParentSubCalendarDataStore.ParentSubCalendar parentSubCalendar = (ParentSubCalendarDataStore.ParentSubCalendar)subCalendar;
            String[] childSubCalendarIds = parentSubCalendar.getChildSubCalendarIds().toArray(new String[0]);
            calendars = this.calendarDataStore.getSubCalendarsWithRestriction(childSubCalendarIds);
        }
        for (PersistedSubCalendar calendar : calendars) {
            if (calendar == null) continue;
            Either result = (Either)function.apply((Object)calendar);
            if (result.isLeft()) {
                Exception e = (Exception)result.left().get();
                LOG.error("Error on sub calendar {}", (Object)calendar.toString(), (Object)e);
                throw e;
            }
            returnSet.addAll((Collection)result.right().getOrElse(Collections.emptySet()));
        }
        return returnSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar subCalendar, DateTime start, DateTime end) throws Exception {
        String methodSignature = "getEvents(PersistedSubCalendar subCalendar, DateTime start, DateTime end)";
        UtilTimerStack.push((String)methodSignature);
        long loadStart = System.currentTimeMillis();
        try {
            if (end.isBefore((ReadableInstant)start)) {
                throw new IllegalArgumentException("End date " + end + " is before start date " + start);
            }
            List<VEvent> vEventList = this.calendarDataStore.getEvents(subCalendar, start, end);
            TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
            Function<Void, Boolean> eventPermissionChecker = this.getEventPermisionChecker(subCalendar);
            Collection<SubCalendarEvent> subCalendarEvents = this.recurrenceRuleProcessor.getRecurrenceEvents(this, vEventList, subCalendarTimeZone, subCalendar, start, end, eventPermissionChecker);
            LinkedHashSet<SubCalendarEvent> events = new LinkedHashSet<SubCalendarEvent>(subCalendarEvents);
            this.eventPublisher.publish((Object)new SubCalendarEventsLoaded(this, AuthenticatedUserThreadLocal.get(), subCalendar, System.currentTimeMillis() - loadStart, start, end, events));
            LinkedHashSet<SubCalendarEvent> linkedHashSet = events;
            return linkedHashSet;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) throws Exception {
        Preconditions.checkNotNull((Object)subCalendar);
        Preconditions.checkNotNull((Object)vEventUids);
        Preconditions.checkArgument((vEventUids.length > 0 ? 1 : 0) != 0);
        String methodSignature = "getEvents(PersistedSubCalendar subCalendar, String[] vEventUids, Predicate<VEvent> vEventPredicate)";
        UtilTimerStack.push((String)methodSignature);
        try {
            Set<SubCalendarEvent> set = this.executeActionOnHierachy(subCalendar, childSubCalendar -> {
                try {
                    TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
                    List<VEvent> eventComponents = null;
                    eventComponents = this.calendarDataStore.getEvents((PersistedSubCalendar)childSubCalendar, vEventPredicate, vEventUids);
                    Function<Void, Boolean> eventPermissionChecker = this.getEventPermisionChecker(subCalendar);
                    Set events = eventComponents.stream().map(eventComponent -> this.toSubCalendarEvent((VEvent)eventComponent, (PersistedSubCalendar)childSubCalendar, subCalendarTimeZone, eventPermissionChecker)).collect(Collectors.toSet());
                    return Either.right(events);
                }
                catch (Exception e) {
                    return Either.left((Object)e);
                }
            });
            return set;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    @Override
    public Set<SubCalendarEvent> getUpcomingEvents(PersistedSubCalendar subCalendar, DateTime start, DateTime end) throws Exception {
        if (end.isBefore((ReadableInstant)start)) {
            throw new IllegalArgumentException("End date " + end + " is before start date " + start);
        }
        DateTimeZone subCalendarTimeZoneJoda = DateTimeZone.forID((String)subCalendar.getTimeZoneId());
        DateTime startRange = start.withZone(subCalendarTimeZoneJoda);
        DateTime endRange = end.withZone(subCalendarTimeZoneJoda);
        List<VEvent> eventComponents = this.calendarDataStore.getEvents(subCalendar, startRange, endRange);
        TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
        Function<Void, Boolean> eventPermissionChecker = this.getEventPermisionChecker(subCalendar);
        Collection<SubCalendarEvent> subCalendarEvents = this.recurrenceRuleProcessor.getRecurrenceEvents(this, eventComponents, subCalendarTimeZone, subCalendar, start, end, eventPermissionChecker);
        LinkedHashSet<SubCalendarEvent> events = new LinkedHashSet<SubCalendarEvent>(Collections2.filter(subCalendarEvents, event -> {
            if (event.isAllDay()) {
                DateTime eventStartDate = event.getStartTime();
                return eventStartDate.getYear() == start.getYear() && eventStartDate.getMonthOfYear() == start.getMonthOfYear() && eventStartDate.getDayOfMonth() == start.getDayOfMonth();
            }
            return true;
        }));
        return events;
    }

    @Override
    public JodaIcal4jDateTimeConverter getJodaIcal4jDateTimeConverter() {
        return this.jodaIcal4jDateTimeConverter;
    }

    @Override
    public VEvent getEvent(PersistedSubCalendar subCalendar, String vEventUid, String recurrenceId) throws Exception {
        Set foundSetOfEvent = this.executeActionOnHierachy(subCalendar, persistedSubCalendar -> {
            try {
                VEvent foundEvent = this.calendarDataStore.getEvent((PersistedSubCalendar)persistedSubCalendar, vEventUid, recurrenceId);
                if (foundEvent == null) {
                    return Either.right(Collections.emptyList());
                }
                return Either.right(Collections.singleton(foundEvent));
            }
            catch (Exception e) {
                return Either.left((Object)e);
            }
        });
        Optional firstFound = foundSetOfEvent.stream().findFirst();
        if (!firstFound.isPresent()) {
            LOG.warn("Could not load event with event id {} and recurrence id  {}", (Object)vEventUid, (Object)recurrenceId);
            return null;
        }
        return (VEvent)firstFound.get();
    }

    @Override
    public SubCalendarEvent toSubCalendarEvent(VEvent eventComponent, PersistedSubCalendar subCalendar, TimeZone subCalendarTimeZone, Function<Void, Boolean> permissionChecker) {
        String methodSignature = "toSubCalendarEvent(VEvent, PersistedSubCalendar, TimeZone)";
        UtilTimerStack.push((String)methodSignature);
        LOG.debug("VEVENT to transform is: {}", (Object)eventComponent);
        try {
            DateTime nextDay;
            Object shouldSkipTimeZone;
            RecurrenceId recurrenceId;
            Location locationProperty;
            Description descriptionProperty;
            Url urlProperty;
            Summary summaryProperty;
            Object categories;
            Object rruleProperty;
            PropertyList<ExDate> exDates;
            Object subCalendarType;
            Object customEventTypeProperty;
            SubCalendarEvent subCalendarEvent = new SubCalendarEvent();
            subCalendarEvent.setSubCalendar(subCalendar);
            subCalendarEvent.setUid(eventComponent.getUid().getValue());
            subCalendarEvent.setEventType(subCalendar.getType());
            Status statusProperty = eventComponent.getStatus();
            if (statusProperty != null) {
                subCalendarEvent.setStatus(statusProperty.getValue());
            }
            if ((customEventTypeProperty = eventComponent.getProperty("X-CONFLUENCE-CUSTOM-TYPE-ID")) != null) {
                subCalendarEvent.setCustomEventTypeId(((Content)customEventTypeProperty).getValue());
            }
            if ((subCalendarType = eventComponent.getProperty("X-CONFLUENCE-SUBCALENDAR-TYPE")) != null) {
                subCalendarEvent.setEventType(((Content)subCalendarType).getValue());
            }
            if (!(exDates = eventComponent.getProperties("EXDATE")).isEmpty()) {
                subCalendarEvent.setExDates(exDates);
            }
            if (null != (rruleProperty = eventComponent.getProperty("RRULE"))) {
                subCalendarEvent.setRruleStr(((Content)rruleProperty).getValue());
            }
            if (null != (categories = eventComponent.getProperty("CATEGORIES"))) {
                subCalendarEvent.setEventTypeName(((Content)categories).getValue());
            } else {
                subCalendarEvent.setEventTypeName("other");
            }
            if (eventComponent.getLastModified() != null) {
                long time = eventComponent.getLastModified().getDateTime().getTime();
                subCalendarEvent.setLastModifiedDate(String.valueOf(time));
            }
            if (null != (summaryProperty = eventComponent.getSummary())) {
                subCalendarEvent.setName(((Content)summaryProperty).getValue());
            }
            if (null != (urlProperty = eventComponent.getUrl())) {
                subCalendarEvent.setUrl(((Content)urlProperty).getValue());
            }
            if (null != (descriptionProperty = eventComponent.getDescription())) {
                subCalendarEvent.setDescription(((Content)descriptionProperty).getValue());
            }
            if (null != (locationProperty = eventComponent.getLocation())) {
                subCalendarEvent.setLocation(((Content)locationProperty).getValue());
            }
            if (null != (recurrenceId = eventComponent.getRecurrenceId()) && StringUtils.isNotBlank((CharSequence)recurrenceId.getValue())) {
                subCalendarEvent.setRecurrenceId(recurrenceId.getValue());
                subCalendarEvent.setOriginalStartTime(this.jodaIcal4jDateTimeConverter.toJodaTime(recurrenceId.getDate(), subCalendarTimeZone));
            }
            if ((shouldSkipTimeZone = eventComponent.getProperty("skipSubCalendarTimezone")) != null) {
                if (subCalendarEvent.getExtraProperties() == null) {
                    subCalendarEvent.setExtraProperties(new HashMap<String, String>());
                }
                subCalendarEvent.getExtraProperties().put("skipSubCalendarTimezone", Boolean.toString(true));
            }
            Date startDate = eventComponent.getStartDate().getDate();
            Date endDate = eventComponent.getEndDate().getDate();
            subCalendarEvent.setStartTime(this.jodaIcal4jDateTimeConverter.toJodaTime(startDate, subCalendarTimeZone));
            subCalendarEvent.setEndTime(this.jodaIcal4jDateTimeConverter.toJodaTime(endDate, subCalendarTimeZone));
            if (!(startDate instanceof net.fortuna.ical4j.model.DateTime) && !(endDate instanceof net.fortuna.ical4j.model.DateTime)) {
                subCalendarEvent.setAllDay(true);
            } else if ("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE".equals(subCalendar.getStoreKey()) && (nextDay = subCalendarEvent.getStartTime().plusDays(1)).millisOfDay().get() == 0 && nextDay.equals((Object)subCalendarEvent.getEndTime())) {
                subCalendarEvent.setAllDay(true);
            }
            DtStart orginalDtStart = (DtStart)eventComponent.getProperty("DTSTART");
            DateTime orginalStartDate = this.jodaIcal4jDateTimeConverter.toJodaTime(orginalDtStart.getDate(), subCalendarTimeZone);
            subCalendarEvent.setOriginalStartDate(orginalStartDate);
            DtEnd orginalDtEnd = (DtEnd)eventComponent.getProperty("DTEND");
            if (orginalDtEnd != null) {
                DateTime orginalEndDate = this.jodaIcal4jDateTimeConverter.toJodaTime(orginalDtEnd.getDate(), subCalendarTimeZone);
                subCalendarEvent.setOriginalEndDate(orginalEndDate);
            } else {
                Duration duration = (Duration)eventComponent.getProperty("DURATION");
                DateTime endDateTime = orginalStartDate.plus(duration.getDuration().get(ChronoUnit.MILLIS));
                subCalendarEvent.setOriginalEndDate(endDateTime);
            }
            PropertyList attendeeProperties = eventComponent.getProperties("ATTENDEE");
            if (!attendeeProperties.isEmpty()) {
                TreeSet<Invitee> invitees = new TreeSet<Invitee>();
                for (Attendee attendeeProperty : attendeeProperties) {
                    ConfluenceUser confluenceUser = this.calendarHelper.getUser(attendeeProperty, this.userAccessor);
                    if (null == confluenceUser) continue;
                    ConfluenceUserInvitee confluenceUserInvitee = new ConfluenceUserInvitee(confluenceUser);
                    confluenceUserInvitee.setAvatarIconUrl(String.format("%s%s", this.settingsManager.getGlobalSettings().getBaseUrl(), this.userAccessor.getUserProfilePicture((User)confluenceUser).getDownloadPath()));
                    invitees.add(confluenceUserInvitee);
                }
                subCalendarEvent.setInvitees(invitees);
            }
            StringBuilder miscStringBuilder = new StringBuilder();
            subCalendarEvent.setTextColor("#FFFFFF");
            subCalendarEvent.setColorScheme(subCalendar.getColor());
            subCalendarEvent.setBorderColor(miscStringBuilder.append("#").append(this.getSubCalendarColorAsHexValue(subCalendar.getColor())).toString());
            miscStringBuilder.setLength(0);
            subCalendarEvent.setBackgroundColor(miscStringBuilder.append("#").append(this.getSubCalendarColorAsHexValue(subCalendar.getColor())).toString());
            miscStringBuilder.setLength(0);
            subCalendarEvent.setSecondaryBorderColor(miscStringBuilder.append("#").append(this.subCalendarColorRegistry.getEvenMoreLightenedColorHex(subCalendar.getColor())).toString());
            if ((subCalendar.getType() != null && StringUtils.equals((CharSequence)subCalendar.getType(), (CharSequence)"custom") || subCalendar.getTypeKey() != null && StringUtils.equals((CharSequence)subCalendar.getTypeKey(), (CharSequence)"calendar3.subcalendar.type.custom")) && subCalendar.getCustomEventTypes() != null) {
                CustomEventType customEventType = subCalendar.getCustomEventTypes().iterator().next();
                subCalendarEvent.setClassName(customEventType.getIcon());
                subCalendarEvent.setCustomEventTypeId(customEventType.getCustomEventTypeId());
                subCalendarEvent.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.CALENDAR_MODULE_KEY, "img/customeventtype/" + customEventType.getIcon() + "_48.png", UrlMode.ABSOLUTE));
                subCalendarEvent.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.CALENDAR_MODULE_KEY, "img/customeventtype/" + customEventType.getIcon() + "_24.png", UrlMode.ABSOLUTE));
            } else {
                subCalendarEvent.setClassName(subCalendar.getType());
                subCalendarEvent.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.CALENDAR_MODULE_KEY, "img/events_48.png", UrlMode.ABSOLUTE));
                subCalendarEvent.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.CALENDAR_MODULE_KEY, "img/events_24.png", UrlMode.ABSOLUTE));
            }
            if (permissionChecker != null) {
                subCalendarEvent.setEditable((Boolean)permissionChecker.apply(null));
            }
            SubCalendarEvent subCalendarEvent2 = this.calendarDataStore.transform(subCalendarEvent, eventComponent);
            return subCalendarEvent2;
        }
        catch (CalendarException ce) {
            LOG.error("Exception while calling toSubCalendarEvent ", (Throwable)ce);
            throw new CalendarException((Exception)ce, ce.isCustomError(), ce.getMessage() != null ? ce.getMessage() : ce.getErrorMessageKey(), new Object[0]);
        }
        catch (Exception e) {
            LOG.error("Exception while calling toSubCalendarEvent ", (Throwable)e);
            throw new CalendarException(e, "calendar3.error.loadevents.notexist", new Object[0]);
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private Function<Void, Boolean> getEventPermisionChecker(PersistedSubCalendar subCalendar) {
        boolean isEditable = this.calendarDataStore.hasEditEventPrivilege(subCalendar, AuthenticatedUserThreadLocal.get());
        return aVoid -> isEditable;
    }

    private SubCalendarEvent.Repeat getRepeatFromEventComponent(VEvent vEvent) {
        RRule rRule = (RRule)vEvent.getProperty("RRULE");
        return null != rRule ? new SubCalendarEvent.Repeat(rRule.getValue()) : null;
    }

    @Override
    public String generateUid() {
        return this.uidGenerator.generateUid().getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SubCalendarEvent addEvent(SubCalendarEvent newSubCalendarEvent) throws Exception {
        String method = "addEvent(SubCalendarEvent newSubCalendarEvent)";
        newSubCalendarEvent.setUid((String)StringUtils.defaultIfEmpty((CharSequence)StringUtils.trim((String)newSubCalendarEvent.getUid()), (CharSequence)this.generateUid()));
        PersistedSubCalendar parentSubCalendar = newSubCalendarEvent.getSubCalendar();
        if (parentSubCalendar.getDisableEventTypes() != null && parentSubCalendar.getDisableEventTypes().contains(newSubCalendarEvent.getEventType())) {
            throw new CalendarException("calendar3.error.disableEvent.permission.addevent", parentSubCalendar.getName());
        }
        this.checkCustomEventTypeExistsForSubCalendar(newSubCalendarEvent, parentSubCalendar);
        try {
            UtilTimerStack.push((String)method);
            boolean isSubscription = this.isSubscribingSubCalendar(parentSubCalendar);
            PersistedSubCalendar subCalendar = this.getOrCreateSuitableSubCalendarForEvent(this.resolveSourceSubCalendar(parentSubCalendar), newSubCalendarEvent);
            TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
            DateTime originalStartTime = newSubCalendarEvent.getOriginalStartTime();
            VEvent newEventComponent = this.calendarDataStore.addEvent(subCalendar, this.vEventMapper.toVEvent(subCalendar, newSubCalendarEvent));
            VEventToSubCalendarEventFunction vEventToSubCalendarEventFunction = new VEventToSubCalendarEventFunction(subCalendar, subCalendarTimeZone, this.getEventPermisionChecker(subCalendar));
            if (null == originalStartTime) {
                this.eventPublisher.publish((Object)new SubCalendarEventCreated(this, AuthenticatedUserThreadLocal.get(), vEventToSubCalendarEventFunction.apply(newEventComponent)));
            } else {
                this.eventPublisher.publish((Object)new SubCalendarEventRecurrenceRescheduled(this, AuthenticatedUserThreadLocal.get(), vEventToSubCalendarEventFunction.apply(this.calendarDataStore.getEvent(subCalendar, newSubCalendarEvent.getUid(), null)), vEventToSubCalendarEventFunction.apply(newEventComponent)));
            }
            PersistedSubCalendar persistedSubCalendar = isSubscription ? this.getChildSubCalendarOfSubscriptionSubscribingToSubCalendar(newSubCalendarEvent.getSubCalendar().getId(), subCalendar) : subCalendar;
            SubCalendarEvent subCalendarEvent = this.toSubCalendarEvent(newEventComponent, persistedSubCalendar, subCalendarTimeZone, this.getEventPermisionChecker(persistedSubCalendar));
            return subCalendarEvent;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    private PersistedSubCalendar getChildSubCalendarOfSubscriptionSubscribingToSubCalendar(String parentSubscribingSubCalendarId, PersistedSubCalendar subscribedSubCalendar) {
        PersistedSubCalendar parentSubscribingSubCalendar = this.getSubCalendar(parentSubscribingSubCalendarId);
        if (!this.isSubscribingSubCalendar(parentSubscribingSubCalendar)) {
            throw new IllegalArgumentException("Specified parent sub-calendarn not a SubscribingSubCalendar.");
        }
        Collection subscribingSubCalendars = Collections2.filter((Collection)Collections2.transform(parentSubscribingSubCalendar.getChildSubCalendarIds(), childSubCalendarId -> (SubscribingSubCalendar)this.calendarDataStore.getSubCalendar((String)childSubCalendarId)), childSubCalendarSummary -> StringUtils.equals((CharSequence)subscribedSubCalendar.getId(), (CharSequence)childSubCalendarSummary.getSubscriptionId()));
        return subscribingSubCalendars.isEmpty() ? null : (PersistedSubCalendar)subscribingSubCalendars.iterator().next();
    }

    private PersistedSubCalendar getOrCreateSuitableSubCalendarForEvent(PersistedSubCalendar parent, SubCalendarEvent subCalendarEvent) throws Exception {
        String eventTypeTemp = subCalendarEvent.getEventType();
        String customEventTypeId = subCalendarEvent.getCustomEventTypeId();
        ArrayList defaultEventTypes = Lists.newArrayList((Object[])new String[]{"other", "leaves", "travel", "birthdays", "jira", "jira-agile-sprint", "jira-project-releases"});
        if (StringUtils.isEmpty((CharSequence)customEventTypeId) && !defaultEventTypes.contains(eventTypeTemp)) {
            eventTypeTemp = "other";
        }
        String eventType = eventTypeTemp;
        if (StringUtils.equals((CharSequence)parent.getType(), (CharSequence)eventType)) {
            return parent;
        }
        Set<String> disableEventType = this.calendarDataStore.getSubCalendar(parent.getId()).getDisableEventTypes();
        if (disableEventType.contains(eventType)) {
            throw new ForbiddenCalendarException("Could not get getOrCreateSuitableSubCalendarForEvent for [" + eventType + "] because it is disable");
        }
        Collection<Object> suitableChildSubCalendars = Collections.emptyList();
        if (parent.getChildSubCalendarIds() != null) {
            suitableChildSubCalendars = this.getChildSubCalendars(parent, (com.google.common.base.Predicate<PersistedSubCalendar>)((com.google.common.base.Predicate)childSubCalendar -> {
                if (StringUtils.equals((CharSequence)eventType, (CharSequence)"custom")) {
                    boolean isExistCustomEventTypeCalendar = false;
                    if (childSubCalendar.getCustomEventTypes() != null) {
                        for (CustomEventType customEventType : childSubCalendar.getCustomEventTypes()) {
                            if (!StringUtils.equals((CharSequence)customEventType.getCustomEventTypeId(), (CharSequence)customEventTypeId)) continue;
                            isExistCustomEventTypeCalendar = true;
                            break;
                        }
                    }
                    return isExistCustomEventTypeCalendar;
                }
                return StringUtils.equals((CharSequence)eventType, (CharSequence)childSubCalendar.getType());
            }));
        }
        if (!suitableChildSubCalendars.isEmpty()) {
            PersistedSubCalendar suitableChildSubCalendar = null;
            String eventUid = subCalendarEvent.getUid();
            if (suitableChildSubCalendars.size() > 1 && StringUtils.isNotBlank((CharSequence)eventUid)) {
                for (PersistedSubCalendar persistedSubCalendar : suitableChildSubCalendars) {
                    if (this.calendarDataStore.getEvent(persistedSubCalendar, eventUid, null) == null) continue;
                    suitableChildSubCalendar = persistedSubCalendar;
                    break;
                }
            } else {
                suitableChildSubCalendar = (PersistedSubCalendar)suitableChildSubCalendars.iterator().next();
            }
            return suitableChildSubCalendar;
        }
        String newCustomEventTypeId = customEventTypeId;
        if (subCalendarEvent.getSubCalendar() != null && !this.isSubscribingSubCalendar(subCalendarEvent.getSubCalendar()) && !StringUtils.equals((CharSequence)parent.getId(), (CharSequence)subCalendarEvent.getSubCalendarId()) && StringUtils.equals((CharSequence)eventType, (CharSequence)"custom")) {
            CustomEventTypeEntity customEventTypeEntity;
            CustomEventType customEventType = null;
            Set<CustomEventType> customEventTypes = subCalendarEvent.getSubCalendar().getCustomEventTypes();
            if (customEventTypes != null) {
                for (CustomEventType item : customEventTypes) {
                    if (!StringUtils.equals((CharSequence)item.getCustomEventTypeId(), (CharSequence)newCustomEventTypeId)) continue;
                    customEventType = item;
                    break;
                }
            }
            if (customEventType != null && (customEventTypeEntity = this.calendarDataStore.updateCustomEventType((Option<ReminderSettingCallback>)Option.option((Object)new DefaultReminderSettingCallback(this.eventPublisher)), parent, null, customEventType.getTitle(), customEventType.getIcon(), 0)) != null) {
                newCustomEventTypeId = customEventTypeEntity.getID();
            }
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        SubCalendar childSubCalendar2 = new SubCalendar();
        childSubCalendar2.setParent(parent);
        childSubCalendar2.setName(parent.getName());
        childSubCalendar2.setDescription(parent.getDescription());
        childSubCalendar2.setTimeZoneId(parent.getTimeZoneId());
        childSubCalendar2.setType(eventType);
        childSubCalendar2.setCustomEventTypeId(newCustomEventTypeId);
        PersistedSubCalendar persistedSubCalendar = this.calendarDataStore.save(childSubCalendar2);
        this.eventPublisher.publish((Object)new SubCalendarCreatedOnEventCreation((Object)persistedSubCalendar, currentUser, parent));
        return persistedSubCalendar;
    }

    private PersistedSubCalendar resolveSourceSubCalendar(PersistedSubCalendar persistedSubCalendar) {
        return this.isSubscribingSubCalendar(persistedSubCalendar) ? this.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId()) : persistedSubCalendar;
    }

    @Override
    public SubCalendarEvent updateEvent(SubCalendarEvent newSubCalendarEventDetails) throws Exception {
        return this.updateEvent(newSubCalendarEventDetails, false);
    }

    private SubCalendarEvent updateEvent(SubCalendarEvent newSubCalendarEventDetails, boolean suppressFireUpdatedEvent) throws Exception {
        PersistedSubCalendar parentSubCalendar = newSubCalendarEventDetails.getSubCalendar();
        if (parentSubCalendar.getDisableEventTypes() != null && parentSubCalendar.getDisableEventTypes().contains(newSubCalendarEventDetails.getEventType())) {
            throw new CalendarException("calendar3.error.disableEvent.permission.updateevent", parentSubCalendar.getName());
        }
        this.checkEventHasInviteesIfRequired(newSubCalendarEventDetails);
        this.checkCustomEventTypeExistsForSubCalendar(newSubCalendarEventDetails, parentSubCalendar);
        return this.updateEventInternal(newSubCalendarEventDetails, suppressFireUpdatedEvent);
    }

    private void checkEventHasInviteesIfRequired(SubCalendarEvent subCalendarEvent) {
        Set<Invitee> invitees;
        if (subCalendarEvent.getSubCalendar() instanceof RequiresInvitees && ((invitees = subCalendarEvent.getInvitees()) == null || invitees.isEmpty())) {
            throw new NoInviteesException("calendar3.event.update.no.invitees", subCalendarEvent.getEventType());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SubCalendarEvent updateEventInternal(SubCalendarEvent newSubCalendarEventDetails, boolean suppressFireUpdatedEvent) throws Exception {
        String method = "updateEventInternal(SubCalendarEvent newSubCalendarEventDetails, boolean suppressFireUpdatedEvent)";
        try {
            UtilTimerStack.push((String)method);
            boolean isSubscription = this.isSubscribingSubCalendar(newSubCalendarEventDetails.getSubCalendar());
            PersistedSubCalendar subCalendar = this.getOrCreateSuitableSubCalendarForEvent(this.resolveSourceSubCalendar(newSubCalendarEventDetails.getSubCalendar()), newSubCalendarEventDetails);
            if (subCalendar == null) {
                throw new CalendarException("Unable to get or create subcalendar for event");
            }
            String subCalendarEventUid = newSubCalendarEventDetails.getUid();
            String subCalendarEventRecurrenceId = newSubCalendarEventDetails.getRecurrenceId();
            TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(StringUtils.isNotBlank((CharSequence)subCalendar.getTimeZoneId()) ? subCalendar.getTimeZoneId() : newSubCalendarEventDetails.getSubCalendar().getTimeZoneId());
            VEvent eventComponentToUpdate = this.calendarDataStore.getEvent(subCalendar, subCalendarEventUid, subCalendarEventRecurrenceId);
            if (null == eventComponentToUpdate) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Unable to find event with id %s in sub-calendar %s", subCalendarEventUid, subCalendar.getName()));
                }
                SubCalendarEvent subCalendarEvent = newSubCalendarEventDetails;
                return subCalendarEvent;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("VEVENT before update is: \n {}", (Object)eventComponentToUpdate.toString());
            }
            SubCalendarEvent previousSubCalendarEvent = null;
            if (!suppressFireUpdatedEvent) {
                previousSubCalendarEvent = new VEventToSubCalendarEventFunction(subCalendar, subCalendarTimeZone, this.getEventPermisionChecker(subCalendar)).apply(eventComponentToUpdate);
            }
            this.vEventMapper.toVEvent(subCalendar, newSubCalendarEventDetails, eventComponentToUpdate);
            eventComponentToUpdate = this.calendarDataStore.updateEvent(subCalendar, eventComponentToUpdate);
            if (!suppressFireUpdatedEvent) {
                this.eventPublisher.publish((Object)new SubCalendarEventUpdated(this, AuthenticatedUserThreadLocal.get(), previousSubCalendarEvent, new VEventToSubCalendarEventFunction(subCalendar, subCalendarTimeZone, this.getEventPermisionChecker(subCalendar)).apply(eventComponentToUpdate)));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Updating VEVENT is: \n {}", (Object)eventComponentToUpdate.toString());
            }
            PersistedSubCalendar persistedSubCalendar = isSubscription ? this.getChildSubCalendarOfSubscriptionSubscribingToSubCalendar(newSubCalendarEventDetails.getSubCalendar().getId(), subCalendar) : subCalendar;
            SubCalendarEvent subCalendarEvent = this.toSubCalendarEvent(eventComponentToUpdate, persistedSubCalendar, subCalendarTimeZone, this.getEventPermisionChecker(persistedSubCalendar));
            return subCalendarEvent;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    @Override
    public com.google.common.base.Optional<SubCalendarEvent> updateEventOrRemoveInvalid(SubCalendarEvent subCalendarEvent) throws Exception {
        try {
            return com.google.common.base.Optional.of((Object)this.updateEvent(subCalendarEvent, true));
        }
        catch (NoInviteesException e) {
            LOG.debug("Removing event [{}] with no invitees", (Object)subCalendarEvent.getUid());
            this.removeEvent(subCalendarEvent.getSubCalendar(), subCalendarEvent.getUid(), subCalendarEvent.getRecurrenceId());
            return com.google.common.base.Optional.absent();
        }
    }

    @Override
    public Collection<SubCalendarEvent> updateOrRemoveInvalidExistingEvents(PersistedSubCalendar subCalendar, Collection<String> vEventUids) throws Exception {
        HashSet<SubCalendarEvent> updatedEvents = new HashSet<SubCalendarEvent>();
        Set<SubCalendarEvent> eventsToUpdate = this.getEvents(subCalendar, null, vEventUids.toArray(new String[0]));
        for (SubCalendarEvent event : eventsToUpdate) {
            SubCalendarEvent subCalendarEvent = (SubCalendarEvent)this.updateEventOrRemoveInvalid(event).orNull();
            if (subCalendarEvent == null) continue;
            updatedEvents.add(subCalendarEvent);
        }
        return updatedEvents;
    }

    @Override
    public SubCalendarEvent stopEventRecurrence(PersistedSubCalendar subCalendar, String eventUid, String recurUntil) throws Exception {
        try {
            PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
            VEvent baseEventComponent = this.calendarDataStore.getEvent(srcSubCalendar, eventUid, null);
            TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId());
            SubCalendarEvent baseEvent = this.toSubCalendarEvent(baseEventComponent, srcSubCalendar, subCalendarTimeZone, this.getEventPermisionChecker(srcSubCalendar));
            DateTimeZone subCalendarDateTimeZone = this.jodaIcal4jTimeZoneMapper.toJodaTimeZone(subCalendar.getTimeZoneId());
            DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(AuthenticatedUserThreadLocal.get()));
            DateTime untilFollowTimeZone = null;
            DateTime newRepeatUntil = null;
            String newRepeatUntilString = null;
            if (baseEvent.isAllDay()) {
                untilFollowTimeZone = ISODateTimeFormat.basicDate().withZone(DateTimeZone.UTC).parseDateTime(recurUntil);
                newRepeatUntil = untilFollowTimeZone.minusDays(1);
                newRepeatUntilString = ISODateTimeFormat.basicDate().print((ReadableInstant)newRepeatUntil);
            } else {
                untilFollowTimeZone = baseEvent.getStartTime().withZone(userTimeZone).withDate(ISODateTimeFormat.basicDate().parseDateTime(recurUntil).toLocalDate());
                untilFollowTimeZone = new DateTime(untilFollowTimeZone.getMillis(), DateTimeZone.UTC);
                newRepeatUntil = untilFollowTimeZone.minusDays(1);
                newRepeatUntilString = ISODateTimeFormat.basicDateTimeNoMillis().print((ReadableInstant)newRepeatUntil);
            }
            if (baseEvent.getStartTime().getMillis() <= untilFollowTimeZone.getMillis()) {
                RRule rRule = new RRule(baseEvent.getRruleStr());
                rRule.getRecur().setUntil(null);
                baseEvent.setRruleStr(rRule.getValue() + ";UNTIL=" + newRepeatUntilString);
                this.updateEvent(baseEvent);
                return this.toSubCalendarEvent(this.calendarDataStore.getEvent(srcSubCalendar, eventUid, null), subCalendar, subCalendarTimeZone, this.getEventPermisionChecker(subCalendar));
            }
            this.removeEventRecurrence(subCalendar, srcSubCalendar, eventUid, baseEventComponent, null);
            return null;
        }
        catch (Exception e) {
            throw new CalendarException(e, "calendar3.error.deleteevents.notexist", eventUid);
        }
    }

    @Override
    public void removeEventOnHierarchy(PersistedSubCalendar subCalendar, String eventUid, String recurrenceId) throws Exception {
        this.executeActionOnHierachy(subCalendar, childSubCalendar -> {
            try {
                this.removeEvent((PersistedSubCalendar)childSubCalendar, eventUid, recurrenceId);
                return Either.right((Object)Collections.EMPTY_LIST);
            }
            catch (Exception e) {
                return Either.left((Object)e);
            }
        });
    }

    @Override
    public void removeEvent(PersistedSubCalendar subCalendar, String eventUid, String recurrenceId) throws Exception {
        String method = "removeEvent(PersistedSubCalendar subCalendar, String eventUid, String recurrenceId)";
        try {
            UtilTimerStack.push((String)method);
            PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
            VEvent baseEvent = this.calendarDataStore.getEvent(srcSubCalendar, eventUid, null);
            this.removeEventRecurrence(subCalendar, srcSubCalendar, eventUid, baseEvent, recurrenceId);
        }
        catch (Exception responseException) {
            throw new CalendarException(responseException, "calendar3.error.deleteevents.notexist", eventUid);
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    private void removeEventRecurrence(PersistedSubCalendar subCalendar, PersistedSubCalendar srcSubCalendar, String eventUid, VEvent baseEvent, String recurrenceId) throws Exception {
        TimeZone srcSubCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(srcSubCalendar.getTimeZoneId());
        List<VEvent> rescheduledRecurrences = this.calendarDataStore.getEvents(srcSubCalendar, (VEvent vEvent) -> vEvent.getRecurrenceId() != null, eventUid);
        for (VEvent rescheduledRecurrence : rescheduledRecurrences) {
            if (StringUtils.isNotBlank((CharSequence)recurrenceId) && !rescheduledRecurrence.getRecurrenceId().getValue().equals(recurrenceId)) continue;
            LOG.debug("Deleting recurrence event with id {} and recurrence id {}", (Object)rescheduledRecurrence.getUid(), (Object)rescheduledRecurrence.getRecurrenceId());
            this.calendarDataStore.deleteEvent(srcSubCalendar, eventUid, rescheduledRecurrence.getRecurrenceId().getValue());
            boolean isAllDay = !(baseEvent.getStartDate().getDate() instanceof net.fortuna.ical4j.model.DateTime) && !(baseEvent.getEndDate().getDate() instanceof net.fortuna.ical4j.model.DateTime);
            Date originalRecurringDate = rescheduledRecurrence.getRecurrenceId().getDate();
            DateTime toExclude = this.jodaIcal4jDateTimeConverter.toJodaTime(originalRecurringDate, srcSubCalendarTimeZone);
            if (isAllDay && originalRecurringDate instanceof net.fortuna.ical4j.model.DateTime) {
                toExclude = toExclude.withZone(DateTimeZone.UTC);
            } else if (!isAllDay && originalRecurringDate instanceof net.fortuna.ical4j.model.DateTime) {
                toExclude = toExclude.withZone(this.jodaIcal4jTimeZoneMapper.toJodaTimeZone(srcSubCalendarTimeZone.getID()));
            }
            this.excludeRecurrence(baseEvent, toExclude);
        }
        if (recurrenceId == null) {
            this.calendarDataStore.deleteEvent(srcSubCalendar, eventUid, null);
        } else {
            this.calendarDataStore.updateEvent(srcSubCalendar, baseEvent);
        }
        VEventToSubCalendarEventFunction vEventToSubCalendarEventFunction = new VEventToSubCalendarEventFunction(subCalendar, srcSubCalendarTimeZone, this.getEventPermisionChecker(subCalendar));
        this.eventPublisher.publish((Object)new SubCalendarEventRemoved(this, AuthenticatedUserThreadLocal.get(), recurrenceId == null ? vEventToSubCalendarEventFunction.apply(baseEvent) : null, Lists.newArrayList((Iterable)Collections2.transform(rescheduledRecurrences, (Function)vEventToSubCalendarEventFunction))));
    }

    private void excludeRecurrence(VEvent targetEventComponent, DateTime excludeDate) {
        Date baseStart = targetEventComponent.getStartDate().getDate();
        Date baseEnd = targetEventComponent.getEndDate().getDate();
        boolean isAllDay = !(baseStart instanceof net.fortuna.ical4j.model.DateTime) && !(baseEnd instanceof net.fortuna.ical4j.model.DateTime);
        TimeZone excludedDateTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(excludeDate.getZone().getID());
        DateList exDateList = isAllDay ? new DateList(Value.DATE) : new DateList(Value.DATE_TIME, excludedDateTimeZone);
        exDateList.add(isAllDay ? this.jodaIcal4jDateTimeConverter.toIcal4jDate(excludeDate) : this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(excludeDate));
        ExDate exDate = new ExDate(exDateList);
        if (!isAllDay) {
            exDate.setTimeZone(excludedDateTimeZone);
        }
        targetEventComponent.getProperties().add(exDate);
    }

    @Override
    public boolean setReminderFor(PersistedSubCalendar subCalendar, ConfluenceUser user, boolean isReminder) throws Exception {
        return this.calendarDataStore.setReminderFor(subCalendar, user, isReminder);
    }

    @Override
    public boolean hasReminderFor(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return user != null && this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, user) && this.calendarDataStore.hasReminderFor(subCalendar, user);
    }

    @Override
    public void disableEventTypes(PersistedSubCalendar subCalendar, List<String> disableEventTypes) throws Exception {
        PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
        this.calendarDataStore.disableEventTypes(srcSubCalendar, disableEventTypes);
    }

    @Override
    public CustomEventType updateCustomEventType(PersistedSubCalendar subCalendar, String customEventTypeId, String title, String icon, int periodInMins) throws Exception {
        PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
        CustomEventTypeEntity customEventTypeEntity = this.calendarDataStore.updateCustomEventType((Option<ReminderSettingCallback>)Option.option((Object)new DefaultReminderSettingCallback(this.eventPublisher)), srcSubCalendar, customEventTypeId, title, icon, periodInMins);
        return new CustomEventType(String.valueOf(customEventTypeEntity.getID()), customEventTypeEntity.getTitle(), customEventTypeEntity.getIcon(), customEventTypeEntity.getBelongSubCalendar().getID(), customEventTypeEntity.getCreated(), periodInMins);
    }

    @Override
    public ReminderSettingEntity updateReminderForSanboxEventType(PersistedSubCalendar subCalendar, String eventTypeId, int periodInMins) throws Exception {
        PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
        return this.calendarDataStore.updateReminderForSanboxEventType((Option<ReminderSettingCallback>)Option.option((Object)new DefaultReminderSettingCallback(this.eventPublisher)), srcSubCalendar, eventTypeId, periodInMins);
    }

    @Override
    public ReminderSettingEntity getReminderSetting(String subCalendarId, String storeKey, String customEventTypeId) {
        return this.calendarDataStore.getReminderSetting(subCalendarId, storeKey, customEventTypeId);
    }

    @Override
    public CustomEventType getCustomEventType(PersistedSubCalendar subCalendar, String customEventTypeId) throws Exception {
        PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
        CustomEventTypeEntity customEventTypeEntity = this.calendarDataStore.getCustomEventType(srcSubCalendar, customEventTypeId);
        if (customEventTypeEntity != null) {
            return new CustomEventType(String.valueOf(customEventTypeEntity.getID()), customEventTypeEntity.getTitle(), customEventTypeEntity.getIcon(), customEventTypeEntity.getBelongSubCalendar().getID(), customEventTypeEntity.getCreated(), 0);
        }
        return null;
    }

    @Override
    public Collection<CustomEventType> getCustomEventTypes(String ... customEventTypeId) {
        return this.calendarDataStore.getCustomEventTypes(customEventTypeId).stream().map(customEventTypeEntity -> new CustomEventType(String.valueOf(customEventTypeEntity.getID()), customEventTypeEntity.getTitle(), customEventTypeEntity.getIcon(), customEventTypeEntity.getBelongSubCalendar().getID(), customEventTypeEntity.getCreated(), 0)).collect(Collectors.toList());
    }

    @Override
    public void deleteDisableEventType(String subCalendarId, String eventType) throws Exception {
        this.calendarDataStore.deleteDisableEventType(subCalendarId, eventType);
    }

    @Override
    public void deleteCustomEventType(String subCalendarId, String customEventTypeId) throws Exception {
        this.calendarDataStore.deleteCustomEventType(subCalendarId, customEventTypeId);
    }

    @Override
    public void excludeEventOnHierarchy(PersistedSubCalendar subCalendar, String eventUid, DateTime excludeDate) throws Exception {
        this.executeActionOnHierachy(subCalendar, childSubCalendar -> {
            try {
                this.excludeEvent((PersistedSubCalendar)childSubCalendar, eventUid, excludeDate);
                return Either.right((Object)Collections.EMPTY_LIST);
            }
            catch (Exception e) {
                return Either.left((Object)e);
            }
        });
    }

    @Override
    public void excludeEvent(PersistedSubCalendar subCalendar, String eventUid, DateTime excludeDate) throws Exception {
        String method = "excludeEvent(PersistedSubCalendar subCalendar, String eventUid, DateTime excludeDate)";
        try {
            UtilTimerStack.push((String)method);
            PersistedSubCalendar srcSubCalendar = this.resolveSourceSubCalendar(subCalendar);
            VEvent baseEvent = this.calendarDataStore.getEvent(srcSubCalendar, eventUid, null);
            if (baseEvent == null) {
                LOG.debug("Could not find VEVENT to update maybe we should find it in another Child Sub Calendar");
                return;
            }
            this.excludeRecurrence(baseEvent, !(baseEvent.getStartDate().getDate() instanceof net.fortuna.ical4j.model.DateTime) && !(baseEvent.getEndDate().getDate() instanceof net.fortuna.ical4j.model.DateTime) ? excludeDate.withZoneRetainFields(DateTimeZone.forID((String)srcSubCalendar.getTimeZoneId())) : excludeDate.withZone(DateTimeZone.forID((String)srcSubCalendar.getTimeZoneId())));
            this.calendarDataStore.updateEvent(srcSubCalendar, baseEvent);
            this.eventPublisher.publish((Object)new SubCalendarEventExcluded(this, AuthenticatedUserThreadLocal.get(), new VEventToSubCalendarEventFunction(subCalendar, this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendar.getTimeZoneId()), this.getEventPermisionChecker(subCalendar)).apply(baseEvent), excludeDate));
        }
        catch (Exception e) {
            throw new CalendarException(e, "calendar3.error.deleteevents.notexist", eventUid);
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    @Override
    public SubCalendarEvent moveEvent(SubCalendarEvent subCalendarEvent, PersistedSubCalendar dstParentSubCalendar) throws Exception {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (!this.calendarDataStore.hasEditEventPrivilege(subCalendarEvent.getSubCalendar(), authenticatedUser)) {
            throw new IllegalArgumentException(String.format("Insufficient privileges to move events from sub-calendar %s (%s)", subCalendarEvent.getSubCalendar().getName(), subCalendarEvent.getSubCalendar().getId()));
        }
        if (!this.calendarDataStore.hasEditEventPrivilege(dstParentSubCalendar, authenticatedUser)) {
            throw new IllegalArgumentException(String.format("Insufficient privileges to move event to sub-calendar %s (%s)", dstParentSubCalendar.getName(), dstParentSubCalendar.getId()));
        }
        String customEventTypeId = subCalendarEvent.getCustomEventTypeId();
        String originalCustomEventTypeId = subCalendarEvent.getOriginalCustomEventTypeId();
        PersistedSubCalendar srcParentSubCalendar = this.resolveSourceSubCalendar(subCalendarEvent.getSubCalendar());
        subCalendarEvent.setCustomEventTypeId(originalCustomEventTypeId);
        this.checkCustomEventTypeExistsForSubCalendar(subCalendarEvent, srcParentSubCalendar);
        PersistedSubCalendar srcChildSubCalendar = this.getOrCreateSuitableSubCalendarForEvent(srcParentSubCalendar, subCalendarEvent);
        VEventToSubCalendarEventFunction sourceSubCalendarVEventToSubCalendarEventFunction = new VEventToSubCalendarEventFunction(srcChildSubCalendar, this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(srcChildSubCalendar.getTimeZoneId()), this.getEventPermisionChecker(srcChildSubCalendar));
        SubCalendarEvent previousSubCalendarEventBase = sourceSubCalendarVEventToSubCalendarEventFunction.apply(this.calendarDataStore.getEvent(srcChildSubCalendar, subCalendarEvent.getUid(), null));
        Predicate<VEvent> isRescheduledRecurrencePredicate = vEvent -> vEvent.getRecurrenceId() != null;
        ArrayList previousSubCalendarEventRescheduledRecurrences = Lists.newArrayList((Iterable)Collections2.transform(this.calendarDataStore.getEvents(srcChildSubCalendar, isRescheduledRecurrencePredicate, subCalendarEvent.getUid()), (Function)sourceSubCalendarVEventToSubCalendarEventFunction));
        subCalendarEvent.setSubCalendar(srcChildSubCalendar);
        SubCalendarEvent updatedEvent = this.updateEventInternal(subCalendarEvent, true);
        boolean isDstParentSubscription = this.isSubscribingSubCalendar(dstParentSubCalendar);
        updatedEvent.setCustomEventTypeId(customEventTypeId);
        if (StringUtils.isNotBlank((CharSequence)originalCustomEventTypeId) && StringUtils.isNotBlank((CharSequence)customEventTypeId) && !StringUtils.equals((CharSequence)customEventTypeId, (CharSequence)originalCustomEventTypeId)) {
            this.checkCustomEventTypeExistsForSubCalendar(updatedEvent, dstParentSubCalendar);
        }
        PersistedSubCalendar dstChildSubCalendar = this.getOrCreateSuitableSubCalendarForEvent(this.resolveSourceSubCalendar(dstParentSubCalendar), updatedEvent);
        TimeZone dstChildSubCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(dstChildSubCalendar.getTimeZoneId());
        this.calendarDataStore.moveEvent(srcChildSubCalendar, updatedEvent.getUid(), dstChildSubCalendar);
        VEventToSubCalendarEventFunction destinationSubCalendarVEventToSubCalendarEventFunction = new VEventToSubCalendarEventFunction(dstChildSubCalendar, dstChildSubCalendarTimeZone, this.getEventPermisionChecker(dstChildSubCalendar));
        this.eventPublisher.publish((Object)new SubCalendarEventMoved(this, AuthenticatedUserThreadLocal.get(), previousSubCalendarEventBase, previousSubCalendarEventRescheduledRecurrences, destinationSubCalendarVEventToSubCalendarEventFunction.apply(this.calendarDataStore.getEvent(dstChildSubCalendar, updatedEvent.getUid(), null)), Lists.newArrayList((Iterable)Collections2.transform(this.calendarDataStore.getEvents(dstChildSubCalendar, isRescheduledRecurrencePredicate, updatedEvent.getUid()), (Function)destinationSubCalendarVEventToSubCalendarEventFunction))));
        PersistedSubCalendar persistedSubCalendar = isDstParentSubscription ? this.getChildSubCalendarOfSubscriptionSubscribingToSubCalendar(dstParentSubCalendar.getId(), dstChildSubCalendar) : dstChildSubCalendar;
        return this.toSubCalendarEvent(this.calendarDataStore.getEvent(dstChildSubCalendar, updatedEvent.getUid(), null), persistedSubCalendar, dstChildSubCalendarTimeZone, this.getEventPermisionChecker(persistedSubCalendar));
    }

    @Override
    public SubCalendarEvent changeEvent(SubCalendarEvent subCalendarEvent, String originalEventType, String eventType, String newSubCalendarId) throws Exception {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (!this.calendarDataStore.hasEditEventPrivilege(subCalendarEvent.getSubCalendar(), authenticatedUser)) {
            throw new IllegalArgumentException(String.format("Insufficient privileges to change events from sub-calendar %s (%s)", subCalendarEvent.getSubCalendar().getName(), subCalendarEvent.getSubCalendar().getId()));
        }
        this.deleteDisableEventType(newSubCalendarId, eventType);
        PersistedSubCalendar dstParentSubCalendar = this.getSubCalendar(newSubCalendarId);
        if (!this.calendarDataStore.hasEditEventPrivilege(dstParentSubCalendar, authenticatedUser)) {
            throw new IllegalArgumentException(String.format("Insufficient privileges to change event to sub-calendar %s (%s)", dstParentSubCalendar.getName(), dstParentSubCalendar.getId()));
        }
        PersistedSubCalendar srcParentSubCalendar = this.resolveSourceSubCalendar(subCalendarEvent.getSubCalendar());
        String customEventTypeId = subCalendarEvent.getCustomEventTypeId();
        String originalCutomEventTypeId = subCalendarEvent.getOriginalCustomEventTypeId();
        subCalendarEvent.setEventType(originalEventType);
        subCalendarEvent.setCustomEventTypeId(originalCutomEventTypeId);
        this.checkCustomEventTypeExistsForSubCalendar(subCalendarEvent, srcParentSubCalendar);
        PersistedSubCalendar srcChildSubCalendar = this.getOrCreateSuitableSubCalendarForEvent(srcParentSubCalendar, subCalendarEvent);
        subCalendarEvent.setSubCalendar(srcChildSubCalendar);
        SubCalendarEvent updatedEvent = this.updateEventInternal(subCalendarEvent, true);
        boolean isDstParentSubscription = this.isSubscribingSubCalendar(dstParentSubCalendar);
        updatedEvent.setEventType(eventType);
        updatedEvent.setCustomEventTypeId(customEventTypeId);
        this.checkCustomEventTypeExistsForSubCalendar(updatedEvent, dstParentSubCalendar);
        PersistedSubCalendar dstChildSubCalendar = this.getOrCreateSuitableSubCalendarForEvent(this.resolveSourceSubCalendar(dstParentSubCalendar), updatedEvent);
        TimeZone dstChildSubCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(dstChildSubCalendar.getTimeZoneId());
        this.calendarDataStore.changeEvent(srcChildSubCalendar, updatedEvent.getUid(), dstChildSubCalendar);
        try {
            String subCalendarEventUid = updatedEvent.getUid();
            String subCalendarEventRecurrenceId = updatedEvent.getRecurrenceId();
            TimeZone previousChildSubCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(srcChildSubCalendar.getTimeZoneId());
            VEvent eventComponentToUpdate = this.calendarDataStore.getEvent(dstChildSubCalendar, subCalendarEventUid, subCalendarEventRecurrenceId);
            SubCalendarEvent previousSubCalendarEvent = new VEventToSubCalendarEventFunction(srcChildSubCalendar, previousChildSubCalendarTimeZone, this.getEventPermisionChecker(srcChildSubCalendar)).apply(eventComponentToUpdate);
            this.eventPublisher.publish((Object)new SubCalendarEventUpdated(this, AuthenticatedUserThreadLocal.get(), previousSubCalendarEvent, new VEventToSubCalendarEventFunction(dstChildSubCalendar, dstChildSubCalendarTimeZone, this.getEventPermisionChecker(dstChildSubCalendar)).apply(eventComponentToUpdate)));
        }
        catch (Exception exception) {
            LOG.error("Error send email for change event type ", (Throwable)exception);
        }
        PersistedSubCalendar persistedSubCalendar = isDstParentSubscription ? this.getChildSubCalendarOfSubscriptionSubscribingToSubCalendar(dstParentSubCalendar.getId(), dstChildSubCalendar) : dstChildSubCalendar;
        return this.toSubCalendarEvent(this.calendarDataStore.getEvent(dstChildSubCalendar, updatedEvent.getUid(), null), persistedSubCalendar, dstChildSubCalendarTimeZone, this.getEventPermisionChecker(persistedSubCalendar));
    }

    @Override
    public Calendar createEmptyCalendarForSubCalendar(PersistedSubCalendar subCalendar) throws Exception {
        return this.calendarDataStore.createEmptyCalendarForSubCalendar(subCalendar);
    }

    @Override
    public Calendar getSubCalendarContent(PersistedSubCalendar subCalendar) throws Exception {
        return this.calendarDataStore.getSubCalendarContent(subCalendar);
    }

    @Override
    public void setSubCalendarContent(PersistedSubCalendar subCalendar, Calendar subCalendarData) throws Exception {
        this.calendarDataStore.setSubCalendarContent(subCalendar, subCalendarData);
    }

    @Override
    public List<PersistedSubCalendar> flattenSubCalendars(Collection<PersistedSubCalendar> subCalendarsInView) {
        LinkedList<PersistedSubCalendar> childSubCalendars = new LinkedList<PersistedSubCalendar>();
        for (PersistedSubCalendar subCalendar : subCalendarsInView) {
            if (subCalendar instanceof ParentSubCalendarDataStore.ParentSubCalendar) {
                if (subCalendar.getChildSubCalendarIds() == null) continue;
                for (String childSubCalendarID : subCalendar.getChildSubCalendarIds()) {
                    childSubCalendars.add(this.getSubCalendar(childSubCalendarID));
                }
                continue;
            }
            childSubCalendars.add(subCalendar);
        }
        return childSubCalendars;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getSubCalendarsInView(ConfluenceUser user) {
        UtilTimerStack.push((String)"CalendarManager.getSubCalendarsInView()");
        try {
            Set<String> subCalendarIds = this.calendarPreferenceManager.getUserPreference(user).getSubCalendarsInView();
            ImmutableSet immutableSet = ImmutableSet.copyOf(this.filterSubCalendarIds(subCalendarIds.toArray(new String[0])));
            return immutableSet;
        }
        finally {
            UtilTimerStack.pop((String)"CalendarManager.getSubCalendarsInView()");
        }
    }

    @Override
    public Set<String> getSubCalendarsOnSpace(String spaceKey) {
        boolean permitted;
        Space spaceSubCalendar = null;
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            spaceSubCalendar = this.spaceManager.getSpace(spaceKey);
        }
        if (!(permitted = this.spacePermissionManager.hasPermission("USECONFLUENCE", spaceSubCalendar, (User)AuthenticatedUserThreadLocal.get()))) {
            return Collections.emptySet();
        }
        return this.calendarDataStore.getSubCalendarIdsOnSpace(spaceKey);
    }

    @Override
    public long countSubCalendarsOnSpace(String spaceKey) {
        return this.calendarDataStore.getSubCalendarIdsOnSpace(spaceKey).size();
    }

    @Override
    public void deleteSubCalendarOnSpace(PersistedSubCalendar persistedSubCalendar, String spaceKey) {
        this.calendarDataStore.removeSubCalendarFromSpaceView(persistedSubCalendar, spaceKey);
    }

    @Override
    public void deleteSubCalendarInView(ConfluenceUser user) {
        Collection persistedSubCalendars = Collections2.transform(this.getSubCalendarsInView(user), this.calendarDataStore::getSubCalendar);
        for (PersistedSubCalendar persistedSubCalendar : persistedSubCalendars) {
            this.calendarDataStore.remove(persistedSubCalendar);
        }
    }

    @Override
    public boolean isPersonalCalendarEmpty(ConfluenceUser user) {
        return Collections2.filter(this.getSubCalendarsInView(user), subCalendarId -> null != this.getSubCalendarSummary((String)subCalendarId)).isEmpty();
    }

    @Override
    public int getSubCalendarsCount() {
        return this.calendarDataStore.getSubCalendarsCount();
    }

    @Override
    public Set<SubCalendarSummary> findSubCalendars(String term, int startIndex, int pageSize, ConfluenceUser user) throws InvalidSearchException {
        LinkedHashSet searchResults = Sets.newLinkedHashSet((Iterable)Collections2.transform(this.calendarSearcher.findSubCalendars(user, term, startIndex, pageSize), this.calendarDataStore::getSubCalendarSummary));
        return searchResults;
    }

    @Override
    public List<SubCalendarSummary> getAllSubCalendars(ConfluenceUser user) {
        LinkedList<SubCalendarSummary> summariesUserCanView = new LinkedList<SubCalendarSummary>();
        for (String id : this.calendarDataStore.getAllParentSubCalendarIds()) {
            if (!this.calendarPermissionManager.hasViewEventPrivilege(new LightweightPersistentSubCalendar(id), user)) continue;
            summariesUserCanView.add(this.calendarDataStore.getSubCalendarSummary(id));
        }
        Collections.sort(summariesUserCanView);
        return summariesUserCanView;
    }

    @Override
    public List<String> getAllSubCalendarIds(String spaceKey, int start, int limit) {
        return new ArrayList<String>(this.calendarDataStore.getAllParentSubCalendarIds(spaceKey, start, limit));
    }

    @Override
    public Set<String> getAvailableSubCalendarColorCssClasses() {
        return this.subCalendarColorRegistry.getColorClasses();
    }

    @Override
    public String getRandomCalendarColor(String ... colourClassesToExclude) {
        return this.subCalendarColorRegistry.getRandomColourClass(colourClassesToExclude);
    }

    @Override
    public String getSubCalendarColorAsHexValue(String subCalendarColorCssClass) {
        return this.subCalendarColorRegistry.getColorHex(subCalendarColorCssClass);
    }

    @Override
    public void refresh(PersistedSubCalendar subCalendar) {
        if (this.calendarDataStore instanceof RefreshableCalendarDataStore) {
            ((RefreshableCalendarDataStore)this.calendarDataStore).refresh(subCalendar);
        }
    }

    @Override
    public void setUserPreference(ConfluenceUser user, UserCalendarPreference userCalendarPreference) {
        this.calendarPreferenceManager.setUserPreference(user, userCalendarPreference);
    }

    @Override
    public UserCalendarPreference getUserPreference(ConfluenceUser user) {
        return this.calendarPreferenceManager.getUserPreference(user);
    }

    @Override
    public Set<String> updateSubCalendarsInView(ConfluenceUser currentUser, String[] subCalendarIds) {
        Set<String> parentSubCalendarSet;
        UserCalendarPreference userPreferences = this.getUserPreference(currentUser);
        Set<String> finalSet = parentSubCalendarSet = Arrays.stream(subCalendarIds).filter(StringUtils::isNotBlank).map(this::getPersistedSubCalendar).filter(com.google.common.base.Optional::isPresent).map(com.google.common.base.Optional::get).filter(persistedSubCalendar -> StringUtils.isEmpty((CharSequence)persistedSubCalendar.getParentId())).map(PersistedSubCalendar::getId).collect(Collectors.toSet());
        userPreferences.setSubCalendarsInView(finalSet);
        this.setUserPreference(currentUser, userPreferences);
        return finalSet;
    }

    @Override
    public void hideSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        this.calendarPreferenceManager.removeFromView(user, persistedSubCalendar);
    }

    @Override
    public void watchSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        if (persistedSubCalendar instanceof SubscribingSubCalendar) {
            try {
                PersistedSubCalendar subCalendar = this.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId());
                if (subCalendar != null) {
                    this.calendarPreferenceManager.watch(user, subCalendar);
                }
            }
            catch (CalendarException calendarException) {}
        } else {
            this.calendarPreferenceManager.watch(user, persistedSubCalendar);
        }
    }

    @Override
    public void unwatchSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        this.calendarPreferenceManager.unwatch(user, persistedSubCalendar);
        if (persistedSubCalendar instanceof SubscribingSubCalendar) {
            try {
                PersistedSubCalendar subCalendar = this.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId());
                if (subCalendar != null) {
                    this.calendarPreferenceManager.unwatch(user, subCalendar);
                }
            }
            catch (CalendarException calendarException) {
                // empty catch block
            }
        }
    }

    @Override
    public boolean isWatching(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user, boolean includeWatchesOnSubscribingContent) {
        return this.isWatching(user, persistedSubCalendar) || includeWatchesOnSubscribingContent && this.isWatchingViaContent(persistedSubCalendar, user);
    }

    @Override
    public Map<String, Boolean> isWatching(ConfluenceUser user, PersistedSubCalendar ... persistedSubCalendars) {
        HashMap<String, Boolean> results = new HashMap<String, Boolean>();
        for (PersistedSubCalendar persistedSubCalendar : persistedSubCalendars) {
            results.put(persistedSubCalendar.getId(), this.isWatching(user, persistedSubCalendar));
        }
        return results;
    }

    @Override
    public boolean isWatchingViaContent(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        if (user != null) {
            Space space;
            for (ContentEntityObject contentEmbeddingSubCalendar : Collections2.filter(this.subCalendarSubscriptionStatisticsAccessor.getContentEmbeddingSubCalendar(persistedSubCalendar), (com.google.common.base.Predicate)Predicates.instanceOf(AbstractPage.class))) {
                AbstractPage page = (AbstractPage)contentEmbeddingSubCalendar;
                if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page) || this.notificationManager.getNotificationByUserAndSpace((User)user, page.getSpace()) != null || this.notificationManager.getNotificationByUserAndContent((User)user, (ContentEntityObject)page) == null) continue;
                return true;
            }
            String spaceKey = persistedSubCalendar.getSpaceKey();
            if (StringUtils.isNotBlank((CharSequence)spaceKey) && (space = this.spaceManager.getSpace(spaceKey)) != null && this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)space) && this.notificationManager.getNotificationByUserAndSpace((User)user, space) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void hideEventsOfSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        this.calendarPreferenceManager.hideEvents(user, persistedSubCalendar);
    }

    @Override
    public void unhideEventsOfSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        this.calendarPreferenceManager.unhideEvents(user, persistedSubCalendar);
    }

    @Override
    public boolean isEventsOfSubCalendarHidden(PersistedSubCalendar persistedSubCalendar, ConfluenceUser user) {
        return this.calendarPreferenceManager.isEventsHidden(user, persistedSubCalendar);
    }

    @Override
    public Message getTextForSubCalendar(PersistedSubCalendar persistedSubCalendar, Message originalMessage) {
        return this.calendarDataStore.getTypeSpecificText(persistedSubCalendar, originalMessage);
    }

    private void checkCustomEventTypeExistsForSubCalendar(SubCalendarEvent subCalendarEvent, PersistedSubCalendar srcParentSubCalendar) {
        if (StringUtils.isNotBlank((CharSequence)subCalendarEvent.getCustomEventTypeId())) {
            boolean customEventTypeExists = false;
            Set<CustomEventType> customEventTypes = srcParentSubCalendar.getCustomEventTypes();
            if (customEventTypes != null && customEventTypes.size() > 0) {
                for (CustomEventType customEventType : customEventTypes) {
                    if (!StringUtils.equals((CharSequence)customEventType.getCustomEventTypeId(), (CharSequence)subCalendarEvent.getCustomEventTypeId())) continue;
                    customEventTypeExists = true;
                    break;
                }
            }
            if (!customEventTypeExists) {
                throw new CalendarException("calendar.customeventtype.doesnotexist", subCalendarEvent.getCustomEventTypeId());
            }
        }
    }

    @Override
    public List<ReminderEvent> getEventUpComingReminder() {
        return this.getEventUpComingReminder(System.currentTimeMillis());
    }

    @Override
    public List<ReminderEvent> getEventUpComingReminder(long timezoneSpecificMilliseconds) {
        DateTime systemUTCTime = new DateTime(timezoneSpecificMilliseconds, DateTimeZone.UTC);
        long startSystemUTC = new DateTime(systemUTCTime.getYear(), systemUTCTime.getMonthOfYear(), systemUTCTime.getDayOfMonth(), systemUTCTime.getHourOfDay(), systemUTCTime.getMinuteOfHour(), DateTimeZone.UTC).getMillis();
        long schedulerTime = 300000L;
        ArrayList<ReminderEvent> reminderEventList = new ArrayList<ReminderEvent>();
        reminderEventList.addAll(this.calendarDataStore.getSingleEventUpComingReminder(startSystemUTC, schedulerTime));
        List<VEvent> repeatEventComponents = this.calendarDataStore.getRepeatEventUpComingReminder();
        reminderEventList.addAll(this.recurrenceRuleProcessor.getRecurrenceEventsForReminder(this.jodaIcal4jTimeZoneMapper, this.jodaIcal4jDateTimeConverter, repeatEventComponents, startSystemUTC, schedulerTime));
        reminderEventList.addAll(this.calendarDataStore.getJiraEventUpComingReminder(startSystemUTC, schedulerTime));
        return reminderEventList;
    }

    @Override
    public Option<Map<ConfluenceUser, Collection<ReminderEvent>>> getReminderListFor(Collection<ReminderEvent> reminderEvents) {
        String[] subCalendarIds = Collections2.transform(reminderEvents, reminderEvent -> reminderEvent == null ? "" : reminderEvent.getSubCalendarId()).toArray(new String[0]);
        return this.calendarDataStore.getReminderListFor((Function<Map<String, Collection<String>>, PersistedSubCalendar>)((Function)userReminderMap -> {
            HashMap reminderEventGroupByUser = Maps.newHashMap();
            if (userReminderMap != null && userReminderMap.size() > 0) {
                for (Map.Entry entry : userReminderMap.entrySet()) {
                    String userKey = (String)entry.getKey();
                    Collection reminderSubCalendarIDList = (Collection)entry.getValue();
                    ConfluenceUser reminder = this.userAccessor.getUserByKey(new UserKey(userKey));
                    boolean isUserDeactivate = this.userAccessor.isDeactivated((User)reminder);
                    if (isUserDeactivate) {
                        LOG.warn("Could not find user instance for user key ?", (Object)userKey);
                        continue;
                    }
                    Collection filteredReminderEvents = this.getTransformSubCalendarIdToReminderEvent(userKey, reminderSubCalendarIDList, reminderEvents);
                    filteredReminderEvents = Collections2.transform(filteredReminderEvents, input -> (ReminderEvent)input.clone());
                    ArrayList sortedFilteredReminderEvents = Lists.newArrayList((Iterable)filteredReminderEvents);
                    Collections.sort(sortedFilteredReminderEvents, (reminderEvent1, reminderEvent2) -> reminderEvent1.getPeriod() <= reminderEvent2.getPeriod() ? -1 : 1);
                    reminderEventGroupByUser.put(reminder, sortedFilteredReminderEvents);
                }
            }
            return reminderEventGroupByUser;
        }), subCalendarIds);
    }

    @Override
    public List<ReminderEvent> getInviteesFor(List<ReminderEvent> reminderEvents) {
        Integer[] eventIds = Collections2.transform(reminderEvents, reminderEvent -> reminderEvent == null ? 0 : reminderEvent.getEventId()).toArray(new Integer[0]);
        Map<Integer, Collection<String>> invitess = this.calendarDataStore.getInviteesFor(eventIds);
        if (invitess != null && invitess.size() > 0) {
            this.getTransformInviteeToReminderEvent(invitess, reminderEvents);
        }
        return reminderEvents;
    }

    private Collection<ReminderEvent> getTransformSubCalendarIdToReminderEvent(String userKey, Collection<String> subCalendarIds, Collection<ReminderEvent> reminderEvents) {
        ArrayList subCalendarList = Lists.newArrayList(subCalendarIds);
        return Collections2.filter(reminderEvents, input -> {
            if (input == null) {
                return false;
            }
            boolean subCalendarIdMatch = subCalendarList.contains(input.getSubCalendarId());
            if (!CalendarUtil.isJiraStoreKey(input.getStoreKey())) {
                return subCalendarIdMatch;
            }
            return subCalendarIdMatch && input.getUserKey().equals(userKey);
        });
    }

    private void getTransformInviteeToReminderEvent(Map<Integer, Collection<String>> invitess, Collection<ReminderEvent> reminderEvents) {
        for (ReminderEvent reminderEvent : reminderEvents) {
            if (!invitess.containsKey(reminderEvent.getEventId())) continue;
            reminderEvent.setInviteeIds(Lists.newArrayList((Iterable)invitess.get(reminderEvent.getEventId())));
        }
    }

    @Override
    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser user, String ... childSubCalendars) {
        if (user != null) {
            return this.calendarDataStore.getChildSubCalendarHasReminders(user, childSubCalendars);
        }
        return new HashSet<String>();
    }

    @Override
    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser user) {
        if (user != null) {
            return this.calendarDataStore.getAllSubCalendarIdHasReminders(user);
        }
        return new HashSet<String>();
    }

    @Override
    public Option<PersistedSubCalendar> getChildSubCalendarByStoreKey(PersistedSubCalendar parentSubCalendar, String storeKey) {
        Preconditions.checkNotNull((Object)parentSubCalendar);
        Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)storeKey) ? 1 : 0) != 0);
        return this.calendarDataStore.getChildSubCalendarByStoreKey(parentSubCalendar, storeKey);
    }

    @Override
    public Option<PersistedSubCalendar> getChildSubCalendarByCustomEventTypeId(PersistedSubCalendar parentSubCalendar, String customEventTypeId) {
        Preconditions.checkNotNull((Object)parentSubCalendar);
        Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)customEventTypeId) ? 1 : 0) != 0);
        return this.calendarDataStore.getChildSubCalendarByCustomEventTypeId(parentSubCalendar, customEventTypeId);
    }

    @Override
    public Set<String> filterExistSubCalendarIds(String ... subCalendarIds) {
        if (subCalendarIds.length < 1) {
            return new HashSet<String>();
        }
        return this.calendarDataStore.filterExistSubCalendarIds(subCalendarIds);
    }

    @Override
    public Message getSubCalendarEventWarning(PersistedSubCalendar subCalendar, long start, long end) throws Exception {
        subCalendar.setStart(start);
        subCalendar.setEnd(end);
        return this.calendarDataStore.getSubCalendarEventWarning(subCalendar);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onSubCalendarTrackChanged(SubCalendarTrackChangeEvent subCalendarTrackChangeEvent) {
        Object subCalendar = subCalendarTrackChangeEvent.getSubCalendar();
        this.subCalendarUpdateTracker.trackChange((PersistedSubCalendar)subCalendar);
    }

    @EventListener
    public void onEventUpdated(SubCalendarEventUpdated subCalendarEventUpdated) {
        PersistedSubCalendar subCalendar = subCalendarEventUpdated.getSubCalendar();
        this.subCalendarUpdateTracker.trackChange(subCalendar);
    }

    @EventListener
    public void onEventAdded(SubCalendarEventCreated subCalendarEventCreated) {
        PersistedSubCalendar subCalendar = subCalendarEventCreated.getSubCalendar();
        this.subCalendarUpdateTracker.trackChange(subCalendar);
    }

    @EventListener
    public void onEventRemoved(SubCalendarEventRemoved subCalendarEventRemoved) {
        PersistedSubCalendar subCalendar = subCalendarEventRemoved.getSubCalendar();
        this.subCalendarUpdateTracker.trackChange(subCalendar);
    }

    @EventListener
    public void onSpaceRemoveEvent(SpaceRemoveEvent spaceRemoveEvent) {
        String spaceKey = spaceRemoveEvent.getSpace().getKey();
        Set<String> subCalendarIds = this.calendarDataStore.getSubCalendarIdsOnSpace(spaceKey);
        for (String subCalendarId : subCalendarIds) {
            PersistedSubCalendar subCalendar = this.calendarDataStore.getSubCalendar(subCalendarId);
            if (subCalendar == null || !spaceKey.equals(subCalendar.getSpaceKey())) continue;
            this.removeSubCalendar(subCalendar);
        }
    }

    @EventListener
    public void onUserRemoved(UserRemoveEvent event) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser((User)event.getUser());
        if (confluenceUser != null) {
            this.calendarDataStore.removeSubCalendarRestrictions(confluenceUser.getKey().getStringValue());
            this.calendarUserPreferenceStore.clearUserPreferenceCache(confluenceUser);
            Map<String, Set<String>> userEventsBySubCalendar = this.calendarDataStore.getVEventUidsForUserBySubCalendar(confluenceUser);
            this.calendarDataStore.deleteInviteeFromAllEvents(confluenceUser.getKey().getStringValue());
            this.updateExistingEvents(userEventsBySubCalendar);
        }
    }

    private void updateExistingEvents(Map<String, Set<String>> eventsBySubCalendar) {
        LOG.info("Updating events with old invitees ...");
        eventsBySubCalendar.keySet().stream().forEach(subCalendarId -> {
            PersistedSubCalendar persistedSubCalendar = (PersistedSubCalendar)this.getPersistedSubCalendar((String)subCalendarId).orNull();
            if (persistedSubCalendar != null) {
                try {
                    this.updateOrRemoveInvalidExistingEvents(persistedSubCalendar, (Collection)eventsBySubCalendar.get(subCalendarId));
                }
                catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Failed to update subCalendar with id [" + persistedSubCalendar.getName() + "]", (Throwable)e);
                    }
                    LOG.error("Failed to update subCalendar with id [{}]: {}. Turn on debug logging to see stacktrace", (Object)persistedSubCalendar.getName(), (Object)e.getMessage());
                }
            }
        });
    }

    @Override
    public void addCalendarsToSpaceView(Set<String> embeddedCalendarIds, String spaceKey) {
        this.calendarDataStore.addCalendarsToSpaceView(embeddedCalendarIds, spaceKey);
    }

    private boolean isWatching(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        boolean watching = this.calendarPreferenceManager.isWatching(user, persistedSubCalendar);
        if (!watching && persistedSubCalendar instanceof SubscribingSubCalendar) {
            watching = this.calendarPreferenceManager.isWatching(user, this.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId()));
        }
        return watching;
    }

    @Override
    public List<String> getEventInviteeUserIds(String eventUid) {
        return this.calendarDataStore.getInviteesFor(eventUid);
    }

    private class VEventToSubCalendarEventFunction
    implements Function<VEvent, SubCalendarEvent> {
        private final PersistedSubCalendar subCalendar;
        private final TimeZone subCalendarTimeZone;
        private final Function<Void, Boolean> eventPermissionChecker;

        private VEventToSubCalendarEventFunction(PersistedSubCalendar subCalendar, TimeZone subCalendarTimeZone, Function<Void, Boolean> eventPermissionChecker) {
            this.subCalendar = subCalendar;
            this.subCalendarTimeZone = subCalendarTimeZone;
            this.eventPermissionChecker = eventPermissionChecker;
        }

        public SubCalendarEvent apply(VEvent vEvent) {
            SubCalendarEvent anEvent = DefaultCalendarManager.this.toSubCalendarEvent(vEvent, this.subCalendar, this.subCalendarTimeZone, this.eventPermissionChecker);
            anEvent.setRepeat(DefaultCalendarManager.this.getRepeatFromEventComponent(vEvent));
            return anEvent;
        }
    }

    public static class BaseUrlHostInfo
    implements HostInfo {
        private static final String LOOPBACK_HOST_NAME = "127.0.0.1";
        private final SettingsManager settingsManager;

        public BaseUrlHostInfo(SettingsManager settingsManager) {
            this.settingsManager = settingsManager;
        }

        @Override
        public String getHostName() {
            try {
                return new URL(this.settingsManager.getGlobalSettings().getBaseUrl()).getHost();
            }
            catch (MalformedURLException e) {
                LOG.error("Unable to get host name from base URL", (Throwable)e);
                return LOOPBACK_HOST_NAME;
            }
        }
    }
}

