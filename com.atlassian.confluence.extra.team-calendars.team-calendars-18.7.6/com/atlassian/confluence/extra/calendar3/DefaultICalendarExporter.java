/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.DefaultCalendarManager;
import com.atlassian.confluence.extra.calendar3.ICalendarExporter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractChildJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarExportICS;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarExportTransformer;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.XComponent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultICalendarExporter
implements ICalendarExporter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultICalendarExporter.class);
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final CalendarManager calendarManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final JiraAccessor jiraAccessor;
    private final EventPublisher eventPublisher;
    private final UidGenerator uidGenerator;
    private final CalendarHelper calendarHelper;

    @Autowired
    public DefaultICalendarExporter(@ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, CalendarManager calendarManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, JiraAccessor jiraAccessor, @ComponentImport EventPublisher eventPublisher, CalendarHelper calendarHelper) {
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.calendarManager = calendarManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.jiraAccessor = jiraAccessor;
        this.eventPublisher = eventPublisher;
        this.uidGenerator = new FixedUidGenerator(new DefaultCalendarManager.BaseUrlHostInfo(this.settingsManager), String.valueOf(new Random().nextInt()));
        this.calendarHelper = calendarHelper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void export(ConfluenceUser forUser, PersistedSubCalendar persistedSubCalendar, OutputStream outputStream, boolean isSubscribe) throws Exception {
        String methodSignature = "DefaultICalendarExporter.export(PersistedSubCalendar, OutputStream)";
        UtilTimerStack.push((String)"DefaultICalendarExporter.export(PersistedSubCalendar, OutputStream)");
        ConfluenceUser originalRemoteUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)forUser);
            Calendar subCalendarContent = this.getTransformedSubCalendarContent(persistedSubCalendar);
            if (persistedSubCalendar.getChildSubCalendarIds() != null) {
                Collection<PersistedSubCalendar> childSubCalendars = this.getChildSubCalendars(persistedSubCalendar);
                ComponentList<CalendarComponent> subCalendarContentComponents = subCalendarContent.getComponents();
                this.exportLocallyManagedSubCalendars(subCalendarContentComponents, childSubCalendars);
                DefaultICalendarExporter.exportExternallySourcedSubCalendars(subCalendarContentComponents, childSubCalendars);
                if (isSubscribe) {
                    this.exportJiraManageSubCalendars(subCalendarContentComponents, childSubCalendars);
                }
                this.outputCalendar(subCalendarContent, outputStream);
            }
            this.eventPublisher.publish((Object)new SubCalendarExportICS(this, forUser));
        }
        finally {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)originalRemoteUser);
            UtilTimerStack.pop((String)"DefaultICalendarExporter.export(PersistedSubCalendar, OutputStream)");
        }
    }

    public static void exportExternallySourcedSubCalendars(List<CalendarComponent> subCalendarContentComponents, Collection<PersistedSubCalendar> childSubCalendars) {
        logger.debug("Exporting externally sourced SubCalendars");
        childSubCalendars.stream().filter(childSubCalendar -> !(childSubCalendar instanceof LocallyManagedSubCalendar)).forEach(childSubCalendar -> {
            block3: {
                try {
                    XComponent eventSeriesComponent = new XComponent("X-EVENT-SERIES", new PropertyList());
                    PropertyList<Property> eventSeriesProperties = eventSeriesComponent.getProperties();
                    eventSeriesProperties.add(new Summary(childSubCalendar.getName()));
                    eventSeriesProperties.add(new Description(StringUtils.defaultString(childSubCalendar.getDescription())));
                    eventSeriesProperties.add(new XProperty("X-CONFLUENCE-SUBCALENDAR-TYPE", childSubCalendar.getType()));
                    String childSubCalendarSourceLocation = StringUtils.defaultString(childSubCalendar.getSourceLocation());
                    if (childSubCalendarSourceLocation.isEmpty()) {
                        logger.warn("Child subcalendar with name {} and id {} has null source location", (Object)childSubCalendar.getName(), (Object)childSubCalendar.getId());
                    }
                    eventSeriesProperties.add(new Url(URI.create(childSubCalendarSourceLocation)));
                    subCalendarContentComponents.add(eventSeriesComponent);
                }
                catch (Exception e) {
                    logger.info("Couldn't export external child subcalendar {}. Please enable DEBUG logging for details.", (Object)childSubCalendar.getName());
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Failure caused by exception:", (Throwable)e);
                }
            }
        });
    }

    private void exportJiraManageSubCalendars(List<CalendarComponent> subCalendarContentComponents, Collection<PersistedSubCalendar> childSubCalendars) throws Exception {
        logger.debug("Exporting Jira SubCalendars");
        childSubCalendars.stream().filter(childSubCalendar -> childSubCalendar instanceof AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar).forEach(childSubCalendar -> {
            block17: {
                try {
                    ApplicationLink applicationLink;
                    Calendar childSubCalendarContent = this.getTransformedSubCalendarContent((PersistedSubCalendar)childSubCalendar);
                    String displayUrl = "";
                    String applicationId = ((AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar)childSubCalendar).getApplicationId();
                    if (StringUtils.isNotEmpty(applicationId) && (applicationLink = this.jiraAccessor.getLinkedJiraInstance(applicationId)) != null) {
                        displayUrl = applicationLink.getDisplayUrl().toString();
                    }
                    for (CalendarComponent vEventObj : childSubCalendarContent.getComponents("VEVENT")) {
                        PropertyList<Property> propertyList = vEventObj.getProperties();
                        String keyIssue = vEventObj.getProperty("X-JIRA-ISSUE-KEY") != null ? ((Content)vEventObj.getProperty("X-JIRA-ISSUE-KEY")).getValue() : "";
                        String statusIssue = vEventObj.getProperty("X-JIRA-STATUS") != null ? ((Content)vEventObj.getProperty("X-JIRA-STATUS")).getValue() : "";
                        String assignee = vEventObj.getProperty("X-JIRA-ASSIGNEE") != null ? ((Content)vEventObj.getProperty("X-JIRA-ASSIGNEE")).getValue() : "";
                        String url = null;
                        StringBuilder stringBuilder = new StringBuilder();
                        Object oldSummary = vEventObj.getProperty("SUMMARY");
                        Object newSummary = ((Content)oldSummary).getValue();
                        if (CalendarUtil.isJiraVersion((VEvent)vEventObj)) {
                            if (vEventObj.getProperty("X-JIRA-PROJECT") != null) {
                                newSummary = ((Content)vEventObj.getProperty("X-JIRA-PROJECT")).getValue() + " - " + (String)newSummary;
                            }
                            url = CalendarUtil.getVersionUrl(stringBuilder, (VEvent)vEventObj, displayUrl);
                        } else if (CalendarUtil.isGreenHopperSprint((VEvent)vEventObj)) {
                            String sprintViewBoardUrl;
                            Object sprintHomePageUrlProperty;
                            if (vEventObj.getProperty("X-JIRA-PROJECT") != null) {
                                newSummary = ((Content)vEventObj.getProperty("X-JIRA-PROJECT")).getValue() + " - " + (String)newSummary;
                            }
                            if (null != (sprintHomePageUrlProperty = vEventObj.getProperty("X-GREENHOPPER-SPRINT-HOMEPAGE-URL"))) {
                                String sprintHomePageUrl = ((Content)sprintHomePageUrlProperty).getValue();
                                if (StringUtils.isNotBlank(sprintHomePageUrl)) {
                                    url = sprintHomePageUrl;
                                }
                            } else if (vEventObj.getProperty("X-GREENHOPPER-SPRINT-VIEWBOARDS-URL") != null && StringUtils.isNotBlank(sprintViewBoardUrl = ((Content)vEventObj.getProperty("X-GREENHOPPER-SPRINT-VIEWBOARDS-URL")).getValue())) {
                                url = sprintViewBoardUrl;
                            }
                        } else {
                            if (StringUtils.isNotEmpty(keyIssue) && !StringUtils.startsWith((String)newSummary, keyIssue)) {
                                newSummary = keyIssue + " - " + (String)newSummary;
                            }
                            url = CalendarUtil.getIssueUrl(stringBuilder, (VEvent)vEventObj, displayUrl);
                        }
                        StringBuffer description = new StringBuffer().append((String)(StringUtils.isNotEmpty(assignee) ? this.getText("calendar3.eventdetails.assignee", null) + ": " + assignee + ".\n" : assignee)).append((String)(StringUtils.isNotEmpty(statusIssue) ? this.getText("calendar3.eventdetails.status", null) + ": " + statusIssue + ".\n\n" : statusIssue));
                        Object oldDescription = vEventObj.getProperty("DESCRIPTION");
                        if (oldDescription != null) {
                            propertyList.remove((Property)oldDescription);
                        }
                        if (StringUtils.isNotEmpty(url)) {
                            description = description.append(url);
                        }
                        propertyList.add(new Description(description.toString()));
                        propertyList.remove((Property)oldSummary);
                        propertyList.add(new Summary((String)newSummary));
                        subCalendarContentComponents.add(vEventObj);
                    }
                }
                catch (Exception e) {
                    logger.info("Couldn't export Jira child subcalendar {}. Please enable DEBUG logging for details.", (Object)childSubCalendar.getName());
                    if (!logger.isDebugEnabled()) break block17;
                    logger.debug("Failure caused by exception:", (Throwable)e);
                }
            }
        });
    }

    private void exportLocallyManagedSubCalendars(List<CalendarComponent> subCalendarContentComponents, Collection<PersistedSubCalendar> childSubCalendars) throws Exception {
        logger.debug("Exporting locally managed SubCalendars");
        childSubCalendars.stream().filter(cal -> cal instanceof LocallyManagedSubCalendar).forEach(childSubCalendar -> {
            block3: {
                try {
                    Calendar childSubCalendarContent = this.getTransformedSubCalendarContent((PersistedSubCalendar)childSubCalendar);
                    for (CalendarComponent vEventObj : childSubCalendarContent.getComponents("VEVENT")) {
                        subCalendarContentComponents.add(vEventObj);
                    }
                }
                catch (Exception e) {
                    logger.info("Couldn't export local child subcalendar {}. Please enable DEBUG logging for details.", (Object)childSubCalendar.getName());
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Failure caused by exception:", (Throwable)e);
                }
            }
        });
    }

    private Collection<PersistedSubCalendar> getChildSubCalendars(PersistedSubCalendar persistedSubCalendar) {
        return Collections2.filter((Collection)Collections2.transform(persistedSubCalendar.getChildSubCalendarIds(), childSubCalendarId -> {
            PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar((String)childSubCalendarId);
            if (subCalendar instanceof SubscribingSubCalendar) {
                return this.calendarManager.getSubCalendar(((SubscribingSubCalendar)subCalendar).getSubscriptionId());
            }
            return subCalendar;
        }), (Predicate)Predicates.notNull());
    }

    private void outputCalendar(Calendar subCalendarContent, OutputStream outputStream) throws IOException, ValidationException {
        Ical4jIoUtil.newCalendarOutputter().output(subCalendarContent, outputStream);
    }

    private Calendar getTransformedSubCalendarContent(PersistedSubCalendar persistedSubCalendar) throws Exception {
        return new CalendarExportTransformer(this.settingsManager, this.userAccessor, persistedSubCalendar, this.jodaIcal4jTimeZoneMapper, this.uidGenerator, this.calendarHelper).transform(this.calendarManager.getSubCalendarContent(persistedSubCalendar));
    }

    private String getText(String i18nKey, List substitutions) {
        return this.getI18nBean().getText(i18nKey, substitutions);
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale());
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}

