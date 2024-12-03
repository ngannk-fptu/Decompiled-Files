/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.mail.server.MailServerManager
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.mail.notification.listeners.async;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.content.page.async.PageEvent;
import com.atlassian.confluence.event.events.content.page.async.PageTrashedEvent;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.event.api.EventListener;
import com.atlassian.mail.server.MailServerManager;
import java.io.Serializable;
import java.util.Objects;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class PageEventListener {
    private static final Logger log = LoggerFactory.getLogger(PageEventListener.class);
    private final PageManager pageManager;
    private final UserAccessor userAccessor;
    private final DataSourceFactory dataSourceFactory;
    private final NotificationsSender notificationsSender;
    private final PlatformTransactionManager platformTransactionManager;
    private final MailServerManager mailServerManager;

    public PageEventListener(PageManager pageManager, @Qualifier(value="userAccessor") UserAccessor userAccessor, DataSourceFactory dataSourceFactory, NotificationsSender notificationsSender, PlatformTransactionManager platformTransactionManager, MailServerManager mailServerManager) {
        this.pageManager = pageManager;
        this.userAccessor = userAccessor;
        this.dataSourceFactory = dataSourceFactory;
        this.notificationsSender = notificationsSender;
        this.platformTransactionManager = platformTransactionManager;
        this.mailServerManager = mailServerManager;
    }

    @EventListener
    public void handleEventInTransaction(PageTrashedEvent event) {
        TransactionCallbackWithoutResult callback = this.createCallback(event);
        TransactionTemplate template = this.createTemplate(this.platformTransactionManager);
        template.execute((TransactionCallback)callback);
    }

    private TransactionCallbackWithoutResult createCallback(final PageEvent event) {
        return new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                PageEventListener.this.handleEvent(event);
            }
        };
    }

    private TransactionTemplate createTemplate(PlatformTransactionManager platformTransactionManager) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(0);
        transactionDefinition.setReadOnly(true);
        return new TransactionTemplate(platformTransactionManager, (TransactionDefinition)transactionDefinition);
    }

    private ConversionContext createConversionContext(NotificationData notificationData) {
        ContentEntityObject notificationObject = (ContentEntityObject)notificationData.getCommonContext().getMap().get("content");
        PageContext context = notificationObject.toPageContext();
        context.setOutputType("email");
        return new DefaultConversionContext(context);
    }

    public void handleEvent(PageEvent event) {
        if (!(event instanceof PageTrashedEvent) || !this.mailServerManager.isDefaultSMTPMailServerDefined() || event.isSuppressNotifications()) {
            return;
        }
        PageTrashedEvent pageTrashedEvent = (PageTrashedEvent)event;
        log.debug("Processing event: {}", (Object)event);
        Page page = Objects.requireNonNull(this.pageManager.getPage(event.getPageId()));
        NotificationData data = this.createNotificationData(pageTrashedEvent, page);
        data.setTemplateName("Confluence.Templates.Mail.Notifications.pageRemove.soy");
        this.notificationsSender.sendSpaceNotifications(page.getSpace(), data, this.createConversionContext(data));
        this.notificationsSender.sendPageNotifications(page, data, this.createConversionContext(data));
        this.notificationsSender.sendNetworkNotifications(data, this.createConversionContext(data));
    }

    private NotificationData createNotificationData(PageTrashedEvent event, Page page) {
        ConfluenceUser user = event.getOriginatingUserKey() != null ? this.userAccessor.getExistingUserByKey(event.getOriginatingUserKey()) : null;
        UserPreferences preferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        boolean shouldNotifyOnOwnActions = preferences.getBoolean("confluence.prefs.notify.for.my.own.actions");
        NotificationData notificationData = new NotificationData(user, shouldNotifyOnOwnActions, page);
        NotificationContext commonContext = notificationData.getCommonContext();
        DataSource avatarDataSource = this.dataSourceFactory.getAvatar(notificationData.getModifier());
        commonContext.setContent(page);
        commonContext.setEvent(event);
        notificationData.setSubject("$space.name > $content.title");
        notificationData.addToContext("page", page);
        notificationData.addToContext("content", page);
        notificationData.addToContext("contentType", (Serializable)((Object)"page"));
        notificationData.addToContext("space", page.getSpace());
        notificationData.addTemplateImage(this.dataSourceFactory.getServletContainerResource("/images/icons/contenttypes/page_16.png", "page-icon"));
        notificationData.addToContext("avatarCid", (Serializable)((Object)avatarDataSource.getName()));
        notificationData.addTemplateImage(avatarDataSource);
        return notificationData;
    }
}

