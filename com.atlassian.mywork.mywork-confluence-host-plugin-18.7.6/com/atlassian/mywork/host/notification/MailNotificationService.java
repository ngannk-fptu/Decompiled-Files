/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory$FilterByType
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem$Builder
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.collect.Lists
 *  javax.activation.DataSource
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.notification;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mywork.model.Task;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.collect.Lists;
import java.util.function.Predicate;
import javax.activation.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MailNotificationService {
    private final DataSourceFactory imageDataSourceFactory;
    private final I18nResolver i18nResolver;
    private final MultiQueueTaskManager taskManager;
    private static final String EMAIL_RESOURCES = "com.atlassian.mywork.mywork-confluence-host-plugin:workday-email-resources";
    private static final String NOTIFICATION_TEMPLATE = "MyWork.Email.Templates.notification.soy";
    private static final String MAIL_QUEUE_NAME = "mail";

    public MailNotificationService(@ComponentImport DataSourceFactory imageDataSourceFactory, @ComponentImport I18nResolver i18nResolver, @ComponentImport @Qualifier(value="multiQueueTaskManager") MultiQueueTaskManager taskManager) {
        this.imageDataSourceFactory = imageDataSourceFactory;
        this.i18nResolver = i18nResolver;
        this.taskManager = taskManager;
    }

    private PreRenderedMailNotificationQueueItem createQueueItem(User recipient, User sender, String templateLocation, String templateName, String subject, NotificationContext context) {
        PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with((User)recipient, (String)templateName, (String)subject).andSender(sender).andTemplateLocation(templateLocation).andContext(context.getMap());
        builder.andRelatedBodyParts(this.imagesUsedByChromeTemplate());
        return builder.render();
    }

    private Iterable<DataSource> imagesUsedByChromeTemplate() {
        return (Iterable)((PluginDataSourceFactory)this.imageDataSourceFactory.forPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get()).getResourcesFromModules("chrome-template", (Predicate)PluginDataSourceFactory.FilterByType.IMAGE).get();
    }

    public void sendDeprecatedTaskEmail(ConfluenceUser user, Iterable<Task> personalTasks) {
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.put("taskList", (Object)Lists.newArrayList(personalTasks));
        String subject = this.i18nResolver.getText("com.atlassian.mywork.email.subject");
        DefaultUser sender = new DefaultUser(null, this.i18nResolver.getText("com.atlassian.mywork.email.sender"), "");
        PreRenderedMailNotificationQueueItem mail = this.createQueueItem((User)user, (User)sender, EMAIL_RESOURCES, NOTIFICATION_TEMPLATE, subject, notificationContext);
        this.taskManager.addTask(MAIL_QUEUE_NAME, (com.atlassian.core.task.Task)mail);
    }
}

