/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.storage.InlineTasksUtils;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.links.SimpleLink;
import com.atlassian.confluence.mail.notification.ConversionContextCreator;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNotificationsSender
implements NotificationsSender {
    private static final Logger log = LoggerFactory.getLogger(DefaultNotificationsSender.class);
    private static final Set<String> TEMPLATES_WITH_DIFF_CONTENT = ImmutableSet.of((Object)"page-edited-notification.vm", (Object)"comment-edited-notification.vm", (Object)"Confluence.Templates.Mail.Notifications.commentEdit.soy", (Object)"Confluence.Templates.Mail.Notifications.pageEdit.soy", (Object)"Confluence.Templates.Mail.Notifications.blogEdit.soy");
    private static final Set<String> TEMPLATES_WITH_CONTENT_REFERENCING_TASK_IMAGES = ImmutableSet.of((Object)"page-added-notification.vm", (Object)"Confluence.Templates.Mail.Notifications.pageAdd.soy");
    private static final Set<String> TEMPLATES_WITH_ACTION_LINKS = ImmutableSet.of((Object)"Confluence.Templates.Mail.Notifications.pageAdd.soy", (Object)"Confluence.Templates.Mail.Notifications.pageEdit.soy", (Object)"Confluence.Templates.Mail.Notifications.blogpostAdd.soy", (Object)"Confluence.Templates.Mail.Notifications.blogEdit.soy", (Object)"Confluence.Templates.Mail.Notifications.commentAdd.soy", (Object)"Confluence.Templates.Mail.Notifications.commentEdit.soy", (Object[])new String[0]);
    private UserAccessor userAccessor;
    private MultiQueueTaskManager taskManager;
    private NotificationManager notificationManager;
    private Renderer viewRenderer;
    private DataSourceFactory dataSourceFactory;
    private ConfluenceAccessManager confluenceAccessManager;
    private PermissionManager permissionManager;
    private FormatSettingsManager formatSettingsManager;
    private WebResourceManager webResourceManager;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private Differ differ;
    private NotificationRenderManager notificationRenderManager;

    public DefaultNotificationsSender(UserAccessor userAccessor, MultiQueueTaskManager taskManager, NotificationManager notificationManager, Renderer viewRenderer, PermissionManager permissionManager, FormatSettingsManager formatSettingsManager, WebResourceManager webResourceManager, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, Differ differ, NotificationRenderManager notificationRenderManager, DataSourceFactory dataSourceFactory, ConfluenceAccessManager confluenceAccessManager) {
        this.userAccessor = userAccessor;
        this.taskManager = taskManager;
        this.notificationManager = notificationManager;
        this.viewRenderer = viewRenderer;
        this.permissionManager = permissionManager;
        this.formatSettingsManager = formatSettingsManager;
        this.webResourceManager = webResourceManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.differ = differ;
        this.notificationRenderManager = notificationRenderManager;
        this.dataSourceFactory = dataSourceFactory;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    @Override
    public final void sendNotification(Notification notification, NotificationData notificationData, ConversionContext conversionContext) {
        ConfluenceUser receiver = notification.getReceiver();
        NotificationContext notificationContext = notificationData.cloneContextForRecipient(receiver);
        notificationContext.setWatchType(notification.getWatchType());
        this.sendNotification(receiver, notificationContext, notificationData, conversionContext);
    }

    @Override
    public final void sendNotification(String recipient, NotificationContext context, NotificationData notificationData, ConversionContext conversionContext) {
        ConfluenceUser toUser = this.userAccessor.getUserByName(recipient);
        if (toUser == null) {
            log.debug("Dropping notification to nonexistent recipient {}", (Object)recipient);
            return;
        }
        this.sendNotification(toUser, context, notificationData, conversionContext);
    }

    public final void sendNotification(ConfluenceUser toUser, NotificationContext context, NotificationData notificationData, ConversionContext conversionContext) {
        String recipient = toUser.getName();
        try {
            AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(() -> {
                log.info("Send Notification: Creating notification for user: '{}'", (Object)notificationData.getSubject(), (Object)recipient);
                if (!this.isValidUser(recipient, notificationData, toUser)) {
                    return null;
                }
                ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences(toUser);
                context.putAll(MacroUtils.defaultVelocityContext());
                context.putAll(notificationData.getCommonContext().getMap());
                context.setRecipient(toUser);
                context.addWebFragmentContext();
                context.put("webResourceManager", this.webResourceManager);
                context.put("dateFormatter", userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager));
                context.put("viewRenderer", this.viewRenderer);
                context.put("conversionContext", conversionContext);
                context.put("conversionContextCreator", new ConversionContextCreator());
                I18NBean userI18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(toUser));
                context.setI18n(userI18NBean);
                OutputMimeTypeAwareVelocityContext confluenceVelocityContext = new OutputMimeTypeAwareVelocityContext(context.getMap());
                confluenceVelocityContext.setOutputMimeType("text/plain");
                String renderedSubject = VelocityUtils.getRenderedContent(notificationData.getSubject(), (Context)confluenceVelocityContext);
                if (StringUtils.isBlank((CharSequence)toUser.getEmail())) {
                    log.warn("Send Notification: Not sending email [ " + renderedSubject + " ] to [ " + toUser.getFullName() + " ]:  No email set");
                    return null;
                }
                String templateName = notificationData.getTemplateName();
                if (TEMPLATES_WITH_DIFF_CONTENT.contains(templateName)) {
                    boolean showDiffs = userPreferences.isShowDifferencesInNotificationEmails();
                    context.put("showDiffs", showDiffs);
                    context.put("showFullContent", false);
                    if (showDiffs) {
                        ContentEntityObject originalContent = (ContentEntityObject)context.get("originalContent");
                        ContentEntityObject content = (ContentEntityObject)context.get("content");
                        try {
                            String emailDiff = this.differ.diff(originalContent, content);
                            context.put("diffHtml", emailDiff);
                            this.attachTaskImagesIfNeeded(context, emailDiff);
                        }
                        catch (DiffException e) {
                            log.error("Send Notification: Error determining diff for: " + content + ". Diff will be omitted from the notification email.", (Throwable)e);
                        }
                    }
                }
                if (TEMPLATES_WITH_CONTENT_REFERENCING_TASK_IMAGES.contains(templateName)) {
                    ContentEntityObject content = (ContentEntityObject)context.get("content");
                    try {
                        this.attachTaskImagesIfNeeded(context, this.differ.diff(content, content));
                    }
                    catch (DiffException e) {
                        log.error("Send Notification: Error determining diff for: " + content + ". Any inline task checkboxes will be omitted from the notification email.", (Throwable)e);
                    }
                }
                context.put("user", toUser);
                context.put("leftFooterLinks", this.getWebItemLinks("email.footer.links.left", context));
                context.put("rightFooterLinks", this.getWebItemLinks("email.footer.links.right", context));
                if (notificationData.getTemplateName().endsWith(".soy")) {
                    if (TEMPLATES_WITH_ACTION_LINKS.contains(notificationData.getTemplateName())) {
                        this.notificationRenderManager.attachActionIconImages("email.adg.action.links", context);
                    }
                    context.put("actionLinks", this.getWebItemLinks("email.adg.action.links", context));
                    context.put("footerLinks", this.getWebItemLinks("email.adg.footer.links", context));
                }
                this.taskManager.addTask("mail", this.createNotificationTask(toUser, notificationData, renderedSubject, context));
                log.debug("Send Notification: task added to send mail to '{}' at email address '{}'", (Object)toUser.getName(), (Object)toUser.getEmail());
                return null;
            }, toUser);
        }
        catch (RuntimeException t) {
            log.error("Error sending notification", (Throwable)t);
        }
    }

    @Override
    public void sendPageNotifications(AbstractPage page, NotificationData notificationData, ConversionContext conversionContext) {
        List<Notification> pageNotifications = this.notificationManager.getNotificationsByContent(page);
        log.info("Sending page notifications for '{}' to {} people.", (Object)notificationData.getSubject(), (Object)pageNotifications.size());
        this.sendNotifications(pageNotifications, notificationData, conversionContext);
    }

    @Override
    public void sendSpaceNotifications(Space space, NotificationData notificationData, ConversionContext conversionContext) {
        List<Notification> spaceNotifications = this.notificationManager.getNotificationsBySpaceAndType(space, null);
        log.info("Sending space notifications for '{}' to {} people.", (Object)notificationData.getSubject(), (Object)spaceNotifications.size());
        this.sendNotifications(spaceNotifications, notificationData, conversionContext);
    }

    @Override
    public void sendNetworkNotifications(NotificationData notificationData, ConversionContext conversionContext) {
        User modifier = notificationData.getModifier();
        if (modifier == null) {
            return;
        }
        List<Notification> notificationList = this.notificationManager.findNotificationsByFollowing(modifier);
        log.info("Sending network notifications for '{}' to {} people.", (Object)notificationData.getSubject(), (Object)notificationList.size());
        this.sendNotifications(notificationList, notificationData, conversionContext);
    }

    @Override
    public void sendNotifications(List<Notification> notifications, NotificationData notificationData, ConversionContext conversionContext) {
        for (Notification notification : notifications) {
            String userName;
            ConfluenceUser receiver = notification.getReceiver();
            String string = userName = receiver != null ? receiver.getName() : null;
            if (notificationData.doNotNotifyAgain(userName)) continue;
            notificationData.addDoNotNotifyAgain(userName);
            this.sendNotification(notification, notificationData, conversionContext);
        }
    }

    private List<SimpleLink> getWebItemLinks(String section, NotificationContext context) {
        List<WebItemModuleDescriptor> webItems = this.notificationRenderManager.getDisplayableItems(section, context);
        ArrayList<SimpleLink> emailLinks = new ArrayList<SimpleLink>();
        for (WebItemModuleDescriptor webItem : webItems) {
            String url = webItem.getLink().getRenderedUrl(context.getMap());
            String i18nKey = webItem.getWebLabel().getKey();
            emailLinks.add(new SimpleLink(i18nKey, url));
        }
        return emailLinks;
    }

    @VisibleForTesting
    boolean isValidUser(String username, NotificationData notificationData, User toUser) {
        if (toUser == null) {
            log.debug("Send Notification: No user could be found with the name '{}'", (Object)username);
            return false;
        }
        if (this.userAccessor.isDeactivated(toUser) || !this.userHasLicensedAccess(toUser) || !this.isPermittedRecipient(notificationData, toUser)) {
            log.debug("Send Notification: The user {} was not a permissible recipient.", (Object)toUser.getName());
            return false;
        }
        return true;
    }

    private boolean userHasLicensedAccess(User user) {
        return this.confluenceAccessManager.getUserAccessStatus(user).hasLicensedAccess();
    }

    private boolean isPermittedRecipient(NotificationData notificationData, User recipient) {
        ConfluenceEntityObject entity = notificationData.getPermissionEntity();
        return entity == null || this.permissionManager.hasPermissionNoExemptions(recipient, Permission.VIEW, entity);
    }

    protected Task createNotificationTask(User toUser, NotificationData notificationData, String renderedSubject, NotificationContext context) {
        PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with(toUser, notificationData.getTemplateName(), renderedSubject).andSender(notificationData.getModifier()).andContext(context.getMap()).andRelatedBodyParts(context.getTemplateImageDataSources());
        if (notificationData.getTemplateName().endsWith(".soy")) {
            builder.andRelatedBodyParts(this.imagesUsedByChromeTemplate());
        }
        return builder.render();
    }

    private Iterable<DataSource> imagesUsedByChromeTemplate() {
        return this.dataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get().getResourcesFromModules("chrome-template", PluginDataSourceFactory.FilterByType.IMAGE::test).get();
    }

    private void attachTaskImagesIfNeeded(NotificationContext notificationContext, String emailDiff) {
        for (DataSource resource : InlineTasksUtils.getRequiredResources(this.dataSourceFactory, emailDiff)) {
            notificationContext.addTemplateImage(resource);
        }
    }
}

