/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.components.impl;

import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailNotificationManager;
import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailService;
import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailTaskFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSummaryEmailService
implements SummaryEmailService {
    private MultiQueueTaskManager taskManager;
    private SummaryEmailNotificationManager notificationManager;
    private SummaryEmailTaskFactory taskFactory;
    private TransactionTemplate transactionTemplate;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SettingsManager settingsManager;
    private final CrowdService crowdService;
    private static final Logger log = LoggerFactory.getLogger(DefaultSummaryEmailService.class);
    protected static final String MAIL_QUEUE_NAME = "mail";
    private static final int BATCH_SIZE = Integer.getInteger("daily-summary-email.batchsize", 20);

    public DefaultSummaryEmailService(SummaryEmailTaskFactory taskFactory, @ComponentImport MultiQueueTaskManager taskManager, SummaryEmailNotificationManager notificationManager, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport SettingsManager settingsManager, @ComponentImport CrowdService crowdService) {
        this.taskManager = taskManager;
        this.taskFactory = taskFactory;
        this.notificationManager = notificationManager;
        this.transactionTemplate = transactionTemplate;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager = settingsManager;
        this.crowdService = crowdService;
    }

    @Override
    public int sendEmailForDate(Date date) {
        if (BATCH_SIZE < 1) {
            throw new IllegalStateException("daily-summary-email.batchsize should be larger than 0 :" + BATCH_SIZE);
        }
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        String defaultSchedule = this.getDefaultSchedule(pluginSettings);
        boolean defaultEnabled = this.getDefaultEnabled(pluginSettings);
        int batchCount = 0;
        AtomicInteger totalSent = new AtomicInteger(0);
        ArrayList<Exception> exceptions = new ArrayList<Exception>();
        log.info("Sending summary email @ {}, scheduled fire time was : {}.", (Object)new Date(), (Object)date);
        Instant start = Instant.now();
        List<String> usernames = this.getActiveUsernames();
        while (usernames.size() > 0) {
            int toIndex = usernames.size() > BATCH_SIZE ? BATCH_SIZE : usernames.size();
            List<String> usernameSubList = usernames.subList(0, toIndex);
            Instant beforeGetUserBatchInstant = Instant.now();
            List<User> batchedUsers = this.notificationManager.getUsersToReceiveNotificationAt(usernameSubList, date, defaultSchedule, defaultEnabled);
            if (log.isDebugEnabled()) {
                log.debug("Sending summary email batch {}. Getting {} users took {} minutes. ", new Object[]{batchCount, batchedUsers.size(), Duration.between(beforeGetUserBatchInstant, Instant.now()).toMinutes()});
            }
            Instant beforeSendEmailToUserBatchInstant = Instant.now();
            totalSent.getAndAdd(this.sendEmailToUserBatch(date, batchedUsers, exceptions));
            if (log.isDebugEnabled()) {
                log.debug("Summary email batch {} sent after {} minutes. ", (Object)batchCount, (Object)Duration.between(beforeSendEmailToUserBatchInstant, Instant.now()).toMinutes());
            }
            usernameSubList.clear();
            if (exceptions.size() > 0) {
                log.error("{} exceptions thrown sending the daily summary email batch {}. Turn on WARN level logging for com.atlassian.confluence.plugins.dailysummary.components.impl for more details.", (Object)exceptions.size(), (Object)batchCount);
                for (Exception ex : exceptions) {
                    log.warn("Exception thrown sending the daily summary email", (Throwable)ex);
                }
                exceptions.clear();
            }
            ++batchCount;
        }
        log.info("All summary emails have been sent @ {} after {} minutes", (Object)new Date(), (Object)Duration.between(start, Instant.now()).toMinutes());
        return totalSent.get();
    }

    private List<String> getActiveUsernames() {
        PropertyRestriction activeUserRestriction = Restriction.on((Property)UserTermKeys.ACTIVE).containing((Object)true);
        return Lists.newArrayList((Iterable)this.crowdService.search((Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)activeUserRestriction).returningAtMost(-1)));
    }

    private int sendEmailToUserBatch(Date date, List<User> users, List<Exception> exceptions) {
        AtomicInteger subtotalSent = new AtomicInteger(0);
        Iterator<User> userIterator = users.iterator();
        if (userIterator.hasNext()) {
            try {
                this.transactionTemplate.execute(() -> {
                    int counter = 0;
                    for (int i = 0; i < BATCH_SIZE && userIterator.hasNext(); ++i) {
                        User user = (User)userIterator.next();
                        try {
                            counter += this.doSend(user, date);
                            continue;
                        }
                        catch (Exception ex) {
                            exceptions.add(ex);
                        }
                    }
                    subtotalSent.getAndAdd(counter);
                    return Boolean.TRUE;
                });
            }
            catch (Exception ex) {
                exceptions.add(ex);
            }
        }
        return subtotalSent.get();
    }

    private int doSend(User user, Date date) {
        int taskCount = 0;
        Optional<Task> email = this.taskFactory.createEmailTask(user, date);
        if (email.isPresent()) {
            ++taskCount;
            this.taskManager.addTask(MAIL_QUEUE_NAME, email.get());
        } else {
            log.debug("Summary email does not have content for user {}", (Object)user);
        }
        return taskCount;
    }

    @Override
    public boolean sendEmail(User user, Date date) {
        return (Integer)this.transactionTemplate.execute(() -> this.doSend(user, date)) > 0;
    }

    private String getDefaultSchedule(PluginSettings settings) {
        String defaultVal = (String)settings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule");
        if (defaultVal != null) {
            return defaultVal;
        }
        defaultVal = "weekly";
        settings.put("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule", (Object)defaultVal);
        return defaultVal;
    }

    private boolean getDefaultEnabled(PluginSettings pluginSettings) {
        String defaultStr = (String)pluginSettings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled");
        if (defaultStr != null) {
            return Boolean.parseBoolean(defaultStr);
        }
        boolean enabled = this.settingsManager.getGlobalSettings().isDenyPublicSignup();
        pluginSettings.put("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled", (Object)Boolean.toString(enabled));
        return enabled;
    }
}

