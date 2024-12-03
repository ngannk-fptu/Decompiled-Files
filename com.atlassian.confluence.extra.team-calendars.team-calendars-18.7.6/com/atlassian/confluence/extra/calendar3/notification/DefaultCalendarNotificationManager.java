/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.jmx.JmxSMTPMailServer
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.user.search.SearchResult
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 *  javax.mail.util.ByteArrayDataSource
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.extra.calendar3.eventmacro.events.WaitingAttendantPromoted;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.extra.calendar3.events.ReminderNotificationEvent;
import com.atlassian.confluence.extra.calendar3.events.ReminderSetingEvent;
import com.atlassian.confluence.extra.calendar3.events.ReminderSettingCreated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreatedOnEventCreation;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreatedOnJiraEventCreation;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventCreated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventExcluded;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventMoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventRecurrenceRescheduled;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventRemoved;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarEventUpdated;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarInternalSubscribed;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRemoved;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEmailNotification;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventPeriodGroup;
import com.atlassian.confluence.extra.calendar3.notification.CalendarMailQueueItem;
import com.atlassian.confluence.extra.calendar3.notification.CalendarNotificationManager;
import com.atlassian.confluence.extra.calendar3.notification.ProfilePictureConst;
import com.atlassian.confluence.extra.calendar3.notification.ReminderEmailNotificationBuilder;
import com.atlassian.confluence.extra.calendar3.reminder.RemindingSettingHelper;
import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.CalendarAsyncHelper;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.ConditionalCallable;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.confluence.extra.calendar3.util.PdlUtil;
import com.atlassian.confluence.jmx.JmxSMTPMailServer;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.user.search.SearchResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@Component
public class DefaultCalendarNotificationManager
implements CalendarNotificationManager,
InitializingBean,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCalendarNotificationManager.class);
    private static final Pattern BUILTIN_PROFILEPICS_PATH_PATTERN = Pattern.compile(".*/images/icons/profilepics/.+$");
    private static final String MIME_TYPE_IMAGE_PNG = "image/png";
    private static final String PAGE_ICON_PATH = PdlUtil.isPdlEnabled() ? "/images/icons/contenttypes/page_16.png" : "/images/icons/docs_16.gif";
    private final EventPublisher eventPublisher;
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;
    private final PersonalInformationManager personalInformationManager;
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final FormatSettingsManager formatSettingsManager;
    private final VelocityHelperService velocityHelperService;
    private final MultiQueueTaskManager taskManager;
    private final NotificationManager notificationManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final BuildInformationManager buildInformationManager;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
    private final CalendarManager calendarManager;
    private final TransactionTemplate transactionTemplate;
    private final CalendarPermissionManager calendarPermissionManager;
    private final DataSourceFactory dataSourceFactory;
    private final String baseWebResourceModuleKey;
    private final RemindingSettingHelper remindingSettingHelper;
    private final ReminderEmailNotificationBuilder reminderEmailNotificationBuilder;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final CalendarSettingsManager calendarSettingsManager;
    private final MailServerManager mailServerManager;
    private final CalendarAsyncHelper calendarAsyncHelper;

    @Autowired
    public DefaultCalendarNotificationManager(@ComponentImport TransactionTemplate transactionTemplate, @ComponentImport EventPublisher eventPublisher, @ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PersonalInformationManager personalInformationManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport MultiQueueTaskManager taskManager, @ComponentImport NotificationManager notificationManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, BuildInformationManager buildInformationManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, @Qualifier(value="remindingSettingHelper") RemindingSettingHelper remindingSettingHelper, ReminderEmailNotificationBuilder reminderEmailNotificationBuilder, @ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, CalendarSettingsManager calendarSettingsManager, @ComponentImport DataSourceFactory dataSourceFactory, @ComponentImport MailServerManager mailServerManager, AsynchronousTaskExecutor asynchronousTaskExecutor) {
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.personalInformationManager = personalInformationManager;
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.formatSettingsManager = formatSettingsManager;
        this.velocityHelperService = velocityHelperService;
        this.taskManager = taskManager;
        this.notificationManager = notificationManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.buildInformationManager = buildInformationManager;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.remindingSettingHelper = remindingSettingHelper;
        this.reminderEmailNotificationBuilder = reminderEmailNotificationBuilder;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.calendarSettingsManager = calendarSettingsManager;
        this.dataSourceFactory = dataSourceFactory;
        this.mailServerManager = mailServerManager;
        this.calendarAsyncHelper = new CalendarAsyncHelper(asynchronousTaskExecutor, transactionTemplate);
        this.baseWebResourceModuleKey = buildInformationManager.getPluginKey() + ":calendar-resources";
    }

    public void afterPropertiesSet() {
        if (this.buildInformationManager.isNotificationsEnabled()) {
            this.eventPublisher.register((Object)this);
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @VisibleForTesting
    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    @EventListener
    public void notifyReminderSettingCreated(ReminderSettingCreated reminderSettingCreated) throws Exception {
        this.calendarAsyncHelper.doAsync(() -> {
            this.setRemindingMeForSubscribers(reminderSettingCreated);
            return true;
        });
    }

    private void setRemindingMeForSubscribers(ReminderSetingEvent event) throws Exception {
        Object parentSubCal = Objects.requireNonNull(event).getSubCalendar();
        Objects.requireNonNull(parentSubCal);
        ReminderSettingCallback.ReminderSettingChange settingChange = event.getReminderSettingChange();
        Objects.requireNonNull(settingChange);
        this.doNotifyInTransaction(() -> {
            Option<PersistedSubCalendar> childSubCalendar = StringUtils.isNotEmpty((CharSequence)settingChange.getStoreKey()) ? this.calendarManager.getChildSubCalendarByStoreKey((PersistedSubCalendar)parentSubCal, settingChange.getStoreKey()) : this.calendarManager.getChildSubCalendarByCustomEventTypeId((PersistedSubCalendar)parentSubCal, settingChange.getCustomEventTypeId());
            if (childSubCalendar.isEmpty()) {
                LOG.warn("Could not enable reminding me for not existed child sub calendar");
                return null;
            }
            Set<ConfluenceUser> subscribers = this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar((PersistedSubCalendar)parentSubCal, false);
            subscribers.add(event.getTrigger());
            this.remindingSettingHelper.enableRemindingForWatcher(subscribers, (PersistedSubCalendar)childSubCalendar.get());
            return null;
        });
    }

    @EventListener
    public void notifySubCalendarCreatedOnEventCreation(SubCalendarCreatedOnEventCreation subCalendarCreatedOnEventCreation) {
        Object parent = subCalendarCreatedOnEventCreation.getSubCalendar();
        PersistedSubCalendar childSubCalendar = (PersistedSubCalendar)subCalendarCreatedOnEventCreation.getSource();
        Set<EventTypeReminder> reminderSettings = ((PersistedSubCalendar)parent).getEventTypeReminders();
        Option defaultEventTypeReminderSettings = Iterables.findFirst(reminderSettings, input -> input.getEventTypeId().equals(childSubCalendar.getType()));
        Set<CustomEventType> customEventTypes = ((PersistedSubCalendar)parent).getCustomEventTypes();
        Option customEventTypeReminderSettings = Iterables.findFirst(customEventTypes, input -> input.getCustomEventTypeId().equals(childSubCalendar.getCustomEventTypeId()) && input.getPeriodInMins() > 0);
        if (defaultEventTypeReminderSettings.isEmpty() && customEventTypeReminderSettings.isEmpty()) {
            return;
        }
        LOG.info("Will enable reminding setting for watcher");
        Set<ConfluenceUser> subscribingUsers = this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar((PersistedSubCalendar)parent, false);
        this.remindingSettingHelper.enableRemindingForWatcher(subscribingUsers, childSubCalendar);
    }

    @EventListener
    public void notifySubCalendarInternalSubscribed(SubCalendarInternalSubscribed subCalendarInternalSubscribed) throws Exception {
        this.calendarAsyncHelper.doAsyncWithTransaction(() -> {
            this.remindingSettingHelper.enableRemindingFor(subCalendarInternalSubscribed.getTrigger(), (PersistedSubCalendar)subCalendarInternalSubscribed.getSource());
            return true;
        });
    }

    @EventListener
    public void notifySubCalendarCreatedOnJiraEventCreation(SubCalendarCreatedOnJiraEventCreation subCalendarCreatedOnJiraEventCreation) {
        Object parent = subCalendarCreatedOnJiraEventCreation.getSubCalendar();
        PersistedSubCalendar childSubCalendar = (PersistedSubCalendar)subCalendarCreatedOnJiraEventCreation.getSource();
        LOG.info("Will enable reminding setting for watcher");
        Set<ConfluenceUser> subscribingUsers = this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar((PersistedSubCalendar)parent, false);
        this.remindingSettingHelper.enableRemindingForWatcher(subscribingUsers, childSubCalendar);
    }

    @EventListener
    public void notifyReminderEvent(ReminderNotificationEvent reminderNotificationEvent) throws MailException {
        Objects.requireNonNull(reminderNotificationEvent);
        Objects.requireNonNull(reminderNotificationEvent.getTrigger());
        Objects.requireNonNull(reminderNotificationEvent.getSource());
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            ConfluenceUser userToBeNotified = reminderNotificationEvent.getTrigger();
            Collection notifyEvents = (Collection)reminderNotificationEvent.getSource();
            if (notifyEvents.size() > 0) {
                this.sendEventReminderNotification(userToBeNotified, notifyEvents);
            }
            return true;
        }));
    }

    @EventListener
    public void notifyEventAdded(SubCalendarEventCreated eventCreated) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            SubCalendarEvent newEvent = eventCreated.getEvent();
            ConfluenceUser trigger = this.getEventTrigger(eventCreated);
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> newMentions = this.getMentions(newEvent);
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> usersToNotify = this.getMailableUsers(newEvent, Collections.emptySet(), this.mergeUsers(Collections.singletonList(trigger), newMentions));
            for (ConfluenceUser userToNotify : usersToNotify) {
                this.sendEventAddedNotification(trigger, userToNotify, newEvent);
            }
            for (ConfluenceUser newMention : newMentions) {
                if (!this.isUserMailable(trigger, newMention, newEvent)) continue;
                this.sendMentionNotification(trigger, newMention, newEvent);
            }
            return true;
        }));
    }

    private void sendMentionNotification(ConfluenceUser trigger, ConfluenceUser mentioned, SubCalendarEvent subCalendarEvent) throws MailException, MessagingException, IOException {
        String triggerDisplayName = this.getUserDisplayName(trigger);
        PersistedSubCalendar changedSubCalendarForMentioned = this.getChangedSubCalendarForUser(subCalendarEvent, mentioned);
        Email notification = this.createBaseEventNotification(trigger, mentioned, this.getText(mentioned, "calendar3.notification.mention.title", triggerDisplayName, changedSubCalendarForMentioned.getName()), this.getText(mentioned, "calendar3.notification.mention.subtitle", this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), this.getSubCalendarPreviewUrl(changedSubCalendarForMentioned), HtmlUtil.htmlEncode((String)changedSubCalendarForMentioned.getName())), null, subCalendarEvent, changedSubCalendarForMentioned);
        CalendarMailQueueItem item = new CalendarMailQueueItem(notification);
        this.taskManager.addTask("mail", (Task)item);
    }

    private String getUserProfileUrl(ConfluenceUser user) {
        return String.format("%s/display/~%s", this.getBaseUrl(), HtmlUtil.urlEncode((String)user.getName()));
    }

    private String getBaseUrl() {
        return this.settingsManager.getGlobalSettings().getBaseUrl();
    }

    private String getSubCalendarPreviewUrl(PersistedSubCalendar persistedSubCalendar) {
        PersistedSubCalendar calendar = persistedSubCalendar;
        if (persistedSubCalendar instanceof SubscribingSubCalendar) {
            calendar = this.calendarManager.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId());
        }
        String spaceKey = calendar.getSpaceKey();
        String subCalendarId = calendar.getId();
        String calendarName = calendar.getName();
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            return String.format("%s/calendar/previewcalendar.action?subCalendarId=%s", this.getBaseUrl(), subCalendarId);
        }
        return String.format("%s/display/%s/calendar/%s?calendarName=%s", this.getBaseUrl(), spaceKey, subCalendarId, calendarName);
    }

    private String getText(ConfluenceUser user, String i18nKey, Object ... substitutions) {
        return this.getI18NBean(user).getText(i18nKey, substitutions);
    }

    private I18NBean getI18NBean(ConfluenceUser user) {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)(user == null ? AuthenticatedUserThreadLocal.get() : user)));
    }

    private boolean isUserMailable(ConfluenceUser trigger, ConfluenceUser userToNotify, SubCalendarEvent subCalendarEvent) {
        return null != userToNotify && !this.isMentionedTrigger(trigger, userToNotify) && this.isUserActiveWithEmail(userToNotify) && this.calendarPermissionManager.hasViewEventPrivilege(this.getChangedSubCalendarForUser(subCalendarEvent, userToNotify), userToNotify);
    }

    private boolean isMentionedTrigger(ConfluenceUser trigger, ConfluenceUser mentioned) {
        return trigger.equals(mentioned);
    }

    private boolean isUserActiveWithEmail(ConfluenceUser user) {
        return !this.userAccessor.isDeactivated((User)user) && StringUtils.isNotBlank((CharSequence)user.getEmail());
    }

    private Set<ConfluenceUser> mergeUsers(Collection<ConfluenceUser> ... users) {
        HashSet<ConfluenceUser> mergedUsers = new HashSet<ConfluenceUser>();
        for (Collection<ConfluenceUser> userCollection : users) {
            mergedUsers.addAll(userCollection);
        }
        return mergedUsers;
    }

    private Set<ConfluenceUser> getMentions(SubCalendarEvent subCalendarEvent) {
        Set<Invitee> invitees = subCalendarEvent.getInvitees();
        if (null == invitees) {
            return Collections.emptySet();
        }
        return new HashSet<ConfluenceUser>(Collections2.filter((Collection)Collections2.transform(invitees, (Function)new InviteeToUserTransformFunction(this.userAccessor)), (Predicate)Predicates.notNull()));
    }

    private void sendEventReminderNotification(ConfluenceUser userToNotify, Collection<ReminderEvent> notifyEvents) throws MailException, MessagingException, IOException {
        if (this.getDefaultSMTPServer() == null) {
            LOG.debug("There is no mail server is setup will discard reminder notification");
            return;
        }
        String titleKey = this.getText(userToNotify, "calendar3.notification.reminder.title", new Object[0]);
        Email notification = this.createReminderEventNotification(userToNotify, titleKey, notifyEvents);
        CalendarMailQueueItem item = new CalendarMailQueueItem(notification);
        this.taskManager.addTask("mail", (Task)item);
    }

    private void sendEventAddedNotification(ConfluenceUser trigger, ConfluenceUser userToNotify, SubCalendarEvent newEvent) throws MailException, MessagingException, IOException {
        String triggerDisplayName = this.getUserDisplayName(trigger);
        PersistedSubCalendar changedSubCalendarForUser = this.getChangedSubCalendarForUser(newEvent, userToNotify);
        String eventTypeName = this.getEventTypeName(newEvent, userToNotify);
        String titleKey = this.getI18nKeyForEventType(newEvent.getEventType(), "calendar3.notification.event.added.title");
        String subtitleKey = this.getI18nKeyForEventType(newEvent.getEventType(), "calendar3.notification.event.added.subtitle");
        Email notification = this.createBaseEventNotification(trigger, userToNotify, this.getText(userToNotify, titleKey, triggerDisplayName, eventTypeName, changedSubCalendarForUser.getName()), this.getText(userToNotify, subtitleKey, this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), eventTypeName, this.getSubCalendarPreviewUrl(changedSubCalendarForUser), HtmlUtil.htmlEncode((String)changedSubCalendarForUser.getName())), null, newEvent, changedSubCalendarForUser);
        CalendarMailQueueItem item = new CalendarMailQueueItem(notification);
        this.taskManager.addTask("mail", (Task)item);
    }

    private Email createReminderEventNotification(ConfluenceUser toUser, String title, Collection<ReminderEvent> notifyEvents) {
        Email notification = new Email(toUser.getEmail());
        notification.setSubject(title);
        notification.setFrom(this.getDefaultSMTPServer().getDefaultFrom());
        notification.setFromName(this.getText(toUser, "calendar3.reminder.from", new Object[0]));
        notification.setMimeType("text/html");
        notification.setMultipart((Multipart)new MimeMultipart("related"));
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        ReminderEmailNotification reminderEmailNotification = this.reminderEmailNotificationBuilder.build(notification, toUser, notifyEvents);
        I18NBean i18NBean = this.getI18NBean(toUser);
        velocityContext.put("i18n", i18NBean);
        velocityContext.put("i18nBean", i18NBean);
        velocityContext.put("title", title);
        velocityContext.put("reminderEmailNotification", reminderEmailNotification);
        if (reminderEmailNotification.getEventCount() == 1) {
            ReminderEventPeriodGroup periodGroup = reminderEmailNotification.getReminderEventPeriodGroups().get(0);
            velocityContext.put("periodGroup", periodGroup);
            velocityContext.put("reminderEvent", periodGroup.getReminderEventSubCalendarGroups().get(0).getReminderEvents().get(0));
            notification.setBody(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/reminder-single-event-notification.vm", velocityContext));
        } else {
            notification.setBody(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/reminder-event-notification.vm", velocityContext));
        }
        return notification;
    }

    private Email createBaseEventNotification(ConfluenceUser trigger, ConfluenceUser toUser, String title, String subTitleHtml, String warningNoteHtml, SubCalendarEvent subCalendarEvent, PersistedSubCalendar parentCalendar) throws MailException, MessagingException, IOException {
        Set<Invitee> invitees;
        SubCalendarWatchGroup watchGroup;
        Space space;
        boolean isWatchingCalendar;
        boolean isShowEnablingRemindingMeOption;
        Email notification = new Email(toUser.getEmail());
        notification.setSubject(title);
        notification.setFrom(this.getDefaultSMTPServer().getDefaultFrom());
        notification.setFromName(this.getFromName(trigger));
        notification.setMimeType("text/html");
        notification.setMultipart((Multipart)new MimeMultipart("related"));
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        velocityContext.put("eventTypeName", this.getEventTypeName(subCalendarEvent, toUser));
        velocityContext.put("notifiedUser", toUser);
        velocityContext.put("childCalendar", subCalendarEvent.getSubCalendar());
        PersistedSubCalendar realParentSubCalendar = parentCalendar;
        if (parentCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar) {
            realParentSubCalendar = subCalendarEvent.getSubCalendar() != null ? subCalendarEvent.getSubCalendar().getParent() : parentCalendar;
        }
        velocityContext.put("parentCalendar", realParentSubCalendar);
        String childSubCalendarId = subCalendarEvent.getSubCalendar().getId();
        int reminderPeriod = realParentSubCalendar.getReminderPeriodFor(subCalendarEvent);
        Set<String> remindingSubCalendars = this.calendarManager.getChildSubCalendarHasReminders(toUser, Lists.newArrayList((Object[])new String[]{childSubCalendarId}).toArray(new String[0]));
        boolean isAlreadyEnableRemindingSetting = remindingSubCalendars != null && remindingSubCalendars.size() > 0 && !CalendarUtil.isJiraSubCalendarType(parentCalendar.getType());
        boolean bl = isShowEnablingRemindingMeOption = !isAlreadyEnableRemindingSetting && reminderPeriod > 0;
        if (isShowEnablingRemindingMeOption) {
            MimeBodyPart reminderIconBodyPart = new MimeBodyPart();
            reminderIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/reminder.png", MIME_TYPE_IMAGE_PNG)));
            reminderIconBodyPart.setFileName("reminder.png");
            reminderIconBodyPart.setHeader("Content-ID", "<setreminder>");
            reminderIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)reminderIconBodyPart);
        }
        velocityContext.put("showRemindingMeOption", isShowEnablingRemindingMeOption);
        I18NBean i18NBean = this.getI18NBean(toUser);
        velocityContext.put("i18n", i18NBean);
        velocityContext.put("i18nBean", i18NBean);
        velocityContext.put("title", title);
        velocityContext.put("subTitleHtml", subTitleHtml);
        velocityContext.put("event", subCalendarEvent);
        DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(toUser));
        Locale userLocale = this.localeManager.getLocale((User)toUser);
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern((String)this.formatSettingsManager.getDateFormat()).withLocale(userLocale);
        DateTime eventStart = subCalendarEvent.getStartTime();
        DateTime eventEnd = subCalendarEvent.getEndTime();
        if (subCalendarEvent.isAllDay()) {
            velocityContext.put("startDate", dateFormatter.print((ReadableInstant)new DateTime(eventStart.getYear(), eventStart.getMonthOfYear(), eventStart.getDayOfMonth(), 0, 0, 0, 0, userTimeZone)));
            if (!eventStart.plusDays(1).isEqual((ReadableInstant)eventEnd)) {
                velocityContext.put("endDate", dateFormatter.print((ReadableInstant)new DateTime(eventEnd.getYear(), eventEnd.getMonthOfYear(), eventEnd.getDayOfMonth(), 0, 0, 0, 0, userTimeZone).minusDays(1)));
            }
        } else {
            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern((String)this.formatSettingsManager.getTimeFormat()).withLocale(userLocale);
            eventStart = eventStart.withZone(userTimeZone);
            eventEnd = eventEnd.withZone(userTimeZone);
            velocityContext.put("startDate", dateFormatter.print((ReadableInstant)eventStart));
            velocityContext.put("startTime", timeFormatter.print((ReadableInstant)eventStart));
            velocityContext.put("endDate", dateFormatter.print((ReadableInstant)eventEnd));
            velocityContext.put("endTime", timeFormatter.print((ReadableInstant)eventEnd));
        }
        if (null != subCalendarEvent.getRepeat() || StringUtils.isNotBlank((CharSequence)subCalendarEvent.getRecurrenceId())) {
            velocityContext.put("repeatString", this.getRepeatAsString(subCalendarEvent, toUser));
            MimeBodyPart infoIconBodyPart = new MimeBodyPart();
            infoIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource("/images/icons/emoticons/information.png", "information.png")));
            infoIconBodyPart.setHeader("Content-ID", "<infoicon>");
            infoIconBodyPart.setFileName("information.gif");
            infoIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)infoIconBodyPart);
        }
        if (isWatchingCalendar = this.canUnwatchCalendar(toUser, subCalendarEvent)) {
            MimeBodyPart subCalendarIconBodyPart = new MimeBodyPart();
            subCalendarIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/other_12.png", MIME_TYPE_IMAGE_PNG)));
            subCalendarIconBodyPart.setFileName("other_12.png");
            subCalendarIconBodyPart.setHeader("Content-ID", "<subcalendartype>");
            subCalendarIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)subCalendarIconBodyPart);
            velocityContext.put("watchedSubCalendar", subCalendarEvent.getSubCalendar());
        }
        HashSet<SubCalendarWatchGroup> uniqueSubCalendarWatchGroups = new HashSet<SubCalendarWatchGroup>();
        SubCalendarWatchGroupFactory subCalendarWatchGroupFactory = new SubCalendarWatchGroupFactory(this.dataSourceFactory, this.notificationManager);
        Collection<AbstractPage> contentEmbeddingSubCalendar = this.getWatchedContentEmbeddingSubCalendar(subCalendarEvent.getSubCalendar().getEffectiveParent(), toUser);
        for (AbstractPage watchedPageEmbeddingSubCalendar : contentEmbeddingSubCalendar) {
            uniqueSubCalendarWatchGroups.add(subCalendarWatchGroupFactory.getWatchGroup(watchedPageEmbeddingSubCalendar));
        }
        String spaceKey = realParentSubCalendar.getSpaceKey();
        if (StringUtils.isNotBlank((CharSequence)spaceKey) && (space = this.spaceManager.getSpace(spaceKey)) != null && this.permissionManager.hasPermission((User)toUser, Permission.VIEW, (Object)space) && (watchGroup = subCalendarWatchGroupFactory.getWatchGroup(toUser, space)) != null) {
            uniqueSubCalendarWatchGroups.add(watchGroup);
        }
        ArrayList sortedSubCalendarWatchGroups = new ArrayList(uniqueSubCalendarWatchGroups);
        Collections.sort(sortedSubCalendarWatchGroups, new Comparator<SubCalendarWatchGroup>(){

            @Override
            public int compare(SubCalendarWatchGroup leftSubCalendarWatchGroup, SubCalendarWatchGroup rightSubCalendarWatchGroup) {
                int result;
                if (leftSubCalendarWatchGroup.isCompareSpaceOnly() && rightSubCalendarWatchGroup.isCompareSpaceOnly()) {
                    result = this.compareSpaces(leftSubCalendarWatchGroup.getSpace(), rightSubCalendarWatchGroup.getSpace());
                } else if (!leftSubCalendarWatchGroup.isCompareSpaceOnly() && !rightSubCalendarWatchGroup.isCompareSpaceOnly()) {
                    result = leftSubCalendarWatchGroup.getPage().getTitle().compareTo(rightSubCalendarWatchGroup.getPage().getTitle());
                    if (0 == result) {
                        result = this.compareSpaces(leftSubCalendarWatchGroup.getSpace(), rightSubCalendarWatchGroup.getSpace());
                    }
                } else {
                    result = leftSubCalendarWatchGroup.isCompareSpaceOnly() ? -1 : 1;
                }
                return result;
            }

            private int compareSpaces(Space leftSpace, Space rightSpace) {
                int result = leftSpace.getName().compareTo(rightSpace.getName());
                if (0 == result) {
                    result = leftSpace.getKey().compareTo(rightSpace.getKey());
                }
                return result;
            }
        });
        velocityContext.put("subCalendarWatchGroups", sortedSubCalendarWatchGroups);
        HashSet<String> uniqueIconIds = new HashSet<String>();
        StringBuilder contentIdBuilder = new StringBuilder();
        for (SubCalendarWatchGroup subCalendarWatchGroup : sortedSubCalendarWatchGroups) {
            String iconId = subCalendarWatchGroup.getIconId();
            if (uniqueIconIds.contains(iconId)) continue;
            uniqueIconIds.add(iconId);
            MimeBodyPart subCalendarWatchGroupIcon = new MimeBodyPart();
            subCalendarWatchGroupIcon.setDisposition("inline");
            subCalendarWatchGroupIcon.setDataHandler(subCalendarWatchGroup.getIconDataSource());
            subCalendarWatchGroupIcon.setFileName(subCalendarWatchGroup.getIconFileName());
            contentIdBuilder.setLength(0);
            subCalendarWatchGroupIcon.setHeader("Content-ID", contentIdBuilder.append('<').append(iconId).append('>').toString());
            notification.getMultipart().addBodyPart((BodyPart)subCalendarWatchGroupIcon);
        }
        if (!isWatchingCalendar && sortedSubCalendarWatchGroups.isEmpty()) {
            velocityContext.put("watchSubCalendarTipHtml", this.getWatchTipHtml(subCalendarEvent, toUser));
        }
        if (StringUtils.isNotBlank((CharSequence)warningNoteHtml)) {
            velocityContext.put("warningNoteHtml", warningNoteHtml);
            MimeBodyPart warningIconBodyPart = new MimeBodyPart();
            warningIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource("/images/icons/emoticons/forbidden.png", "forbidden.png")));
            warningIconBodyPart.setHeader("Content-ID", "<warningicon>");
            warningIconBodyPart.setFileName("warning.png");
            warningIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)warningIconBodyPart);
        }
        if (subCalendarEvent.getInvitees() != null && !subCalendarEvent.getInvitees().isEmpty() && null != (invitees = subCalendarEvent.getInvitees()) && invitees.size() > 1) {
            LinkedHashMap<Invitee, String> inviteesToAvatarSrcMap = new LinkedHashMap<Invitee, String>();
            StringBuilder inviteeAvatarIconCidBuilder = new StringBuilder();
            String profilePath = ProfilePictureConst.DEFAULT_PROFILE.getDownloadPath();
            String externalInviteeAvatarCid = DigestUtils.sha1Hex(profilePath);
            if (!Collections2.filter(invitees, (Predicate)Predicates.not((Predicate)Predicates.instanceOf(ConfluenceUserInvitee.class))).isEmpty()) {
                MimeBodyPart externalInviteeIconBodyPart = new MimeBodyPart();
                externalInviteeIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource(profilePath, "default.gif")));
                externalInviteeIconBodyPart.setHeader("Content-ID", inviteeAvatarIconCidBuilder.append('<').append(externalInviteeAvatarCid).append('>').toString());
                externalInviteeIconBodyPart.setFileName("external-user.gif");
                externalInviteeIconBodyPart.setDisposition("inline");
                notification.getMultipart().addBodyPart((BodyPart)externalInviteeIconBodyPart);
            }
            HashMap<String, String> inviteeIconParams = new HashMap<String, String>(){
                {
                    this.put("width", "22");
                    this.put("height", "22");
                }
            };
            for (Invitee invitee : invitees) {
                if (invitee instanceof ConfluenceUserInvitee) {
                    MimeBodyPart inviteeIconBodyPart = new MimeBodyPart();
                    ConfluenceUser confluenceInvitee = ((ConfluenceUserInvitee)invitee).getUser();
                    IdentifiableContentDataHandler avatarDataHandler = this.createAvatarDataHandler(confluenceInvitee, (Map<String, String>)inviteeIconParams);
                    inviteeIconBodyPart.setDataHandler((DataHandler)avatarDataHandler);
                    inviteeIconBodyPart.setFileName(avatarDataHandler.getFileName());
                    String confluenceInviteeAvatarCid = DigestUtils.sha1Hex(confluenceInvitee.getName());
                    inviteesToAvatarSrcMap.put(invitee, confluenceInviteeAvatarCid);
                    inviteeAvatarIconCidBuilder.setLength(0);
                    inviteeIconBodyPart.setHeader("Content-ID", inviteeAvatarIconCidBuilder.append('<').append(confluenceInviteeAvatarCid).append('>').toString());
                    inviteeIconBodyPart.setDisposition("inline");
                    notification.getMultipart().addBodyPart((BodyPart)inviteeIconBodyPart);
                    continue;
                }
                inviteesToAvatarSrcMap.put(invitee, externalInviteeAvatarCid);
            }
            velocityContext.put("invitees", inviteesToAvatarSrcMap);
        }
        MimeBodyPart senderIconBodyPart = new MimeBodyPart();
        IdentifiableContentDataHandler senderAvatarDataHandler = this.createAvatarDataHandler(trigger);
        senderIconBodyPart.setDataHandler((DataHandler)senderAvatarDataHandler);
        senderIconBodyPart.setHeader("Content-ID", "<" + senderAvatarDataHandler.getContentId() + ">");
        senderIconBodyPart.setFileName(senderAvatarDataHandler.getFileName());
        senderIconBodyPart.setDisposition("inline");
        notification.getMultipart().addBodyPart((BodyPart)senderIconBodyPart);
        velocityContext.put("senderIconCid", senderAvatarDataHandler.getContentId());
        String eventIconUrl = subCalendarEvent.getIconUrl();
        if (StringUtils.isNotBlank((CharSequence)eventIconUrl)) {
            String eventIconCid;
            MimeBodyPart eventIconBodyPart = new MimeBodyPart();
            Attachment eventIconAttachment = this.getUrlAsAttachment(eventIconUrl);
            if (null == eventIconAttachment) {
                eventIconCid = DigestUtils.sha1Hex(eventIconUrl);
                if (this.isUrlPointingToDefaultSpaceLogo(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource("/images/logo/default-space-logo.svg", "confluence_48_white.png")));
                    eventIconBodyPart.setFileName("confluence_48_white.png");
                } else if (this.isUrlPointingToUnreleasedJiraVersionIcon(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/version_open_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("version_open_48.png");
                } else if (this.isUrlPointingToReleasedJiraVersionIcon(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/version_closed_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("version_closed_48.png");
                } else if (this.isUrlPointingToJiraIssueIcon(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/issue_types_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("issue_types_48.png");
                } else if (this.isUrlPointingToGreenHopperIcon(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/greenhopper_sprint_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("greenhopper_sprint_48.png");
                } else if (this.isUrlPointingToMultiplePeopleIcon(eventIconUrl)) {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/people_multiple_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("people_multiple_48.png");
                } else if (this.isUrlPointingToBuiltInUserAvatar(eventIconUrl)) {
                    String baseUrl = this.getBaseUrl();
                    String[] eventIconUrlPathTokens = StringUtils.split((String)eventIconUrl, (String)"/");
                    String eventIconFileName = eventIconUrlPathTokens[eventIconUrlPathTokens.length - 1];
                    eventIconBodyPart.setFileName(eventIconFileName);
                    if (StringUtils.startsWith((CharSequence)eventIconUrl, (CharSequence)baseUrl)) {
                        eventIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource(eventIconUrl.substring(baseUrl.length()), eventIconFileName)));
                    } else {
                        eventIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource(eventIconUrl, eventIconFileName)));
                    }
                } else if (StringUtils.equals((CharSequence)subCalendarEvent.getEventType(), (CharSequence)"custom") && StringUtils.isNotBlank((CharSequence)subCalendarEvent.getCustomEventTypeId())) {
                    try {
                        PersistedSubCalendar persistedSubCalendar = this.getChangedSubCalendarForUser(subCalendarEvent, toUser);
                        CustomEventType customEventType = this.getCustomEventTypeCustom(persistedSubCalendar, subCalendarEvent.getCustomEventTypeId());
                        String iconCustom = customEventType.getIcon() + "_48.png";
                        eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/customeventtype/" + iconCustom, MIME_TYPE_IMAGE_PNG)));
                        eventIconBodyPart.setFileName(iconCustom);
                    }
                    catch (Exception e) {
                        LOG.error("error when get icon custom event type for notify", (Throwable)e);
                        eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/events_48.png", MIME_TYPE_IMAGE_PNG)));
                        eventIconBodyPart.setFileName("events_48.png");
                    }
                } else {
                    eventIconBodyPart.setDataHandler(new DataHandler(this.createClassPathResourceDataSource("com/atlassian/confluence/extra/calendar3/img/events_48.png", MIME_TYPE_IMAGE_PNG)));
                    eventIconBodyPart.setFileName("events_48.png");
                }
            } else {
                IdentifiableContentDataHandler attachmentDataHandler = this.createAttachmentDataHandler(eventIconAttachment, null);
                eventIconBodyPart.setDataHandler((DataHandler)attachmentDataHandler);
                eventIconBodyPart.setFileName(attachmentDataHandler.getFileName());
                eventIconCid = attachmentDataHandler.getContentId();
            }
            eventIconBodyPart.setHeader("Content-ID", "<" + eventIconCid + ">");
            eventIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)eventIconBodyPart);
            velocityContext.put("eventIconCid", eventIconCid);
        }
        if (StringUtils.isNotBlank((CharSequence)subCalendarEvent.getUrl())) {
            MimeBodyPart linkIconBodyPart = new MimeBodyPart();
            linkIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource(PAGE_ICON_PATH, "page_16.png")));
            linkIconBodyPart.setHeader("Content-ID", "<linkicon>");
            linkIconBodyPart.setFileName("page_16.png");
            linkIconBodyPart.setDisposition("inline");
            notification.getMultipart().addBodyPart((BodyPart)linkIconBodyPart);
        }
        notification.setBody(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/event-notification.vm", velocityContext));
        return notification;
    }

    private boolean isUrlPointingToMultiplePeopleIcon(String url) {
        return StringUtils.equals((CharSequence)this.webResourceUrlProvider.getStaticPluginResourceUrl(this.baseWebResourceModuleKey, "img/people_multiple_48.png", UrlMode.ABSOLUTE), (CharSequence)url);
    }

    private boolean isUrlPointingToBuiltInUserAvatar(String url) {
        return BUILTIN_PROFILEPICS_PATH_PATTERN.matcher(url).matches();
    }

    private boolean isUrlPointingToGreenHopperIcon(String url) {
        return StringUtils.equals((CharSequence)this.webResourceUrlProvider.getStaticPluginResourceUrl(this.baseWebResourceModuleKey, "img/greenhopper_sprint_48.png", UrlMode.ABSOLUTE), (CharSequence)url);
    }

    private boolean isUrlPointingToJiraIssueIcon(String url) {
        return StringUtils.equals((CharSequence)this.webResourceUrlProvider.getStaticPluginResourceUrl(this.baseWebResourceModuleKey, "img/issue_types_48.png", UrlMode.ABSOLUTE), (CharSequence)url);
    }

    private boolean isUrlPointingToReleasedJiraVersionIcon(String url) {
        return StringUtils.equals((CharSequence)this.webResourceUrlProvider.getStaticPluginResourceUrl(this.baseWebResourceModuleKey, "img/version_closed_48.png", UrlMode.ABSOLUTE), (CharSequence)url);
    }

    private boolean isUrlPointingToUnreleasedJiraVersionIcon(String url) {
        return StringUtils.equals((CharSequence)this.webResourceUrlProvider.getStaticPluginResourceUrl(this.baseWebResourceModuleKey, "img/version_open_48.png", UrlMode.ABSOLUTE), (CharSequence)url);
    }

    private boolean isUrlPointingToDefaultSpaceLogo(String url) {
        return StringUtils.equals((CharSequence)(this.getBaseUrl() + SpaceLogo.DEFAULT_SPACE_LOGO.getDownloadPath()), (CharSequence)url);
    }

    private DataSource createClassPathResourceDataSource(String resourcePath, String mimeType) throws IOException {
        try (InputStream classPathResourceInput = this.getClass().getClassLoader().getResourceAsStream(resourcePath);){
            if (null == classPathResourceInput) {
                throw new IllegalArgumentException(String.format("Invalid class path resource specified: %s", resourcePath));
            }
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(classPathResourceInput, mimeType);
            return byteArrayDataSource;
        }
    }

    public String getRepeatAsString(SubCalendarEvent subCalendarEvent, ConfluenceUser forUser) {
        SubCalendarEvent.Repeat eventRepeat = subCalendarEvent.getRepeat();
        if (null == eventRepeat) {
            return "";
        }
        int repeatInterval = this.getRepeatInterval(eventRepeat);
        DateTime repeatEndDate = null;
        String until = eventRepeat.getUntil();
        if (StringUtils.isNotBlank((CharSequence)until)) {
            try {
                repeatEndDate = ISODateTimeFormat.basicDateTimeNoMillis().parseDateTime(eventRepeat.getUntil());
            }
            catch (IllegalArgumentException notLongUntilFormat) {
                repeatEndDate = ISODateTimeFormat.basicDate().parseDateTime(until);
            }
        }
        if (Arrays.asList(StringUtils.split((String)StringUtils.defaultString((String)eventRepeat.getByDay()), (String)", ")).isEmpty()) {
            if (null == repeatEndDate) {
                if (1 < repeatInterval) {
                    return this.getText(forUser, "calendar3.notification.repeat.notonspecificdays.forever.custominterval", repeatInterval, this.getFormattedRepeatFrequency(eventRepeat, forUser));
                }
                return this.getText(forUser, "calendar3.notification.repeat.notonspecificdays.forever.defaultinterval", this.getFormattedRepeatFrequency(eventRepeat, forUser));
            }
            DateTimeFormatter dateFormatter = DateTimeFormat.mediumDate().withLocale(this.localeManager.getLocale((User)forUser));
            if (1 < repeatInterval) {
                return this.getText(forUser, "calendar3.notification.repeat.notonspecificdays.ends.custominterval", repeatInterval, this.getFormattedRepeatFrequency(eventRepeat, forUser), dateFormatter.print((ReadableInstant)repeatEndDate.withZoneRetainFields(DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(forUser)))));
            }
            return this.getText(forUser, "calendar3.notification.repeat.notonspecificdays.ends.defaultinterval", this.getFormattedRepeatFrequency(eventRepeat, forUser), dateFormatter.print((ReadableInstant)repeatEndDate.withZoneRetainFields(DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(forUser)))));
        }
        if (null == repeatEndDate) {
            if (1 < repeatInterval) {
                return this.getText(forUser, "calendar3.notification.repeat.onspecificdays.forever.custominterval", repeatInterval, this.getFormattedRepeatFrequency(eventRepeat, forUser), this.getFormattedRepeatDays(eventRepeat, forUser));
            }
            return this.getText(forUser, "calendar3.notification.repeat.onspecificdays.forever.defaultinterval", this.getFormattedRepeatFrequency(eventRepeat, forUser), this.getFormattedRepeatDays(eventRepeat, forUser));
        }
        DateTimeFormatter dateFormatter = DateTimeFormat.mediumDate().withLocale(this.localeManager.getLocale((User)forUser));
        if (1 < repeatInterval) {
            return this.getText(forUser, "calendar3.notification.repeat.onspecificdays.ends.custominterval", repeatInterval, this.getFormattedRepeatFrequency(eventRepeat, forUser), this.getFormattedRepeatDays(eventRepeat, forUser), dateFormatter.print((ReadableInstant)repeatEndDate.withZoneRetainFields(DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(forUser)))));
        }
        return this.getText(forUser, "calendar3.notification.repeat.onspecificdays.ends.defaultinterval", this.getFormattedRepeatFrequency(eventRepeat, forUser), this.getFormattedRepeatDays(eventRepeat, forUser), dateFormatter.print((ReadableInstant)repeatEndDate.withZoneRetainFields(DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(forUser)))));
    }

    @VisibleForTesting
    String getFormattedRepeatDays(SubCalendarEvent.Repeat repeat, ConfluenceUser forUser) {
        HashSet<String> iCalendarDaysOfWeekRepeatOn = new HashSet<String>(Arrays.asList(StringUtils.split((String)StringUtils.defaultString((String)repeat.getByDay()), (String)", ")));
        Pattern dayPattern = Pattern.compile("(\\d)?(?<name>MO|TU|WE|TH|FR|SA|SU)");
        if (iCalendarDaysOfWeekRepeatOn.equals(new HashSet<String>(Arrays.asList("MO", "TU", "WE", "TH", "FR")))) {
            return this.getText(forUser, "calendar3.notification.repeat.freq.weekdays", new Object[0]);
        }
        LinkedHashMap<String, String> iCalendarDayOfWeekToUserLocaleFormattedDayOfWeekMap = this.getICalendarDayOfWeekToUserLocaleFormattedDayOfWeekMap(forUser);
        return iCalendarDaysOfWeekRepeatOn.stream().map(dayPattern::matcher).filter(Matcher::matches).map(matcher -> matcher.group("name")).map(iCalendarDayOfWeekToUserLocaleFormattedDayOfWeekMap::get).collect(Collectors.joining(", "));
    }

    private LinkedHashMap<String, String> getICalendarDayOfWeekToUserLocaleFormattedDayOfWeekMap(ConfluenceUser forUser) {
        List<String> iCalendarDaysOfWeek = Arrays.asList("SU", "MO", "TU", "WE", "TH", "FR", "SAT");
        DateTime firstSundaySinceEpoch = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC).withDayOfWeek(7);
        DateTimeFormatter userDayOfWeekFormatter = DateTimeFormat.forPattern((String)"EEE").withLocale(this.getLocaleManager().getLocale((User)forUser));
        LinkedHashMap<String, String> iCalendarDaysOfWeekToUserDaysOfWeek = new LinkedHashMap<String, String>();
        for (String iCalendarDayOfWeek : iCalendarDaysOfWeek) {
            iCalendarDaysOfWeekToUserDaysOfWeek.put(iCalendarDayOfWeek, userDayOfWeekFormatter.print((ReadableInstant)firstSundaySinceEpoch));
            firstSundaySinceEpoch = firstSundaySinceEpoch.plusDays(1);
        }
        return iCalendarDaysOfWeekToUserDaysOfWeek;
    }

    private String getFormattedRepeatFrequency(SubCalendarEvent.Repeat repeat, ConfluenceUser forUser) {
        String freq = repeat.getFreq();
        int interval = this.getRepeatInterval(repeat);
        if (StringUtils.equals((CharSequence)"DAILY", (CharSequence)freq)) {
            return this.getText(forUser, 1 < interval ? "calendar3.repeat.interval.days" : "calendar3.repeat.interval.day", new Object[0]);
        }
        if (StringUtils.equals((CharSequence)"WEEKLY", (CharSequence)freq)) {
            return this.getText(forUser, 1 < interval ? "calendar3.repeat.interval.weeks" : "calendar3.repeat.interval.week", new Object[0]);
        }
        if (StringUtils.equals((CharSequence)"MONTHLY", (CharSequence)freq)) {
            return this.getText(forUser, 1 < interval ? "calendar3.repeat.interval.months" : "calendar3.repeat.interval.month", new Object[0]);
        }
        if (StringUtils.equals((CharSequence)"YEARLY", (CharSequence)freq)) {
            return this.getText(forUser, 1 < interval ? "calendar3.repeat.interval.years" : "calendar3.repeat.interval.year", new Object[0]);
        }
        return "";
    }

    private int getRepeatInterval(SubCalendarEvent.Repeat repeat) {
        int repeatInterval = 1;
        try {
            repeatInterval = Integer.parseInt(StringUtils.trim((String)StringUtils.defaultString((String)repeat.getInterval())));
        }
        catch (NumberFormatException invalidFrequency) {
            LOG.debug(String.format("Invalid frequency in repeat: %s", repeat.getFreq()), (Throwable)invalidFrequency);
        }
        return repeatInterval;
    }

    private String getFromName(ConfluenceUser trigger) throws MailException {
        String from = "${fullname} (Confluence)";
        String name = "";
        String emailAddress = "";
        String hostname = "";
        try {
            JmxSMTPMailServer jmxSMTPMailServer = (JmxSMTPMailServer)this.getDefaultSMTPServer();
            if (jmxSMTPMailServer != null) {
                from = jmxSMTPMailServer.getFromName();
            }
        }
        catch (ClassCastException notJmxMailServer) {
            LOG.error("Default SMTP server not a JmxSMTPMailServer", (Throwable)notJmxMailServer);
        }
        name = trigger != null ? trigger.getFullName() : "Anonymous";
        emailAddress = trigger != null ? trigger.getEmail() : "";
        hostname = trigger != null && StringUtils.isNotBlank((CharSequence)emailAddress) ? emailAddress.substring(emailAddress.indexOf("@") + 1) : "";
        from = StringUtils.replace((String)from, (String)"${fullname}", (String)name);
        from = StringUtils.replace((String)from, (String)"${email}", (String)emailAddress);
        from = StringUtils.replace((String)from, (String)"${email.hostname}", (String)hostname);
        return from;
    }

    private Attachment getUrlAsAttachment(String url) {
        Attachment theAttachment = null;
        String attachmentDownloadPrefix = this.getBaseUrl() + "/download/attachments/";
        if (StringUtils.startsWith((CharSequence)url, (CharSequence)attachmentDownloadPrefix)) {
            int queryStringStart = url.indexOf(63);
            int attachmentVersion = -1;
            String urlWithoutQueryString = url;
            if (0 <= queryStringStart) {
                Map<String, Collection<String>> queryParams = this.getParametersFromQueryString(url.substring(queryStringStart));
                if (queryParams.containsKey("version")) {
                    attachmentVersion = Integer.parseInt(queryParams.get("version").iterator().next());
                }
                urlWithoutQueryString = url.substring(0, queryStringStart);
            }
            ContentEntityObject content = this.contentEntityManager.getById(Long.parseLong(urlWithoutQueryString.substring(attachmentDownloadPrefix.length(), urlWithoutQueryString.lastIndexOf(47))));
            String attachmentFileName = HtmlUtil.urlDecode((String)urlWithoutQueryString.substring(urlWithoutQueryString.lastIndexOf(47) + 1));
            theAttachment = -1 == attachmentVersion ? this.attachmentManager.getAttachment(content, attachmentFileName) : this.attachmentManager.getAttachment(content, attachmentFileName, attachmentVersion);
        }
        return theAttachment;
    }

    private Map<String, Collection<String>> getParametersFromQueryString(String queryString) {
        HashMap<String, Collection<String>> parameters = new HashMap<String, Collection<String>>();
        for (String parameterValuePair : StringUtils.split((String)StringUtils.defaultString((String)queryString, (String)"?&"))) {
            ArrayList<String> parameterValues;
            String[] valuePair = StringUtils.split((String)parameterValuePair, (String)"=");
            if (valuePair.length < 1) continue;
            String parameter = valuePair[0];
            if (parameters.containsKey(parameter)) {
                parameterValues = (ArrayList<String>)parameters.get(parameter);
            } else {
                parameterValues = new ArrayList<String>();
                parameters.put(parameter, parameterValues);
            }
            parameterValues.add(valuePair.length == 1 ? "" : valuePair[1]);
        }
        return parameters;
    }

    private IdentifiableContentDataHandler createAvatarDataHandler(ConfluenceUser user) throws MessagingException, IOException {
        return this.createAvatarDataHandler(user, null);
    }

    private IdentifiableContentDataHandler createAvatarDataHandler(ConfluenceUser user, Map<String, String> params) throws MessagingException, IOException {
        String profilePicture;
        PropertySet propertySet = this.userAccessor.getPropertySet(user);
        StringBuilder stringBuilder = new StringBuilder();
        if (propertySet == null || (profilePicture = propertySet.getString("confluence.user.profile.picture")) == null) {
            return this.createDefaultProfilePictureDataHandler(params);
        }
        if (profilePicture.startsWith("/images/icons/profilepics/")) {
            String contentId = DigestUtils.sha1Hex(profilePicture.getBytes(StandardCharsets.UTF_8));
            if (null != params && !params.isEmpty()) {
                contentId = stringBuilder.append(contentId).append('_').append(params.hashCode()).toString();
            }
            stringBuilder.setLength(0);
            String extension = profilePicture.lastIndexOf(46) >= 0 ? profilePicture.substring(profilePicture.lastIndexOf(46)) : "";
            return new IdentifiableContentDataHandler(this.dataSourceFactory.getAvatar((User)user), contentId, stringBuilder.append(contentId).append(extension).toString());
        }
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation((User)user);
        if (null == personalInformation) {
            return this.createDefaultProfilePictureDataHandler(params);
        }
        Attachment a = this.attachmentManager.getAttachment((ContentEntityObject)personalInformation, profilePicture);
        if (a != null) {
            return this.createAttachmentDataHandler(a, params);
        }
        return null;
    }

    private IdentifiableContentDataHandler createAttachmentDataHandler(Attachment attachment, Map<String, String> params) throws IOException {
        String fileExtension;
        byte[] attachmentContent = this.getAttachmentContentAsByteArray(attachment);
        StringBuilder stringBuilder = new StringBuilder();
        String contentId = DigestUtils.sha1Hex(attachmentContent);
        if (null != params && !params.isEmpty()) {
            contentId = stringBuilder.append(contentId).append(' ').append(params.hashCode()).toString();
        }
        if (StringUtils.isEmpty((CharSequence)(fileExtension = attachment.getFileExtension()))) {
            fileExtension = CalendarUtil.getImageExtensionFromMineType(attachment.getMediaType());
        }
        stringBuilder.setLength(0);
        return new IdentifiableContentDataHandler(new ProfileImageDataSource((DataSource)new ByteArrayDataSource(attachmentContent, attachment.getMediaType())), contentId, stringBuilder.append(contentId).append('.').append(StringUtils.defaultString((String)fileExtension)).toString());
    }

    private byte[] getAttachmentContentAsByteArray(Attachment attachment) throws IOException {
        try (InputStream attachmentInput = this.attachmentManager.getAttachmentData(attachment);){
            byte[] byArray = IOUtils.toByteArray((InputStream)attachmentInput);
            return byArray;
        }
    }

    private IdentifiableContentDataHandler createDefaultProfilePictureDataHandler(Map<String, String> params) throws IOException {
        String profilePath = "/images/icons/profilepics/default.svg".replace(".svg", ".png");
        String contentId = DigestUtils.sha1Hex(profilePath.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        if (null != params && !params.isEmpty()) {
            contentId = stringBuilder.append(contentId).append('_').append(params.hashCode()).toString();
        }
        stringBuilder.setLength(0);
        String extension = profilePath.lastIndexOf(46) >= 0 ? profilePath.substring(profilePath.lastIndexOf(46)) : "";
        return new IdentifiableContentDataHandler(this.dataSourceFactory.getServletContainerResource(profilePath, "default.png"), contentId, stringBuilder.append(contentId).append(extension).toString());
    }

    private String getWatchTipHtml(SubCalendarEvent subCalendarEvent, ConfluenceUser forUser) {
        String subCalendarId = subCalendarEvent.getSubCalendar().getId();
        if (subCalendarEvent.getSubCalendar() instanceof SubscribingSubCalendar) {
            subCalendarId = ((SubscribingSubCalendar)subCalendarEvent.getSubCalendar()).getSubscriptionId();
        } else if (subCalendarEvent.getSubCalendar().getParent() != null) {
            subCalendarId = subCalendarEvent.getSubCalendar().getParent().getId();
        }
        return this.getText(forUser, "calendar3.notification.watch.tip", String.format("%s/calendar/watchcalendar.action?addWatch=true&subscriptionId=%s", this.getBaseUrl(), HtmlUtil.urlEncode((String)subCalendarId)));
    }

    private boolean canUnwatchCalendar(ConfluenceUser byUser, SubCalendarEvent subCalendarEvent) {
        boolean canUnwatch;
        PersistedSubCalendar subCalendarToWatch = subCalendarEvent.getSubCalendar().getEffectiveParent();
        if (subCalendarToWatch instanceof SubscribingSubCalendar) {
            subCalendarToWatch = this.calendarManager.getSubCalendar(((SubscribingSubCalendar)subCalendarToWatch).getSubscriptionId());
        }
        if (!(canUnwatch = this.isWatchingSubCalendar(subCalendarToWatch, byUser, false))) {
            Collection summariesOfWatchedSubCalendars = Collections2.transform((Collection)Collections2.filter(this.subCalendarSubscriptionStatisticsAccessor.getSubscribingSubCalendarIds(subCalendarToWatch), (Predicate)Predicates.in(this.calendarManager.getUserPreference(byUser).getWatchedSubCalendars())), subCalendarId -> (SubscribingSubCalendarSummary)this.calendarManager.getSubCalendarSummary((String)subCalendarId));
            for (SubscribingSubCalendarSummary subCalendarSummary : summariesOfWatchedSubCalendars) {
                if (!StringUtils.equals((CharSequence)subCalendarToWatch.getId(), (CharSequence)subCalendarSummary.getSubscriptionId()) || !this.calendarManager.isWatching(this.calendarManager.getSubCalendar(subCalendarSummary.getId()), byUser, false)) continue;
                canUnwatch = true;
                break;
            }
        }
        return canUnwatch;
    }

    private Collection<AbstractPage> getWatchedContentEmbeddingSubCalendar(PersistedSubCalendar persistedSubCalendar, ConfluenceUser watcher) {
        if (persistedSubCalendar instanceof SubscribingSubCalendar) {
            return this.getWatchedContentEmbeddingSubCalendar(this.calendarManager.getSubCalendar(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId()), watcher);
        }
        Collection<ContentEntityObject> contentEmbeddingSubCalendar = this.subCalendarSubscriptionStatisticsAccessor.getContentEmbeddingSubCalendar(persistedSubCalendar);
        return Collections2.filter((Collection)Collections2.transform((Collection)Collections2.filter(contentEmbeddingSubCalendar, (Predicate)Predicates.instanceOf(AbstractPage.class)), contentEntityObject -> (AbstractPage)contentEntityObject), abstractPage -> this.permissionManager.hasPermission((User)watcher, Permission.VIEW, abstractPage) && this.notificationManager.isUserWatchingPageOrSpace((User)watcher, abstractPage.getSpace(), abstractPage));
    }

    private String getEventTypeName(SubCalendarEvent theEvent, ConfluenceUser forUser) {
        String changedEventType = "";
        if (StringUtils.equals((CharSequence)theEvent.getEventType(), (CharSequence)"custom") && StringUtils.isNotBlank((CharSequence)theEvent.getCustomEventTypeId())) {
            PersistedSubCalendar changedSubCalendarForUser = this.getChangedSubCalendarForUser(theEvent, forUser);
            CustomEventType customEventType = this.getCustomEventTypeCustom(changedSubCalendarForUser, theEvent.getCustomEventTypeId());
            if (customEventType != null) {
                changedEventType = customEventType.getTitle();
            }
        } else {
            changedEventType = this.getText(forUser, "calendar3.subcalendar.type." + theEvent.getEventType(), new Object[0]);
        }
        return changedEventType;
    }

    private CustomEventType getCustomEventTypeCustom(PersistedSubCalendar subCalendar, String customEventTypeId) {
        try {
            if (subCalendar.getCustomEventTypes() != null && subCalendar.getCustomEventTypes().size() > 0) {
                for (CustomEventType customEventType : subCalendar.getCustomEventTypes()) {
                    if (!StringUtils.equals((CharSequence)customEventType.getCustomEventTypeId(), (CharSequence)customEventTypeId)) continue;
                    return customEventType;
                }
            }
            return this.calendarManager.getCustomEventType(subCalendar, customEventTypeId);
        }
        catch (Exception e) {
            LOG.error("error get custom event type when notify ", (Throwable)e);
            return null;
        }
    }

    private PersistedSubCalendar getChangedSubCalendarForUser(SubCalendarEvent subCalendarEvent, ConfluenceUser forUser) {
        return this.getChangedSubCalendarForUser(subCalendarEvent.getSubCalendar(), forUser);
    }

    private PersistedSubCalendar getChangedSubCalendarForUser(PersistedSubCalendar eventSubCalendar, ConfluenceUser forUser) {
        if (eventSubCalendar.getParent() != null) {
            return this.getChangedSubCalendarForUser(eventSubCalendar.getParent(), forUser);
        }
        PersistedSubCalendar changedSubCalendar = eventSubCalendar instanceof SubscribingSubCalendar ? this.calendarManager.getSubCalendar(((SubscribingSubCalendar)eventSubCalendar).getSubscriptionId()) : eventSubCalendar;
        Collection changedSubCalendarSubscriptionSummaries = Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(forUser), this.calendarManager::getSubCalendarSummary), (Predicate)Predicates.and((Predicate)Predicates.notNull(), subCalendarSummary -> StringUtils.equals((CharSequence)subCalendarSummary.getId(), (CharSequence)changedSubCalendar.getId()) || subCalendarSummary instanceof SubscribingSubCalendarSummary && StringUtils.equals((CharSequence)((SubscribingSubCalendarSummary)subCalendarSummary).getSubscriptionId(), (CharSequence)changedSubCalendar.getId())));
        PersistedSubCalendar changedSubCalendarForUser = changedSubCalendar;
        if (!changedSubCalendarSubscriptionSummaries.isEmpty()) {
            changedSubCalendarForUser = this.calendarManager.getSubCalendar(((SubCalendarSummary)changedSubCalendarSubscriptionSummaries.iterator().next()).getId());
        }
        return changedSubCalendarForUser;
    }

    private String getUserDisplayName(ConfluenceUser trigger) {
        return (String)StringUtils.defaultIfEmpty((CharSequence)trigger.getFullName(), (CharSequence)trigger.getName());
    }

    protected Set<ConfluenceUser> getMailableUsers(SubCalendarEvent subCalendarEvent, Collection<PersistedSubCalendar> additionalSubCalendars, Collection<ConfluenceUser> usersToExclude) {
        final boolean includeSubscriptionsFromContent = !this.calendarSettingsManager.isExcludeSubscriptionsFromContent();
        HashSet<ConfluenceUser> mailableUsers = new HashSet<ConfluenceUser>();
        if (this.getDefaultSMTPServer() == null) {
            LOG.debug("There is no mail server is setup will discard notification");
            return mailableUsers;
        }
        ArrayList subCalendars = Lists.newArrayList((Object[])new PersistedSubCalendar[]{subCalendarEvent.getSubCalendar()});
        if (null != additionalSubCalendars) {
            subCalendars.addAll(additionalSubCalendars);
        }
        for (PersistedSubCalendar subCalendar : subCalendars) {
            PersistedSubCalendar sourceSubCalendar = subCalendar instanceof SubscribingSubCalendar ? this.calendarManager.getSubCalendar(((SubscribingSubCalendar)subCalendar).getSubscriptionId()) : subCalendar;
            final PersistedSubCalendar subCalendarToCalculateSubscriptionsTo = sourceSubCalendar.getEffectiveParent();
            final ArrayList subscribingSubCalendars = Lists.newArrayList();
            subscribingSubCalendars.addAll(Collections2.transform(this.subCalendarSubscriptionStatisticsAccessor.getSubscribingSubCalendarIds(subCalendarToCalculateSubscriptionsTo), this.calendarManager::getSubCalendar));
            mailableUsers.addAll(Collections2.filter(this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar(subCalendarToCalculateSubscriptionsTo, includeSubscriptionsFromContent), (Predicate)new Predicate<ConfluenceUser>(){

                public boolean apply(ConfluenceUser subscriber) {
                    if (LOG.isDebugEnabled() && subscriber != null) {
                        LOG.debug("Subscribed to calendar {}: {}", (Object)subCalendarToCalculateSubscriptionsTo.getId(), (Object)subscriber.getName());
                    }
                    return DefaultCalendarNotificationManager.this.calendarPermissionManager.hasViewEventPrivilege(subCalendarToCalculateSubscriptionsTo, subscriber) && DefaultCalendarNotificationManager.this.isUserActiveWithEmail(subscriber) && (DefaultCalendarNotificationManager.this.isWatchingSubCalendar(subCalendarToCalculateSubscriptionsTo, subscriber, includeSubscriptionsFromContent) || this.isWatchingAnyOfSubscription(subscribingSubCalendars, subscriber));
                }

                private boolean isWatchingAnyOfSubscription(Collection<PersistedSubCalendar> subscribingSubCalendars2, ConfluenceUser user) {
                    for (PersistedSubCalendar subscribingSubCalendar : subscribingSubCalendars2) {
                        if (!DefaultCalendarNotificationManager.this.isWatchingSubCalendar(subscribingSubCalendar, user, false)) continue;
                        return true;
                    }
                    return false;
                }
            }));
            ConfluenceUser sourceSubCalendarCreator = this.userAccessor.getUserByKey(new UserKey(StringUtils.defaultString((String)sourceSubCalendar.getEffectiveParent().getCreator())));
            if (null == sourceSubCalendarCreator || !this.isUserActiveWithEmail(sourceSubCalendarCreator) || !this.isWatchingSubCalendar(subCalendarToCalculateSubscriptionsTo, sourceSubCalendarCreator, true)) continue;
            mailableUsers.add(sourceSubCalendarCreator);
        }
        if (LOG.isDebugEnabled()) {
            for (ConfluenceUser user : mailableUsers) {
                if (user == null) continue;
                LOG.debug("Mailable user: {}", (Object)user.getName());
            }
        }
        if (null != usersToExclude) {
            if (LOG.isDebugEnabled()) {
                for (ConfluenceUser user : usersToExclude) {
                    if (user == null) continue;
                    LOG.debug("Excluded mailable user: {}", (Object)user.getName());
                }
            }
            return Sets.newHashSet((Iterable)Collections2.filter(mailableUsers, (Predicate)Predicates.not((Predicate)Predicates.in((Collection)Collections2.filter(usersToExclude, (Predicate)Predicates.notNull())))));
        }
        return mailableUsers;
    }

    private boolean isWatchingSubCalendar(PersistedSubCalendar subCalendar, ConfluenceUser user, boolean includeWatchesOnSubscribingContent) {
        return this.calendarManager.isWatching(subCalendar, user, includeWatchesOnSubscribingContent);
    }

    private ConfluenceUser getEventTrigger(CalendarEvent calendarEvent) {
        ConfluenceUser trigger = calendarEvent.getTrigger();
        return null == trigger ? null : this.userAccessor.getUserByKey(trigger.getKey());
    }

    private boolean canSendEmail() {
        return this.getDefaultSMTPServer() != null;
    }

    private SMTPMailServer getDefaultSMTPServer() {
        return this.mailServerManager.getDefaultSMTPMailServer();
    }

    @EventListener
    public void notifyEventChanged(SubCalendarEventUpdated eventChanged) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            SubCalendarEvent oldEvent = eventChanged.getPreviousSubCalendarEvent();
            SubCalendarEvent updatedEvent = eventChanged.getEvent();
            ConfluenceUser trigger = this.getEventTrigger(eventChanged);
            Set<ConfluenceUser> newMentions = this.getNewInvitees(oldEvent, updatedEvent);
            Set<ConfluenceUser> existingMentions = this.getExistingInvitees(oldEvent, updatedEvent);
            Set<ConfluenceUser> removedMentions = this.getUninvitedInvitees(oldEvent, updatedEvent);
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> usersToNotify = this.getMailableUsers(updatedEvent, Collections.emptySet(), this.mergeUsers(Collections.singletonList(trigger), newMentions, existingMentions, removedMentions));
            for (ConfluenceUser userToNotify : usersToNotify) {
                this.sendEventUpdatedNotification(trigger, userToNotify, oldEvent, updatedEvent);
            }
            for (ConfluenceUser invitee : this.mergeUsers(existingMentions, removedMentions)) {
                if (!this.isUserMailable(trigger, invitee, updatedEvent)) continue;
                this.sendEventUpdatedNotification(trigger, invitee, oldEvent, updatedEvent);
            }
            for (ConfluenceUser newInvitee : newMentions) {
                if (!this.isUserMailable(trigger, newInvitee, updatedEvent)) continue;
                this.sendMentionNotification(trigger, newInvitee, updatedEvent);
            }
            return true;
        }));
    }

    private Set<ConfluenceUser> getUninvitedInvitees(SubCalendarEvent oldEvent, SubCalendarEvent newEvent) {
        Set<Object> newInvitees = null == newEvent.getInvitees() ? Collections.emptySet() : newEvent.getInvitees();
        return new HashSet<ConfluenceUser>(Collections2.filter((Collection)Collections2.transform(null == oldEvent.getInvitees() ? Collections.emptySet() : Collections2.filter(oldEvent.getInvitees(), (Predicate)Predicates.not((Predicate)Predicates.in(newInvitees))), (Function)new InviteeToUserTransformFunction(this.userAccessor)), (Predicate)Predicates.notNull()));
    }

    private Set<ConfluenceUser> getExistingInvitees(SubCalendarEvent oldEvent, SubCalendarEvent newEvent) {
        Set<Object> oldInvitees = null == oldEvent.getInvitees() ? Collections.emptySet() : oldEvent.getInvitees();
        return new HashSet<ConfluenceUser>(Collections2.filter((Collection)Collections2.transform(null == newEvent.getInvitees() ? Collections.emptySet() : Collections2.filter(newEvent.getInvitees(), (Predicate)Predicates.in(oldInvitees)), (Function)new InviteeToUserTransformFunction(this.userAccessor)), (Predicate)Predicates.notNull()));
    }

    private Set<ConfluenceUser> getNewInvitees(SubCalendarEvent oldEvent, SubCalendarEvent newEvent) {
        Set oldInvitees = null == oldEvent || null == oldEvent.getInvitees() ? Collections.emptySet() : oldEvent.getInvitees();
        return new HashSet<ConfluenceUser>(Collections2.filter((Collection)Collections2.transform(null == newEvent.getInvitees() ? Collections.emptySet() : Collections2.filter(newEvent.getInvitees(), (Predicate)Predicates.not((Predicate)Predicates.in(oldInvitees))), (Function)new InviteeToUserTransformFunction(this.userAccessor)), (Predicate)Predicates.notNull()));
    }

    private void sendEventUpdatedNotification(ConfluenceUser trigger, ConfluenceUser userToNotify, SubCalendarEvent previousSubCalendarEvent, SubCalendarEvent subCalendarEvent) throws MailException, MessagingException, IOException {
        CalendarMailQueueItem item = new CalendarMailQueueItem(this.getRenderedDiffNotification(trigger, userToNotify, previousSubCalendarEvent, subCalendarEvent));
        this.taskManager.addTask("mail", (Task)item);
    }

    private Email getRenderedDiffNotification(ConfluenceUser trigger, ConfluenceUser userToNotify, SubCalendarEvent previousSubCalendarEvent, SubCalendarEvent subCalendarEvent) throws MailException, MessagingException, IOException {
        String triggerDisplayName = this.getUserDisplayName(trigger);
        PersistedSubCalendar previousSubCalendarForUser = this.getChangedSubCalendarForUser(previousSubCalendarEvent, userToNotify);
        PersistedSubCalendar changedSubCalendarForUser = this.getChangedSubCalendarForUser(subCalendarEvent, userToNotify);
        String changedEventType = this.getEventTypeName(subCalendarEvent, userToNotify);
        String titleKey = this.getI18nKeyForEventType(subCalendarEvent.getEventType(), "calendar3.notification.event.updated.title");
        String subtitleKey = this.getI18nKeyForEventType(subCalendarEvent.getEventType(), "calendar3.notification.event.updated.subtitle");
        Email updated = this.createBaseEventNotification(trigger, userToNotify, this.getText(userToNotify, titleKey, triggerDisplayName, changedEventType, previousSubCalendarForUser.getName()), this.getText(userToNotify, subtitleKey, this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), changedEventType, this.getSubCalendarPreviewUrl(changedSubCalendarForUser), HtmlUtil.htmlEncode((String)changedSubCalendarForUser.getName())), null, subCalendarEvent, changedSubCalendarForUser);
        if (null != previousSubCalendarEvent) {
            String previousEventType = this.getEventTypeName(previousSubCalendarEvent, userToNotify);
            Email previous = this.createBaseEventNotification(trigger, userToNotify, this.getText(userToNotify, titleKey, triggerDisplayName, previousEventType, previousSubCalendarForUser.getName()), this.getText(userToNotify, subtitleKey, this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), previousEventType, this.getSubCalendarPreviewUrl(previousSubCalendarForUser), HtmlUtil.htmlEncode((String)previousSubCalendarForUser.getName())), null, previousSubCalendarEvent, changedSubCalendarForUser);
            String diffHtml = this.getHtmlDiff(this.localeManager.getLocale((User)userToNotify), previous.getBody(), updated.getBody());
            if (StringUtils.isNotBlank((CharSequence)diffHtml)) {
                updated.setBody(diffHtml);
            }
        }
        return updated;
    }

    private String getHtmlDiff(Locale userLocale, String previousHtml, String html) throws IOException {
        InputSource previousInput = new InputSource(new StringReader(previousHtml));
        InputSource input = new InputSource(new StringReader(html));
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        try {
            StringWriter diffWriter = new StringWriter();
            SAXTransformerFactory transformerFactory = (SAXTransformerFactory)TransformerFactory.newInstance();
            TransformerHandler diffResultHandler = transformerFactory.newTransformerHandler();
            diffResultHandler.setResult(new StreamResult(diffWriter));
            diffResultHandler.startDocument();
            HTMLDiffer htmlDiffer = new HTMLDiffer(new HtmlSaxDiffOutput(new InlineStylingContentHandler(diffResultHandler), ""));
            htmlDiffer.diff(this.getComparator(previousInput, htmlCleaner, userLocale), this.getComparator(input, htmlCleaner, userLocale));
            diffResultHandler.endDocument();
            return diffWriter.toString();
        }
        catch (TransformerConfigurationException | SAXException xmlError) {
            xmlError.printStackTrace();
            return null;
        }
    }

    private TextNodeComparator getComparator(InputSource previousInput, HtmlCleaner htmlCleaner, Locale userLocale) throws IOException, SAXException {
        DomTreeBuilder previousHtmlBuilder = new DomTreeBuilder();
        htmlCleaner.cleanAndParse(previousInput, previousHtmlBuilder);
        return new TextNodeComparator(previousHtmlBuilder, userLocale);
    }

    @EventListener
    public void notifyEventMovedIntoAnotherSubCalendar(SubCalendarEventMoved eventMoved) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            ConfluenceUser trigger = this.getEventTrigger(eventMoved);
            SubCalendarEvent movedEventBase = eventMoved.getEvent();
            PersistedSubCalendar dstSubCalendar = movedEventBase.getSubCalendar();
            Set<ConfluenceUser> movedBaseMentions = this.getMentions(movedEventBase);
            SubCalendarEvent oldEventBase = eventMoved.getPreviousSubCalendarEvent();
            PersistedSubCalendar srcSubCalendar = oldEventBase.getSubCalendar();
            Set<ConfluenceUser> oldBaseMentions = this.getMentions(oldEventBase);
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> usersToNotify = this.getMailableUsers(movedEventBase, Collections.singletonList(srcSubCalendar), this.mergeUsers(Collections.singletonList(trigger), movedBaseMentions, oldBaseMentions));
            for (ConfluenceUser confluenceUser : usersToNotify) {
                boolean canViewSrcSubCalendar = this.calendarPermissionManager.hasViewEventPrivilege(srcSubCalendar, confluenceUser);
                boolean canViewDstSubCalendar = this.calendarPermissionManager.hasViewEventPrivilege(dstSubCalendar, confluenceUser);
                if (canViewSrcSubCalendar && !canViewDstSubCalendar) {
                    this.sendEventRemovedNotification(trigger, confluenceUser, oldEventBase);
                    continue;
                }
                if (!canViewSrcSubCalendar && canViewDstSubCalendar) {
                    this.sendEventAddedNotification(trigger, confluenceUser, movedEventBase);
                    continue;
                }
                this.sendEventUpdatedNotification(trigger, confluenceUser, oldEventBase, movedEventBase);
            }
            for (ConfluenceUser confluenceUser : oldBaseMentions) {
                if (!this.isUserMailable(trigger, confluenceUser, oldEventBase)) continue;
                if (movedBaseMentions.contains(confluenceUser)) {
                    if (this.isUserMailable(trigger, confluenceUser, movedEventBase)) {
                        this.sendEventUpdatedNotification(trigger, confluenceUser, oldEventBase, movedEventBase);
                        continue;
                    }
                    this.sendEventRemovedNotification(trigger, confluenceUser, oldEventBase);
                    continue;
                }
                this.sendEventRemovedNotification(trigger, confluenceUser, oldEventBase);
            }
            Collection newBaseMentions = Collections2.filter(movedBaseMentions, (Predicate)Predicates.not((Predicate)Predicates.in(oldBaseMentions)));
            for (ConfluenceUser newBaseMention : newBaseMentions) {
                if (!this.isUserMailable(trigger, newBaseMention, movedEventBase)) continue;
                this.sendMentionNotification(trigger, newBaseMention, movedEventBase);
            }
            HashSet<ConfluenceUser> hashSet = new HashSet<ConfluenceUser>(usersToNotify);
            hashSet.addAll(oldBaseMentions);
            hashSet.addAll(newBaseMentions);
            for (SubCalendarEvent oldRescheduledRecurrence : eventMoved.getPreviousSubCalendarEventRescheduledRecurrences()) {
                Set<ConfluenceUser> unnotifiedOldRescheduledRecurrenceMentions;
                Set<ConfluenceUser> oldRescheduledRecurrenceMentions = this.getMentions(oldRescheduledRecurrence);
                Collection<Object> collection = unnotifiedOldRescheduledRecurrenceMentions = null == oldRescheduledRecurrence ? Collections.emptySet() : Collections2.filter(oldRescheduledRecurrenceMentions, (Predicate)Predicates.not((Predicate)Predicates.in(hashSet)));
                if (unnotifiedOldRescheduledRecurrenceMentions.isEmpty()) continue;
                SubCalendarEvent updatedRescheduledRecurrence = eventMoved.getUpdateFor(oldRescheduledRecurrence);
                for (ConfluenceUser userToNotify : unnotifiedOldRescheduledRecurrenceMentions) {
                    if (this.isUserMailable(trigger, userToNotify, oldRescheduledRecurrence)) {
                        if (null == updatedRescheduledRecurrence) {
                            this.sendEventRemovedNotification(trigger, userToNotify, oldRescheduledRecurrence);
                            continue;
                        }
                        if (this.isUserMailable(trigger, userToNotify, updatedRescheduledRecurrence)) {
                            this.sendEventUpdatedNotification(trigger, userToNotify, oldRescheduledRecurrence, updatedRescheduledRecurrence);
                            continue;
                        }
                        this.sendEventRemovedNotification(trigger, userToNotify, oldRescheduledRecurrence);
                        continue;
                    }
                    if (null == updatedRescheduledRecurrence || !this.isUserMailable(trigger, userToNotify, updatedRescheduledRecurrence)) continue;
                    this.sendEventAddedNotification(trigger, userToNotify, updatedRescheduledRecurrence);
                }
            }
            return true;
        }));
    }

    private void sendEventRemovedNotification(ConfluenceUser trigger, ConfluenceUser userToNotify, SubCalendarEvent removedEvent) throws MailException, MessagingException, IOException {
        String triggerDisplayName = this.getUserDisplayName(trigger);
        PersistedSubCalendar changedSubCalendarForUser = this.getChangedSubCalendarForUser(removedEvent, userToNotify);
        String eventTypeName = this.getEventTypeName(removedEvent, userToNotify);
        String titleKey = this.getI18nKeyForEventType(removedEvent.getEventType(), "calendar3.notification.event.removed.title");
        String subTitleKey = this.getI18nKeyForEventType(removedEvent.getEventType(), "calendar3.notification.event.removed.subtitle");
        Email notification = this.createBaseEventNotification(trigger, userToNotify, this.getText(userToNotify, titleKey, triggerDisplayName, eventTypeName, changedSubCalendarForUser.getName()), this.getText(userToNotify, subTitleKey, this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), eventTypeName, this.getSubCalendarPreviewUrl(changedSubCalendarForUser), HtmlUtil.htmlEncode((String)changedSubCalendarForUser.getName())), this.getText(userToNotify, "calendar3.notification.event.removed.warn", new Object[0]), removedEvent, changedSubCalendarForUser);
        CalendarMailQueueItem item = new CalendarMailQueueItem(notification);
        this.taskManager.addTask("mail", (Task)item);
    }

    @EventListener
    public void notifyEventRecurrenceRescheduled(SubCalendarEventRecurrenceRescheduled subCalendarEventRecurrenceRescheduled) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            SubCalendarEvent baseEvent = subCalendarEventRecurrenceRescheduled.getBaseEvent();
            SubCalendarEvent.Repeat baseEventRepeat = baseEvent.getRepeat();
            DateTime baseEventStart = baseEvent.getStartTime();
            DateTime baseEventEnd = baseEvent.getEndTime();
            SubCalendarEvent rescheduledRecurrence = subCalendarEventRecurrenceRescheduled.getEvent();
            baseEvent.setRepeat(null);
            baseEvent.setStartTime(rescheduledRecurrence.getOriginalStartTime());
            baseEvent.setEndTime(new DateTime(baseEvent.getStartTime().getMillis() + (baseEventEnd.getMillis() - baseEventStart.getMillis())));
            try {
                ConfluenceUser trigger = this.getEventTrigger(subCalendarEventRecurrenceRescheduled);
                Set<ConfluenceUser> newMentions = this.getNewInvitees(baseEvent, rescheduledRecurrence);
                Set<ConfluenceUser> existingMentions = this.getExistingInvitees(baseEvent, rescheduledRecurrence);
                Set<ConfluenceUser> removedMentions = this.getUninvitedInvitees(baseEvent, rescheduledRecurrence);
                AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
                Set<ConfluenceUser> usersToNotify = this.getMailableUsers(rescheduledRecurrence, Collections.emptySet(), this.mergeUsers(Collections.singletonList(trigger), newMentions, existingMentions, removedMentions));
                for (ConfluenceUser newMention : newMentions) {
                    if (!this.isUserMailable(trigger, newMention, rescheduledRecurrence)) continue;
                    this.sendMentionNotification(trigger, newMention, rescheduledRecurrence);
                }
                for (ConfluenceUser userToNotify : this.mergeUsers(existingMentions, removedMentions)) {
                    if (!this.isUserMailable(trigger, userToNotify, rescheduledRecurrence)) continue;
                    this.sendEventUpdatedNotification(trigger, userToNotify, baseEvent, rescheduledRecurrence);
                }
            }
            finally {
                baseEvent.setRepeat(baseEventRepeat);
                baseEvent.setEndTime(baseEventEnd);
                baseEvent.setStartTime(baseEventStart);
            }
            return true;
        }));
    }

    @EventListener
    public void notifyEventRecurrenceExcluded(SubCalendarEventExcluded eventExcluded) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            SubCalendarEvent eventBase = eventExcluded.getEvent();
            DateTime recurrence = eventExcluded.getExcludeDate();
            ConfluenceUser trigger = this.getEventTrigger(eventExcluded);
            DateTime originalStart = eventBase.getStartTime();
            DateTime originalEnd = eventBase.getEndTime();
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> mentions = this.getMentions(eventBase);
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            Set<ConfluenceUser> usersToNotify = this.getMailableUsers(eventBase, Collections.emptySet(), this.mergeUsers(Collections.singletonList(trigger), mentions));
            for (ConfluenceUser mention : mentions) {
                if (!this.isUserMailable(trigger, mention, eventBase)) continue;
                usersToNotify.add(mention);
            }
            for (ConfluenceUser userToNotify : usersToNotify) {
                try {
                    if (eventBase.isAllDay()) {
                        eventBase.setStartTime(recurrence.withZone(DateTimeZone.UTC));
                        eventBase.setEndTime(new DateTime(eventBase.getStartTime().getMillis() + (originalEnd.getMillis() - originalStart.getMillis()), eventBase.getStartTime().getZone()));
                    } else {
                        eventBase.setStartTime(recurrence);
                        eventBase.setEndTime(new DateTime(recurrence.getMillis() + (originalEnd.getMillis() - originalStart.getMillis()), recurrence.getZone()));
                    }
                    this.sendEventRemovedNotification(trigger, userToNotify, eventBase);
                }
                finally {
                    eventBase.setStartTime(originalStart);
                    eventBase.setEndTime(originalEnd);
                }
            }
            return true;
        }));
    }

    @EventListener
    public void notifyEventDeleted(SubCalendarEventRemoved eventRemoved) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            ConfluenceUser trigger = this.getEventTrigger(eventRemoved);
            SubCalendarEvent baseRemoved = eventRemoved.getEvent();
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            HashSet<ConfluenceUser> usersAlreadyNotified = new HashSet<ConfluenceUser>();
            AuthenticatedUserThreadLocal.set((ConfluenceUser)trigger);
            for (SubCalendarEvent rescheduledRecurrenceRemoved : eventRemoved.getRescheduledRecurrences()) {
                Set<ConfluenceUser> mentions = this.getMentions(rescheduledRecurrenceRemoved);
                if (null == mentions) continue;
                for (ConfluenceUser mentioned : mentions) {
                    if (!this.isUserMailable(trigger, mentioned, rescheduledRecurrenceRemoved)) continue;
                    this.sendEventRemovedNotification(trigger, mentioned, rescheduledRecurrenceRemoved);
                }
                usersAlreadyNotified.addAll(mentions);
            }
            usersAlreadyNotified.add(trigger);
            if (null == baseRemoved) {
                for (SubCalendarEvent rescheduledRecurrenceRemoved : eventRemoved.getRescheduledRecurrences()) {
                    for (ConfluenceUser userToNotify : this.getMailableUsers(rescheduledRecurrenceRemoved, Collections.emptySet(), usersAlreadyNotified)) {
                        this.sendEventRemovedNotification(trigger, userToNotify, rescheduledRecurrenceRemoved);
                    }
                }
            } else {
                Set<ConfluenceUser> baseMentions = this.getMentions(baseRemoved);
                if (null != baseMentions) {
                    usersAlreadyNotified.addAll(baseMentions);
                }
                for (ConfluenceUser userToNotify : this.getMailableUsers(baseRemoved, Collections.emptySet(), usersAlreadyNotified)) {
                    this.sendEventRemovedNotification(trigger, userToNotify, baseRemoved);
                }
                for (ConfluenceUser baseMention : baseMentions) {
                    if (!this.isUserMailable(trigger, baseMention, baseRemoved)) continue;
                    this.sendEventRemovedNotification(trigger, baseMention, baseRemoved);
                }
            }
            return true;
        }));
    }

    private void doNotifyInTransaction(Callable<Void> notificationAction) throws MailException {
        MailException notificationException = (MailException)((Object)this.transactionTemplate.execute(() -> {
            MailException notificationException1 = null;
            try {
                LOG.info("do notification on thread :" + Thread.currentThread().getName());
                if (this.canSendEmail()) {
                    notificationAction.call();
                } else {
                    LOG.debug("No SMTP server configured for notification");
                }
            }
            catch (Exception anError) {
                notificationException1 = new MailException((Throwable)anError);
            }
            return notificationException1;
        }));
        if (notificationException != null) {
            throw notificationException;
        }
    }

    @EventListener
    public void notifySubCalendarDeleted(SubCalendarRemoved subCalendarRemoved) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(new ConditionalCallable<Boolean>(() -> this.canSendEmail(), () -> {
            if (this.canSendEmail()) {
                Object subCalendar = subCalendarRemoved.getSubCalendar();
                if (!(subCalendar instanceof SubscribingSubCalendar)) {
                    try {
                        Calendar subCalendarData = subCalendarRemoved.getSubCalendarData();
                        this.sendSubCalendarDeletedNotification(this.getEventTrigger(subCalendarRemoved), (PersistedSubCalendar)subCalendar, subCalendarRemoved.getSubscriptions(), subCalendarRemoved.getSubscribers(), subCalendarData);
                    }
                    catch (ValidationException invalidIcs) {
                        throw new MailException(String.format("The contents of sub-calendar %s isn't valid to iCalendar standards", ((PersistedSubCalendar)subCalendar).getId()), (Throwable)invalidIcs);
                    }
                    catch (IOException unableToAttach) {
                        throw new MailException(String.format("Unable to attach the iCalendar export of sub-calendar %s to email", ((PersistedSubCalendar)subCalendar).getId()), (Throwable)unableToAttach);
                    }
                    catch (MessagingException emailProblem) {
                        throw new MailException((Throwable)emailProblem);
                    }
                }
            } else {
                LOG.debug("No SMTP server configured for notification");
            }
            return true;
        }));
    }

    private void sendSubCalendarDeletedNotification(ConfluenceUser trigger, PersistedSubCalendar subCalendar, Collection<PersistedSubCalendar> subscriptions, Set<String> subscribers, Calendar subCalendarContent) throws ValidationException, IOException, MessagingException, MailException {
        byte[] subCalendarContentBytes = this.isSubCalendarContentAttachable(subCalendar) ? this.getSubCalendarContentAsBytes(subCalendarContent) : null;
        String triggerDisplayName = this.getUserDisplayName(trigger);
        for (ConfluenceUser userToNotify : this.getMailableUsers(subCalendar, subscribers)) {
            Collection userToNotifySubscriptions = Collections2.filter(subscriptions, subscription -> StringUtils.equals((CharSequence)subscription.getCreator(), (CharSequence)userToNotify.getKey().toString()));
            PersistedSubCalendar deletedSubCalendarForUser = userToNotifySubscriptions.isEmpty() ? subCalendar : (PersistedSubCalendar)userToNotifySubscriptions.iterator().next();
            Email subCalendarDeletedNotification = this.createBaseSubCalendarNotification(trigger, userToNotify, this.getText(userToNotify, "calendar3.notification.subcalendar.deleted.title", triggerDisplayName, deletedSubCalendarForUser.getName()), this.getText(userToNotify, "calendar3.notification.subcalendar.deleted.subtitle", this.getUserProfileUrl(trigger), HtmlUtil.htmlEncode((String)triggerDisplayName), HtmlUtil.htmlEncode((String)deletedSubCalendarForUser.getName())), subCalendar, deletedSubCalendarForUser);
            if (null != subCalendarContentBytes) {
                subCalendarDeletedNotification.getMultipart().addBodyPart((BodyPart)this.createIcsBodyPart(subCalendar.getId(), subCalendarContentBytes));
            }
            CalendarMailQueueItem item = new CalendarMailQueueItem(subCalendarDeletedNotification);
            this.taskManager.addTask("mail", (Task)item);
        }
    }

    private Email createBaseSubCalendarNotification(ConfluenceUser fromUser, ConfluenceUser toUser, String title, String subTitleHtml, PersistedSubCalendar deletedSourceSubCalendar, PersistedSubCalendar changedSubCalendarForUser) throws MailException, MessagingException, IOException {
        Email notification = new Email(toUser.getEmail());
        notification.setSubject(title);
        notification.setFrom(this.getDefaultSMTPServer().getDefaultFrom());
        notification.setFromName(this.getFromName(fromUser));
        notification.setMimeType("text/html");
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        I18NBean i18nBean = this.getI18NBean(toUser);
        velocityContext.put("i18nBean", i18nBean);
        velocityContext.put("i18n", i18nBean);
        velocityContext.put("subTitleHtml", subTitleHtml);
        velocityContext.put("recoverable", deletedSourceSubCalendar instanceof LocallyManagedSubCalendar);
        velocityContext.put("subCalendar", deletedSourceSubCalendar);
        velocityContext.put("deletedSubCalendarFoUser", changedSubCalendarForUser);
        notification.setMultipart((Multipart)new MimeMultipart("related"));
        MimeBodyPart avatarBodyPart = new MimeBodyPart();
        IdentifiableContentDataHandler senderIconDataHandler = this.createAvatarDataHandler(fromUser);
        avatarBodyPart.setDataHandler((DataHandler)senderIconDataHandler);
        avatarBodyPart.setHeader("Content-ID", "<" + senderIconDataHandler.getContentId() + ">");
        avatarBodyPart.setFileName(senderIconDataHandler.getFileName());
        avatarBodyPart.setDisposition("inline");
        notification.getMultipart().addBodyPart((BodyPart)avatarBodyPart);
        velocityContext.put("senderIconCid", senderIconDataHandler.getContentId());
        MimeBodyPart warningIconBodyPart = new MimeBodyPart();
        warningIconBodyPart.setDataHandler(new DataHandler(this.dataSourceFactory.getServletContainerResource("/images/icons/emoticons/forbidden.png", "forbidden.png")));
        warningIconBodyPart.setHeader("Content-ID", "<warningicon>");
        warningIconBodyPart.setFileName("warning.png");
        warningIconBodyPart.setDisposition("inline");
        notification.getMultipart().addBodyPart((BodyPart)warningIconBodyPart);
        notification.setBody(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/subcalendar-notification.vm", velocityContext));
        return notification;
    }

    private MimeBodyPart createIcsBodyPart(String subCalendarName, byte[] icsBytes) throws MessagingException {
        MimeBodyPart icsPart = new MimeBodyPart();
        icsPart.setDataHandler(new DataHandler((DataSource)new ByteArrayDataSource(icsBytes, "application/ics")));
        icsPart.setHeader("Content-Transfer-Encoding", "base64");
        icsPart.setFileName(subCalendarName + ".ics");
        icsPart.setDisposition("attachment");
        return icsPart;
    }

    @EventListener
    public void notifyWaitingAttendantPromoted(WaitingAttendantPromoted waitingAttendantPromotedEvent) throws MailException {
        this.calendarAsyncHelper.doAsyncWithTransaction(() -> {
            if (this.canSendEmail()) {
                this.notifyPromotedResponders(waitingAttendantPromotedEvent.getEventTitle(), waitingAttendantPromotedEvent.getEventUrlPath(), waitingAttendantPromotedEvent.getPromotedReplies());
            } else {
                LOG.debug("No SMTP server configured for notification");
            }
            return true;
        });
    }

    private void notifyPromotedResponders(String eventTitle, String urlPath, List<Reply> promotedReplies) throws MailException {
        String from = this.getDefaultSMTPServer().getDefaultFrom();
        String pageUrl = this.settingsManager.getGlobalSettings().getBaseUrl() + urlPath;
        for (Reply reply : promotedReplies) {
            String emailStr = reply.getEmail();
            Email email = new Email(emailStr);
            String subject = this.getText(this.getUserByEmail(emailStr), "calendar3.event.notification.promoted.title", eventTitle);
            email.setSubject(subject);
            email.setFrom(from);
            email.setMimeType("text/html");
            Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
            contextMap.put("title", subject);
            contextMap.put("eventTitle", eventTitle);
            contextMap.put("pageUrl", pageUrl);
            email.setBody(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/eventmacro/templates/notificationemail.vm", contextMap));
            CalendarMailQueueItem item = new CalendarMailQueueItem(email);
            this.taskManager.addTask("mail", (Task)item);
        }
    }

    @Nullable
    private ConfluenceUser getUserByEmail(String emailStr) {
        SearchResult usersByEmail = this.userAccessor.getUsersByEmail(emailStr);
        User user = (User)Iterators.getNext((Iterator)usersByEmail.pager().iterator(), null);
        return user == null ? null : FindUserHelper.getUser((User)user);
    }

    private Set<ConfluenceUser> getMailableUsers(PersistedSubCalendar deletedSubCalendar, Collection<String> subscribers) {
        HashSet<ConfluenceUser> mailableUsers = new HashSet<ConfluenceUser>(Collections2.filter((Collection)Collections2.transform(subscribers, subscriberId -> this.userAccessor.getUserByKey(new UserKey(subscriberId))), (Predicate)Predicates.and((Predicate)Predicates.notNull(), this::isUserActiveWithEmail)));
        ConfluenceUser deletedSubCalendarCreator = this.userAccessor.getUserByKey(new UserKey(StringUtils.defaultString((String)deletedSubCalendar.getCreator())));
        if (null != deletedSubCalendarCreator && this.isUserActiveWithEmail(deletedSubCalendarCreator)) {
            mailableUsers.add(deletedSubCalendarCreator);
        }
        return mailableUsers;
    }

    private byte[] getSubCalendarContentAsBytes(Calendar subCalendarContent) throws ValidationException, IOException {
        if (null == subCalendarContent) {
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();){
            Ical4jIoUtil.newCalendarOutputter().output(subCalendarContent, outputStream);
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
    }

    private String getI18nKeyForEventType(String changedEventType, String baseI18nKey) {
        if ("other".equals(changedEventType)) {
            return baseI18nKey + ".for.events";
        }
        return baseI18nKey;
    }

    private boolean isSubCalendarContentAttachable(PersistedSubCalendar subCalendar) {
        return subCalendar instanceof LocallyManagedSubCalendar;
    }

    private static class InviteeToUserTransformFunction
    implements Function<Invitee, ConfluenceUser> {
        private final UserAccessor userAccessor;

        private InviteeToUserTransformFunction(UserAccessor userAccessor) {
            this.userAccessor = userAccessor;
        }

        public ConfluenceUser apply(Invitee invitee) {
            return this.userAccessor.getUserByKey(new UserKey(invitee.getId()));
        }
    }

    public static class IdentifiableContentDataHandler
    extends DataHandler {
        private final String contentId;
        private final String fileName;

        public IdentifiableContentDataHandler(DataSource dataSource, String contentId, String fileName) {
            super(dataSource);
            this.contentId = contentId;
            this.fileName = fileName;
        }

        public String getContentId() {
            return this.contentId;
        }

        public String getFileName() {
            return this.fileName;
        }
    }

    public static interface SubCalendarWatchGroup {
        public Space getSpace();

        public String getUrlPath();

        public AbstractPage getPage();

        public String getTitle();

        public String getIconId();

        public DataHandler getIconDataSource() throws IOException;

        public String getIconFileName();

        public boolean isCompareSpaceOnly();
    }

    private static class SubCalendarWatchGroupFactory {
        private final DataSourceFactory dataSourceFactory;
        private final NotificationManager notificationManager;

        private SubCalendarWatchGroupFactory(DataSourceFactory dataSourceFactory, NotificationManager notificationManager) {
            this.dataSourceFactory = dataSourceFactory;
            this.notificationManager = notificationManager;
        }

        public SubCalendarWatchGroup getWatchGroup(AbstractPage abstractPage) {
            return new ByPage(this.dataSourceFactory, abstractPage);
        }

        public SubCalendarWatchGroup getWatchGroup(ConfluenceUser user, Space space) {
            if (this.notificationManager.getNotificationByUserAndSpace((User)user, space) != null) {
                return new BySpace(space);
            }
            return null;
        }

        private static class ByPage
        implements SubCalendarWatchGroup {
            private final AbstractPage page;
            private final DataSourceFactory dataSourceFactory;

            private ByPage(DataSourceFactory dataSourceFactory, AbstractPage page) {
                this.dataSourceFactory = dataSourceFactory;
                this.page = page;
            }

            @Override
            public Space getSpace() {
                return this.getPage().getSpace();
            }

            @Override
            public AbstractPage getPage() {
                return this.page;
            }

            @Override
            public String getUrlPath() {
                return this.getPage().getUrlPath();
            }

            @Override
            public String getTitle() {
                return this.getPage().getTitle();
            }

            @Override
            public String getIconId() {
                return String.format("page-%s", DigestUtils.md5Hex(this.getPage().getIdAsString()));
            }

            @Override
            public DataHandler getIconDataSource() throws IOException {
                return new DataHandler(this.dataSourceFactory.getServletContainerResource(PAGE_ICON_PATH, "page_16.png"));
            }

            @Override
            public String getIconFileName() {
                return "page_16.png";
            }

            @Override
            public boolean isCompareSpaceOnly() {
                return false;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                ByPage byPage = (ByPage)o;
                return this.page.equals((Object)byPage.page);
            }

            public int hashCode() {
                return this.page.hashCode();
            }
        }

        private static class BySpace
        implements SubCalendarWatchGroup {
            Space space;

            private BySpace(Space space) {
                this.space = space;
            }

            @Override
            public Space getSpace() {
                return this.space;
            }

            @Override
            public AbstractPage getPage() {
                return null;
            }

            @Override
            public String getUrlPath() {
                return this.getSpace().getUrlPath();
            }

            @Override
            public String getTitle() {
                return this.getSpace().getName();
            }

            @Override
            public String getIconId() {
                return String.format("space-%s", DigestUtils.md5Hex(this.getSpace().getKey()));
            }

            @Override
            public DataHandler getIconDataSource() throws IOException {
                try (InputStream iconInput = this.getClass().getClassLoader().getResourceAsStream("com/atlassian/confluence/extra/calendar3/img/space_icon.png");){
                    DataHandler dataHandler = new DataHandler((DataSource)new ByteArrayDataSource(iconInput, DefaultCalendarNotificationManager.MIME_TYPE_IMAGE_PNG));
                    return dataHandler;
                }
            }

            @Override
            public String getIconFileName() {
                return "space_icon.png";
            }

            @Override
            public boolean isCompareSpaceOnly() {
                return true;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                BySpace bySpace = (BySpace)o;
                return this.getSpace().equals((Object)bySpace.getSpace());
            }

            public int hashCode() {
                return this.getSpace().hashCode();
            }
        }
    }

    private static class InlineStylingContentHandler
    implements ContentHandler {
        private final ContentHandler delegate;

        private InlineStylingContentHandler(ContentHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.delegate.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            this.delegate.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            this.delegate.endDocument();
        }

        @Override
        public void startPrefixMapping(String s, String s1) throws SAXException {
            this.delegate.startPrefixMapping(s, s1);
        }

        @Override
        public void endPrefixMapping(String s) throws SAXException {
            this.delegate.endPrefixMapping(s);
        }

        @Override
        public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
            if (null != attributes) {
                if (this.hasAttributeContainingValue(attributes, "class", "diff-html-removed")) {
                    attributes = this.appendInlineStyles(attributes, "background-color: #ffe7e7; text-decoration: line-through;");
                } else if (this.hasAttributeContainingValue(attributes, "class", "diff-html-added")) {
                    attributes = this.appendInlineStyles(attributes, "background-color: #ddfade;");
                } else if (this.hasAttributeContainingValue(attributes, "changeType", "diff-removed-image")) {
                    attributes = this.appendInlineStyles(attributes, "display: none;");
                }
            }
            this.delegate.startElement(s, s1, s2, attributes);
        }

        private boolean hasAttributeContainingValue(Attributes attributes, String qName, String value) {
            return this.hasAttribute(attributes, qName) && StringUtils.contains((CharSequence)attributes.getValue(qName), (CharSequence)value);
        }

        private boolean hasAttribute(Attributes attributes, String qName) {
            return this.getIndexOfAttribute(attributes, qName) >= 0;
        }

        private int getIndexOfAttribute(Attributes attributes, String qName) {
            int j = attributes.getLength();
            for (int i = 0; i < j; ++i) {
                if (!StringUtils.equals((CharSequence)qName, (CharSequence)attributes.getQName(i))) continue;
                return i;
            }
            return -1;
        }

        private Attributes appendInlineStyles(Attributes attributes, String newStyles) {
            AttributesImpl newAttributes = new AttributesImpl(attributes);
            StringBuilder newStyleValueBuilder = new StringBuilder(StringUtils.trim((String)StringUtils.defaultString((String)this.getAttributeValue(attributes, "style"))));
            if (this.hasAttribute(attributes, "style")) {
                newAttributes.removeAttribute(this.getIndexOfAttribute(attributes, "style"));
            }
            if (newStyleValueBuilder.length() > 0) {
                newStyleValueBuilder.append("; ");
            }
            newAttributes.addAttribute("", "style", "style", "CDATA", newStyleValueBuilder.append(newStyles).toString());
            return newAttributes;
        }

        private String getAttributeValue(Attributes attributes, String qName) {
            int j = attributes.getLength();
            for (int i = 0; i < j; ++i) {
                if (!StringUtils.equals((CharSequence)qName, (CharSequence)attributes.getQName(i))) continue;
                return attributes.getValue(i);
            }
            return null;
        }

        @Override
        public void endElement(String s, String s1, String s2) throws SAXException {
            this.delegate.endElement(s, s1, s2);
        }

        @Override
        public void characters(char[] chars, int i, int i1) throws SAXException {
            this.delegate.characters(chars, i, i1);
        }

        @Override
        public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {
            this.delegate.ignorableWhitespace(chars, i, i1);
        }

        @Override
        public void processingInstruction(String s, String s1) throws SAXException {
            this.delegate.processingInstruction(s, s1);
        }

        @Override
        public void skippedEntity(String s) throws SAXException {
            this.delegate.skippedEntity(s);
        }
    }

    public static class ProfileImageDataSource
    implements DataSource {
        private final DataSource delegate;

        public ProfileImageDataSource(DataSource delegate) {
            this.delegate = delegate;
        }

        public InputStream getInputStream() throws IOException {
            return this.delegate.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            return this.delegate.getOutputStream();
        }

        public String getContentType() {
            return this.delegate.getContentType();
        }

        public String getName() {
            return "avatar";
        }
    }
}

