/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.SubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.resources.MessageToStringTransformerFunction;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.message.Message;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSubCalendarResource
extends AbstractResource {
    protected final SettingsManager settingsManager;
    protected final CacheFactory cacheFactory;

    protected AbstractSubCalendarResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor, SettingsManager settingsManager, CacheFactory cacheFactory) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.settingsManager = settingsManager;
        this.cacheFactory = cacheFactory;
    }

    protected Collection<SubCalendarsResponseEntity.ExtendedSubCalendar> getSubcalendarsInternal(String calendarContext, String spaceKey, List<String> subCalendarIdIncludes) {
        LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = new LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar>();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        HashSet<String> subCalendarIds = new HashSet<String>(subCalendarIdIncludes == null || subCalendarIdIncludes.isEmpty() ? (CalendarRenderer.CalendarContext.spaceCalendars.getValue().equals(calendarContext) ? this.calendarManager.getSubCalendarsOnSpace(spaceKey) : this.calendarManager.getSubCalendarsInView(currentUser)) : subCalendarIdIncludes);
        if (subCalendarIds.size() > 0) {
            List persistedSubCalendars = this.calendarManager.getSubCalendarsWithRestriction(subCalendarIds.toArray(new String[0])).stream().filter(calendar -> this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)calendar, currentUser)).map(calendar -> {
                if ((calendar instanceof SubscriptionCalendarDataStore.UrlSubscriptionCalendar || calendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar) && !this.calendarPermissionManager.hasAdminSubCalendarPrivilege((PersistedSubCalendar)calendar, currentUser)) {
                    calendar.setSourceLocation(null);
                    calendar.setUserName(null);
                }
                return calendar;
            }).collect(Collectors.toList());
            HashSet childSubCalendarIds = Sets.newHashSet();
            for (PersistedSubCalendar subCalendar : persistedSubCalendars) {
                try {
                    SubCalendarsResponseEntity.ExtendedSubCalendar subCalendarEntity = this.toExtendedSubCalendar(subCalendar, currentUser);
                    subCalendars.add(subCalendarEntity);
                    childSubCalendarIds.addAll(subCalendar.getChildSubCalendarIds());
                }
                catch (RuntimeException unexpectedError) {
                    LOG.warn(String.format("Unable to retrieve information of sub-calendar with ID %s for user %s. Please ignore if the mycalendars page is appearing alright for the user.", subCalendar.getId(), currentUser != null ? currentUser.getKey().toString() : null), (Throwable)unexpectedError);
                }
            }
            List childSubCalendarList = this.calendarManager.getSubCalendarsWithRestriction(childSubCalendarIds.toArray(new String[0])).stream().map(persistedSubCalendar -> this.toExtendedSubCalendar((PersistedSubCalendar)persistedSubCalendar, currentUser)).collect(Collectors.toList());
            Set<String> allSubCalendarIdHasReminders = this.calendarManager.getAllSubCalendarIdHasReminders(currentUser);
            for (SubCalendarsResponseEntity.ExtendedSubCalendar parentSubCalendar : subCalendars) {
                String parentId = parentSubCalendar.getSubCalendar().getId();
                String calendarName = parentSubCalendar.getSubCalendar().getName();
                parentSubCalendar.setChildSubCalendars(childSubCalendarList.stream().filter(childSubCalendar -> {
                    String refParentId = childSubCalendar.getSubCalendar().getParentId();
                    return parentId.equals(refParentId);
                }).peek(childSubCalendar -> {
                    if (childSubCalendar.getSubCalendar() != null && allSubCalendarIdHasReminders.isEmpty()) {
                        if (childSubCalendar.getSubCalendar() instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar) {
                            PersistedSubCalendar persistedSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)childSubCalendar.getSubCalendar()).getSourceSubCalendar();
                            if (persistedSubCalendar != null && allSubCalendarIdHasReminders.contains(persistedSubCalendar.getId())) {
                                childSubCalendar.setReminderMe(true);
                            }
                        } else if (allSubCalendarIdHasReminders.contains(childSubCalendar.getSubCalendar().getId())) {
                            childSubCalendar.setReminderMe(true);
                        }
                    }
                }).collect(Collectors.toList()));
            }
        }
        return subCalendars;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SubCalendarsResponseEntity.ExtendedSubCalendar toExtendedSubCalendar(PersistedSubCalendar subCalendar, ConfluenceUser forUser) {
        UtilTimerStack.push((String)"CalendarResource.toExtendedSubCalendar()");
        try {
            boolean isEventsOfSubCalendarHidden = this.calendarManager.isEventsOfSubCalendarHidden(subCalendar, forUser);
            SubCalendarsResponseEntity.ExtendedSubCalendar extendedSubCalendar = new SubCalendarsResponseEntity.ExtendedSubCalendar(subCalendar, this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, forUser), this.calendarPermissionManager.hasReloadEventsPrivilege(subCalendar, forUser), this.calendarPermissionManager.hasEditSubCalendarPrivilege(forUser), this.calendarPermissionManager.hasEditEventPrivilege(subCalendar, forUser), false, false, false, isEventsOfSubCalendarHidden, this.calendarPermissionManager.hasDeleteSubCalendarPrivilege(subCalendar, forUser), this.calendarPermissionManager.hasAdminSubCalendarPrivilege(subCalendar, forUser), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventViewUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventViewGroupRestrictions(subCalendar), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventEditUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventEditGroupRestrictions(subCalendar), !isEventsOfSubCalendarHidden ? this.getSubCalendarWarnings(subCalendar) : Collections.emptySet(), this.calendarManager.hasReminderFor(subCalendar, forUser));
            return extendedSubCalendar;
        }
        finally {
            UtilTimerStack.pop((String)"CalendarResource.toExtendedSubCalendar()");
        }
    }

    private Set<String> getSubCalendarWarnings(PersistedSubCalendar subCalendar) {
        Set<Object> warnings = new HashSet();
        try {
            warnings = this.calendarManager.getSubCalendarWarnings(subCalendar);
        }
        catch (Exception exception) {
            LOG.error("Could not get warning for {}, it will be skip", (Object)subCalendar.getId());
        }
        Objects.nonNull(subCalendar);
        HashSet<String> warningMessages = new HashSet<String>();
        try {
            warningMessages.addAll(Collections2.transform((Collection)Collections2.transform(warnings, warningMessage -> this.calendarManager.getTextForSubCalendar(subCalendar, (Message)warningMessage)), (Function)new MessageToStringTransformerFunction(this.getI18nBean())));
        }
        catch (Exception ex) {
            LOG.error("Could not get warning message for subcalendar {}", (Object)subCalendar.getId());
        }
        return warningMessages;
    }
}

