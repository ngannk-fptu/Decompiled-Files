/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.activation.DataSource
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.confluence.admin.criteria.MailServerExistsCriteria;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.notifications.EmailService;
import com.atlassian.confluence.user.notifications.NotificationSendResult;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class DefaultEmailService
implements EmailService {
    private final MultiQueueTaskManager taskManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final DataSourceFactory dataSourceFactory;
    private final VelocityHelperService velocityHelperService;
    private final MailServerExistsCriteria mailServerExistsCriteria;

    public DefaultEmailService(MultiQueueTaskManager taskManager, I18NBeanFactory i18NBeanFactory, DataSourceFactory dataSourceFactory, VelocityHelperService velocityHelperService, MailServerExistsCriteria mailServerExistsCriteria) {
        this.taskManager = taskManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.dataSourceFactory = dataSourceFactory;
        this.velocityHelperService = velocityHelperService;
        this.mailServerExistsCriteria = mailServerExistsCriteria;
    }

    @Override
    public NotificationSendResult sendToEmails(NotificationData notificationData, List<String> recipients) {
        if (!this.mailServerExistsCriteria.isMet()) {
            return new NotificationSendResult(Sets.newHashSet(), Sets.newHashSet(recipients));
        }
        NotificationContext context = notificationData.cloneContext();
        context.putAll(this.velocityHelperService.createDefaultVelocityContext());
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        context.put("i18n", i18NBean);
        DataSource avatarDataSource = this.dataSourceFactory.getAvatar(notificationData.getModifier());
        context.put("avatarCid", avatarDataSource.getName());
        context.addTemplateImage(avatarDataSource);
        String renderedSubject = this.velocityHelperService.getRenderedContent(notificationData.getSubject(), context.getMap());
        HashSet uniqueRecipients = Sets.newHashSet(recipients);
        uniqueRecipients = Sets.newHashSet((Iterable)Collections2.transform((Collection)uniqueRecipients, String::trim));
        uniqueRecipients.remove("");
        HashSet failedRecipients = Sets.newHashSet();
        for (String recipient : uniqueRecipients) {
            InternetAddress address;
            try {
                address = new InternetAddress(recipient, true);
            }
            catch (AddressException e) {
                failedRecipients.add(recipient);
                continue;
            }
            AnonymousUser anonymousUser = new AnonymousUser(address.getAddress());
            PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with(anonymousUser, notificationData.getTemplateName(), renderedSubject).andContext(context.getMap()).andRelatedBodyParts(context.getTemplateImageDataSources());
            builder.andRelatedBodyParts(this.imagesUsedByChromeTemplate().get());
            this.taskManager.addTask("mail", (Task)this.renderEmail(builder));
        }
        this.taskManager.flush("mail");
        uniqueRecipients.removeAll(failedRecipients);
        return new NotificationSendResult(uniqueRecipients, failedRecipients);
    }

    protected PreRenderedMailNotificationQueueItem renderEmail(PreRenderedMailNotificationQueueItem.Builder builder) {
        return builder.render();
    }

    @Override
    public NotificationSendResult sendToEmail(NotificationData notificationData, String emailAddress) {
        ArrayList emails = Lists.newArrayList((Object[])new String[]{emailAddress});
        return this.sendToEmails(notificationData, emails);
    }

    private Optional<Iterable<DataSource>> imagesUsedByChromeTemplate() {
        return this.dataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get().getResourcesFromModules("chrome-template", PluginDataSourceFactory.FilterByType.IMAGE);
    }

    private static class AnonymousUser
    implements User {
        private final String email;

        private AnonymousUser(String email) {
            this.email = email;
        }

        public String getName() {
            return this.email;
        }

        public String getFullName() {
            return this.email;
        }

        public String getEmail() {
            return this.email;
        }
    }
}

