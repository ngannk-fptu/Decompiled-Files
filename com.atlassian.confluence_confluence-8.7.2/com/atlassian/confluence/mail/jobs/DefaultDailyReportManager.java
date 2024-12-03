/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.jobs;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.links.SimpleLink;
import com.atlassian.confluence.mail.ChangeDigestNotificationBean;
import com.atlassian.confluence.mail.jobs.DailyReportManager;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDailyReportManager
implements DailyReportManager {
    public static final String TEMPLATE_NAME = "changedigest-notification.vm";
    private static final Logger log = LoggerFactory.getLogger(DefaultDailyReportManager.class);
    private static final String TEMPLATE_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-daily-summary-email";
    private static final String TEMPLATE_MODULE_KEY = "chrome-template";
    private static final String TEMPLATE_LOCATION = "com.atlassian.confluence.plugins.confluence-daily-summary-email:chrome-template";
    private final MultiQueueTaskManager taskManager;
    private final ChangeDigestNotificationBean changeNotificationBean;
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final GlobalSettingsManager settingsManager;
    private final DataSourceFactory dataSourceFactory;
    private final ContentUiSupport<ContentEntityObject> contentUiSupport;
    private final I18NBeanFactory i18NBeanFactory;

    public DefaultDailyReportManager(MultiQueueTaskManager taskManager, ChangeDigestNotificationBean changeNotificationBean, UserPreferencesAccessor userPreferencesAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager, GlobalSettingsManager settingsManager, DataSourceFactory dataSourceFactory, ContentUiSupport<ContentEntityObject> contentUiSupport, I18NBeanFactory i18NBeanFactory) {
        this.taskManager = taskManager;
        this.changeNotificationBean = changeNotificationBean;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.settingsManager = settingsManager;
        this.dataSourceFactory = dataSourceFactory;
        this.contentUiSupport = contentUiSupport;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public void generateDailyReports() {
        Date date = DateUtils.addDays((Date)new Date(), (int)-1);
        String subject = GeneralUtil.getI18n().getText("daily.report.title");
        List<ChangeDigestReport> reports = this.changeNotificationBean.getAllChangeReports(date);
        if (reports.size() > 0) {
            for (ChangeDigestReport report : reports) {
                this.queueDailyReportEmail(subject, report);
            }
        } else {
            log.info("Nobody awaiting notifications");
        }
    }

    private void queueDailyReportEmail(String subject, ChangeDigestReport report) {
        try {
            if (StringUtils.isBlank((CharSequence)report.getUser().getEmail())) {
                log.warn("Not sending notification email [{}] to [{}]:  No email set", (Object)subject, (Object)report.getUser().getFullName());
                return;
            }
            DateFormatter dateFormatter = this.getDateFormatter(report.getUser());
            PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with(report.getUser(), this.templateName(), subject).andSender(this.getFromUser(report.getUser())).andTemplateLocation(this.templateLocation()).andContextEntry("report", report).andContextEntry("dateFormatter", dateFormatter).andContextEntry("date", dateFormatter.format(new Date())).andContextEntry("footerLinks", this.makeFooterLinks()).andContextEntry("siteTitle", this.settingsManager.getGlobalSettings().getSiteTitle());
            builder.andRelatedBodyParts(this.computePageTypeDatasources(report));
            if (!report.getChangedPersonalInformation().isEmpty()) {
                builder.andRelatedBodyPart(this.attachProfileImage());
            }
            builder.andRelatedBodyParts(this.footerImages());
            if (log.isDebugEnabled()) {
                log.debug("Adding daily report email for user: {} onto the queue.", (Object)report.getUser().getEmail());
            }
            this.taskManager.addTask("mail", (Task)builder.render());
        }
        catch (Exception e) {
            if (report != null) {
                log.error("Failed to create daily report for user " + report.getUser().getName(), (Throwable)e);
            }
            log.error("Error creating daily report", (Throwable)e);
        }
    }

    private User getFromUser(User recipient) {
        String instanceName = this.settingsManager.getGlobalSettings().getSiteTitle();
        String fromSuffix = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(recipient)).getText("daily.report.sender");
        String from = "Confluence".equals(instanceName) ? fromSuffix : String.format("%s %s", instanceName, fromSuffix);
        if (!StringUtils.isBlank((CharSequence)from)) {
            return new DefaultUser(null, from, "");
        }
        return null;
    }

    private Iterable<DataSource> footerImages() {
        return this.dataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get().getResourcesFromModules(TEMPLATE_MODULE_KEY, PluginDataSourceFactory.FilterByType.IMAGE::test).get();
    }

    private Iterable<DataSource> computePageTypeDatasources(ChangeDigestReport report) {
        Function pageReportToCEO = input -> input.getPage();
        Function commentReportToCEO = input -> input.getContentEntityObject();
        Function toContentEntities = input -> {
            List pageReport = input.getPages();
            List commentReports = input.getComments();
            return Iterables.concat((Iterable)Iterables.transform((Iterable)pageReport, (Function)pageReportToCEO), (Iterable)Iterables.transform((Iterable)commentReports, (Function)commentReportToCEO));
        };
        HashSet seenTypes = Sets.newHashSet();
        Function computeDatasource = input -> {
            String type = this.contentUiSupport.getContentTypeI18NKey((ContentEntityObject)input);
            if (!seenTypes.contains(type)) {
                seenTypes.add(type);
                String iconPathFull = this.contentUiSupport.getIconFilePath((ContentEntityObject)input, 16);
                return this.dataSourceFactory.getServletContainerResource(iconPathFull, type + "-icon");
            }
            return null;
        };
        Predicate nonNullElements = input -> input != null;
        Iterable pages = Iterables.transform((Iterable)report.getSpaceReports(), (Function)toContentEntities);
        return Iterables.filter((Iterable)Iterables.transform((Iterable)Iterables.concat((Iterable)pages), (Function)computeDatasource), (Predicate)nonNullElements);
    }

    private DataSource attachProfileImage() {
        return this.dataSourceFactory.getServletContainerResource("/images/icons/user_16.png", "profile-icon");
    }

    private String templateLocation() {
        return TEMPLATE_LOCATION;
    }

    private List<SimpleLink> makeFooterLinks() {
        return ImmutableList.of((Object)new SimpleLink("link text", "/test/href"));
    }

    private String templateName() {
        return "Confluence.Templates.Mail.Recommended.dailyReport.soy";
    }

    private DateFormatter getDateFormatter(User user) {
        return this.userPreferencesAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }
}

