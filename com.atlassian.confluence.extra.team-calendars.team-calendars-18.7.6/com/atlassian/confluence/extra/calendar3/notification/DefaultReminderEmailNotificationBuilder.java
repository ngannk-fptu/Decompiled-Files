/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Option
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.WordUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.ReminderPeriods;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEmailNotification;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventPeriodGroup;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventSubCalendarGroup;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.notification.ReminderEmailNotificationBuilder;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.EmailNotificationHelper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Option;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultReminderEmailNotificationBuilder
implements ReminderEmailNotificationBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultReminderEmailNotificationBuilder.class);
    private static final int TITLE_REMINDER_LENGTH = 100;
    private static final int MAX_EVENT_DESCRIPTION_LENGTH = 220;
    private final UserAccessor userAccessor;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final FormatSettingsManager formatSettingsManager;
    private final CalendarManager calendarManager;
    private final SettingsManager settingsManager;
    private final SubCalendarEventTransformerFactory subCalendarEventTransformerFactory;

    @Autowired
    public DefaultReminderEmailNotificationBuilder(@ComponentImport UserAccessor userAccessor, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, @ComponentImport FormatSettingsManager formatSettingsManager, CalendarManager calendarManager, @ComponentImport SettingsManager settingsManager, SubCalendarEventTransformerFactory subCalendarEventTransformerFactory) {
        this.userAccessor = userAccessor;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.formatSettingsManager = formatSettingsManager;
        this.calendarManager = calendarManager;
        this.settingsManager = settingsManager;
        this.subCalendarEventTransformerFactory = subCalendarEventTransformerFactory;
    }

    @Override
    public ReminderEmailNotification build(Email notification, ConfluenceUser toUser, Collection<ReminderEvent> rawReminderEvents) {
        ReminderEmailNotification reminderEmailNotificationModel = new ReminderEmailNotification();
        if (null == rawReminderEvents || rawReminderEvents.size() == 0) {
            return reminderEmailNotificationModel;
        }
        String reminderNote = this.composeReminderNote(toUser, rawReminderEvents);
        reminderEmailNotificationModel.setReminderNote(reminderNote);
        DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(toUser));
        Locale userLocale = this.localeManager.getLocale((User)toUser);
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern((String)this.formatSettingsManager.getTimeFormat()).withLocale(userLocale);
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern((String)this.formatSettingsManager.getDateFormat()).withLocale(userLocale);
        Collection customEventTypeIds = Collections2.filter((Collection)Collections2.transform(rawReminderEvents, ReminderEvent::getCustomEventTypeId), StringUtils::isNotEmpty);
        Collection<CustomEventType> customEventTypes = this.calendarManager.getCustomEventTypes(customEventTypeIds.toArray(new String[0]));
        Iterator<ReminderEvent> iterator = rawReminderEvents.iterator();
        ReminderEvent firstElement = iterator.next();
        this.composeReminderEventModel(notification, toUser, userLocale, userTimeZone, timeFormatter, dateFormatter, customEventTypes, firstElement);
        ReminderEventPeriodGroup currentPeriodGroup = reminderEmailNotificationModel.addReminderEventPeriodGroup(firstElement);
        ReminderEventSubCalendarGroup currentSubCalendarGroup = currentPeriodGroup.addReminderEventSubCalendarGroup(firstElement);
        currentSubCalendarGroup.addReminderEvent(firstElement);
        while (iterator.hasNext()) {
            ReminderEvent rawReminderEvent = iterator.next();
            this.composeReminderEventModel(notification, toUser, userLocale, userTimeZone, timeFormatter, dateFormatter, customEventTypes, rawReminderEvent);
            if (currentPeriodGroup.isSameGroupKey(rawReminderEvent.getPeriod(), rawReminderEvent.getSubCalendarId())) {
                if (!currentSubCalendarGroup.getSubcalendarId().equals(rawReminderEvent.getSubCalendarId())) {
                    currentSubCalendarGroup = currentPeriodGroup.addReminderEventSubCalendarGroup(rawReminderEvent);
                }
                currentSubCalendarGroup.addReminderEvent(rawReminderEvent);
                continue;
            }
            currentPeriodGroup = reminderEmailNotificationModel.addReminderEventPeriodGroup(rawReminderEvent);
            currentSubCalendarGroup = currentPeriodGroup.addReminderEventSubCalendarGroup(rawReminderEvent);
            currentSubCalendarGroup.addReminderEvent(rawReminderEvent);
        }
        try {
            notification.getMultipart().addBodyPart((BodyPart)EmailNotificationHelper.addImageBodyPart("com/atlassian/confluence/extra/calendar3/img/events_32.png", "team-cal-icon.png", "<events>"));
        }
        catch (MailException mailEx) {
            LOG.error("Failed to get icon resource", (Throwable)mailEx);
        }
        catch (MessagingException messageEx) {
            LOG.error("Failed to get icon resource", (Throwable)messageEx);
        }
        catch (IOException ioEx) {
            LOG.error("Failed to get icon resource", (Throwable)ioEx);
        }
        return reminderEmailNotificationModel;
    }

    private ConfluenceUser getUserById(String userId) {
        return this.userAccessor.getUserByKey(new UserKey(userId));
    }

    private List<String> getInviteeFirstNames(ConfluenceUser forUser, Set<Invitee> invitees) {
        return invitees.stream().map(invitee -> WordUtils.capitalize((String)this.getDisplayName(forUser, StringUtils.trim((String)invitee.getDisplayName())))).map(displayName -> {
            int indexOfWhiteSpace = StringUtils.trim((String)displayName).indexOf(32);
            return indexOfWhiteSpace > 0 ? displayName.substring(0, indexOfWhiteSpace) : displayName;
        }).collect(Collectors.toList());
    }

    private String getDisplayName(ConfluenceUser forUser, String displayName) {
        return (String)StringUtils.defaultIfEmpty((CharSequence)displayName, (CharSequence)this.getText(forUser, "calendar3.error.unknownuser", new Object[0]));
    }

    private String getText(ConfluenceUser user, String i18nKey, Object ... substitutions) {
        return this.getI18NBean(user).getText(i18nKey, substitutions);
    }

    private I18NBean getI18NBean(ConfluenceUser user) {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)(user == null ? AuthenticatedUserThreadLocal.get() : user)));
    }

    private String convertMinToTextPeriod(ConfluenceUser toUser, int min) {
        if (min < 60) {
            return this.getText(toUser, "calendar3.reminder.period.minutes", min);
        }
        int periodInHours = min / 60;
        if (periodInHours > 0 && periodInHours < 24) {
            return this.getText(toUser, periodInHours == 1 ? "calendar3.reminder.period.one.hour" : "calendar3.reminder.period.hours", periodInHours);
        }
        int periodIndays = periodInHours / 24;
        if (periodIndays < 7) {
            return this.getText(toUser, periodIndays == 1 ? "calendar3.reminder.period.one.day" : "calendar3.reminder.period.days", periodIndays);
        }
        int periodInWeeks = periodIndays / 7;
        return this.getText(toUser, periodInWeeks == 1 ? "calendar3.reminder.period.one.week" : "calendar3.reminder.period.weeks", periodInWeeks);
    }

    private String getToggleCalendarRemindingUrl(ReminderEvent reminderEvent) {
        return String.format("%s/calendar/togglecalendarreminding.action?subCalendarId=%s&childSubCalendarId=%s", this.settingsManager.getGlobalSettings().getBaseUrl(), GeneralUtil.urlEncode((String)(StringUtils.isNotEmpty((CharSequence)reminderEvent.getSubscriptionId()) ? reminderEvent.getSubscriptionId() : reminderEvent.getParentCalendarId())), GeneralUtil.urlEncode((String)reminderEvent.getSubCalendarId()));
    }

    private String convertEventTypePropertyToName(ReminderEvent reminderEvent, ConfluenceUser toUser) {
        if (StringUtils.isEmpty((CharSequence)reminderEvent.getCustomEventTypeId())) {
            return this.getText(toUser, reminderEvent.getEventTypeName(), new Object[0]);
        }
        return WordUtils.capitalize((String)reminderEvent.getEventTypeName());
    }

    private String getTextEventTypeNameWithCalendarName(ConfluenceUser toUser, ReminderEvent reminderEvent) {
        return this.getText(toUser, "calendar3.notification.reminder.event.type.name.with.calendar.name", StringUtils.isEmpty((CharSequence)reminderEvent.getCustomEventTypeId()) ? this.getText(toUser, reminderEvent.getEventTypeName(), new Object[0]) : WordUtils.capitalize((String)reminderEvent.getEventTypeName()), WordUtils.capitalize((String)reminderEvent.getParentCalendarName()));
    }

    private String getTextStopRemind(ConfluenceUser toUser, ReminderEvent reminderEvent) {
        return this.getText(toUser, "calendar3.notification.reminder.stop", this.getToggleCalendarRemindingUrl(reminderEvent));
    }

    private String getSubCalendarPreviewUrl(ReminderEvent reminderEvent) {
        return String.format("%s/calendar/previewcalendar.action?subCalendarId=%s", this.settingsManager.getGlobalSettings().getBaseUrl(), GeneralUtil.urlEncode((String)(StringUtils.isNotEmpty((CharSequence)reminderEvent.getSubscriptionId()) ? reminderEvent.getSubscriptionId() : reminderEvent.getParentCalendarId())));
    }

    private void composeReminderEventModel(Email notification, ConfluenceUser toUser, Locale userLocale, DateTimeZone userTimeZone, DateTimeFormatter timeFormatter, DateTimeFormatter dateFormatter, final Collection<CustomEventType> customEventTypes, ReminderEvent reminderEvent) {
        HashSet invitees = Sets.newHashSet((Iterable)Collections2.transform(reminderEvent.getInviteeIds() == null ? Collections.emptyList() : reminderEvent.getInviteeIds(), confluenceUserId -> new ConfluenceUserInvitee(this.getUserById((String)confluenceUserId))));
        StringBuilder stringBuilderInvitees = new StringBuilder();
        List<String> inviteeFirstNames = this.getInviteeFirstNames(toUser, invitees);
        int inviteeFirstNameSize = inviteeFirstNames.size();
        if (inviteeFirstNameSize > 0) {
            if (inviteeFirstNameSize == 1) {
                stringBuilderInvitees.append(inviteeFirstNames.get(0));
            } else {
                stringBuilderInvitees.append(StringUtils.join(inviteeFirstNames.subList(0, inviteeFirstNameSize - 1), (String)", ")).append(" & ").append(inviteeFirstNames.get(inviteeFirstNameSize - 1));
            }
        }
        DateTime currentTime = new DateTime(userTimeZone);
        DateTime startDateTime = new DateTime(reminderEvent.getUtcStart(), userTimeZone);
        DateTime endDateTime = new DateTime(reminderEvent.getUtcEnd(), userTimeZone);
        String startDateDayOfWeekText = startDateTime.dayOfWeek().getAsText(userLocale);
        if (startDateDayOfWeekText.equals(currentTime.dayOfWeek().getAsText(userLocale))) {
            startDateDayOfWeekText = this.getText(toUser, "calendar3.button.today", new Object[0]);
        }
        reminderEvent.setEventStartDateDayOfWeekForDisplay(startDateDayOfWeekText);
        reminderEvent.setEventStartTimeForDisplay(timeFormatter.print((ReadableInstant)startDateTime));
        reminderEvent.setEventStartDatePartForDisplay(String.valueOf(startDateTime.getDayOfMonth()));
        reminderEvent.setEventStartMonthPartForDisplay(startDateTime.monthOfYear().getAsShortText(userLocale));
        reminderEvent.setEventEndTimeForDisplay(timeFormatter.print((ReadableInstant)endDateTime));
        reminderEvent.setEventEndDatePartForDisplay(String.valueOf(endDateTime.getDayOfMonth()));
        reminderEvent.setEventEndMonthPartForDisplay(endDateTime.monthOfYear().getAsShortText(userLocale));
        Period diff = new Period((ReadableInstant)startDateTime, (ReadableInstant)endDateTime);
        reminderEvent.setOnlyDisplayTime(!reminderEvent.isAllDay() && diff.getDays() == 0);
        reminderEvent.setEventDurationInDays(diff.getDays());
        reminderEvent.setStartDateEqualToTodayDate(currentTime.getDayOfMonth() == startDateTime.getDayOfMonth());
        reminderEvent.setEventStartDateLongFormatForDisplay(dateFormatter.print((ReadableInstant)startDateTime));
        int periodMins = ReminderPeriods.toReminderPeriod(reminderEvent.getPeriod()).equals((Object)Option.none()) ? 0 : ((ReminderPeriods)((Object)ReminderPeriods.toReminderPeriod(reminderEvent.getPeriod()).get())).getMins();
        reminderEvent.setReminderPeriodForDisplay(this.convertMinToTextPeriod(toUser, periodMins));
        reminderEvent.setEventTypeNameWithCalendarName(this.getTextEventTypeNameWithCalendarName(toUser, reminderEvent));
        reminderEvent.setStopRemindHtml(this.getTextStopRemind(toUser, reminderEvent));
        if ("com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore".equals(reminderEvent.getStoreKey()) || "com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore".equals(reminderEvent.getStoreKey()) || "com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore".equals(reminderEvent.getStoreKey())) {
            String upcomingEventTitle = this.getText(toUser, "calendar3.notification.reminder.event.travelleavebirthday.upcoming.title", inviteeFirstNameSize > 0 ? stringBuilderInvitees.toString() : this.getText(toUser, "calendar3.error.unknownuser", new Object[0]), WordUtils.uncapitalize((String)this.convertEventTypePropertyToName(reminderEvent, toUser)));
            reminderEvent.setUpComingEventTittle(upcomingEventTitle);
        } else {
            reminderEvent.setUpComingEventTittle(WordUtils.capitalize((String)StringUtils.abbreviate((String)reminderEvent.getTitle(), (int)100), (char[])new char[1]));
            reminderEvent.setInviteesName(stringBuilderInvitees.toString());
        }
        reminderEvent.setSubcalendarPreviewUrl(this.getSubCalendarPreviewUrl(reminderEvent));
        reminderEvent.setCalendarName(WordUtils.capitalize((String)reminderEvent.getCalendarName()));
        reminderEvent.setEventTypeName(this.convertEventTypePropertyToName(reminderEvent, toUser));
        String i18nKeyForViewJiraIssueText = CalendarUtil.getI18nKeyForViewJiraIssueTextFromEventStoreKey(reminderEvent.getStoreKey());
        reminderEvent.setViewJiraIssueText(this.getText(toUser, i18nKeyForViewJiraIssueText, new Object[0]));
        if (StringUtils.isNotEmpty((CharSequence)reminderEvent.getDescription())) {
            reminderEvent.setDescription(StringUtils.abbreviate((String)reminderEvent.getDescription(), (int)220));
        }
        reminderEvent.setJiraIssueStoreKey(CalendarUtil.isJiraStoreKey(reminderEvent.getStoreKey()));
        this.subCalendarEventTransformerFactory.getReminderTransformer().transform(reminderEvent.toLightWeightSubCalendarEvent(invitees), toUser, new SubCalendarEventTransformerFactory.ReminderTransformParameters(){

            @Override
            public SubCalendarEventTransformerFactory getSubCalendarEventTransformerFactory() {
                return DefaultReminderEmailNotificationBuilder.this.subCalendarEventTransformerFactory;
            }

            @Override
            public Collection<CustomEventType> getAvailableCustomEventTypes() {
                return customEventTypes == null ? new ArrayList() : customEventTypes;
            }

            @Override
            public VEvent getRawEvent() {
                return null;
            }

            @Override
            public boolean isReadOnly() {
                return false;
            }
        });
        try {
            Integer eventId = reminderEvent.getEventId();
            String fileName = eventId + "-icon.png";
            String contentId = "<" + eventId + "-icon>";
            if (reminderEvent.getDataHandler() != null) {
                notification.getMultipart().addBodyPart((BodyPart)EmailNotificationHelper.addImageBodyPart(reminderEvent.getDataHandler(), fileName, contentId));
            } else if (StringUtils.isNotEmpty((CharSequence)reminderEvent.getIconUrl())) {
                notification.getMultipart().addBodyPart((BodyPart)EmailNotificationHelper.addImageBodyPart(reminderEvent.getIconUrl(), fileName, contentId));
            }
        }
        catch (MailException mailEx) {
            LOG.warn("Failed to get icon resource for event {}", (Object)reminderEvent.getEventId(), (Object)mailEx);
        }
        catch (MessagingException messageEx) {
            LOG.warn("Failed to get icon resource for event {}", (Object)reminderEvent.getEventId(), (Object)messageEx);
        }
        catch (IOException ioEx) {
            LOG.warn("Failed to get icon resource for event {}", (Object)reminderEvent.getEventId(), (Object)ioEx);
        }
    }

    private String composeReminderNote(ConfluenceUser userToNotify, Collection<ReminderEvent> reminderEventList) {
        String linkToMyCalendarView = String.format("%s/calendar/mycalendar.action", this.settingsManager.getGlobalSettings().getBaseUrl());
        if (reminderEventList.size() != 1) {
            return this.getDefaultReminderNote(userToNotify, linkToMyCalendarView);
        }
        ReminderEvent event = reminderEventList.iterator().next();
        ReminderSettingEntity reminderSettingEntity = this.calendarManager.getReminderSetting(event.getParentCalendarId(), event.getStoreKey(), event.getCustomEventTypeId());
        if (reminderSettingEntity == null) {
            return this.getDefaultReminderNote(userToNotify, linkToMyCalendarView);
        }
        ConfluenceUser lastModifier = this.userAccessor.getUserByKey(new UserKey(reminderSettingEntity.getLastModifier()));
        if (lastModifier == null) {
            return this.getDefaultReminderNote(userToNotify, linkToMyCalendarView);
        }
        return this.getText(userToNotify, "calendar3.notification.reminder.note.with.name", WordUtils.capitalize((String)lastModifier.getFullName()), linkToMyCalendarView);
    }

    private String getDefaultReminderNote(ConfluenceUser userToNotify, String linkToMyCalendarView) {
        return this.getText(userToNotify, "calendar3.notification.reminder.note", linkToMyCalendarView);
    }
}

