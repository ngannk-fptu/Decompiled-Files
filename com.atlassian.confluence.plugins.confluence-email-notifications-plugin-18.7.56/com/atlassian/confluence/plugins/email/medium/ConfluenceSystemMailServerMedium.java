/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.core.ConfluenceSidManager
 *  com.atlassian.confluence.mail.MailContentProcessor
 *  com.atlassian.confluence.mail.embed.MimeBodyPartRecorder
 *  com.atlassian.confluence.user.AuthenticatedUserImpersonator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.notifications.api.ErrorCollection
 *  com.atlassian.plugin.notifications.api.macros.MacroResolver
 *  com.atlassian.plugin.notifications.api.medium.AbstractNotificationMedium
 *  com.atlassian.plugin.notifications.api.medium.Message
 *  com.atlassian.plugin.notifications.api.medium.NotificationMedium
 *  com.atlassian.plugin.notifications.api.medium.RecipientType
 *  com.atlassian.plugin.notifications.api.medium.Server
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.TemplateManager
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.user.User
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.mail.MailContentProcessor;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.plugins.email.medium.ConfluenceSystemMailServer;
import com.atlassian.confluence.plugins.email.medium.MimeMultipartMessageDecorator;
import com.atlassian.confluence.plugins.email.medium.ReplyToFieldProvider;
import com.atlassian.confluence.plugins.email.medium.SystemMailFromFieldRenderer;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.api.medium.AbstractNotificationMedium;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.TemplateManager;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.user.User;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceSystemMailServerMedium
extends AbstractNotificationMedium {
    private static final String THIS_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-email-resources";
    private final String version;
    private final TransactionTemplate transactionTemplate;
    private final MailServerManager mailServerManager;
    private final UserAccessor userAccessor;
    private final MimeBodyPartRecorder mimeBodyPartRecorder;
    private final SystemMailFromFieldRenderer systemMailFromFieldRenderer;
    private final MailContentProcessor mailContentProcessor;
    private final ReplyToFieldProvider replyToFieldProvider;
    private final ClusterManager clusterManager;
    private final ConfluenceSidManager sidManager;

    public ConfluenceSystemMailServerMedium(TemplateManager templateManager, TemplateRenderer templateRenderer, MailServerManager mailServerManager, UserAccessor userAccessor, MimeBodyPartRecorder mimeBodyPartRecorder, TransactionTemplate transactionTemplate, SystemMailFromFieldRenderer systemMailFromFieldRenderer, PluginAccessor pluginAccessor, MacroResolver macroResolver, @Qualifier(value="confluenceNotificationPreferenceManager") UserNotificationPreferencesManager userNotificationPreferenceManager, MailContentProcessor mailContentProcessor, ReplyToFieldProvider replyToFieldProvider, ClusterManager clusterManager, ConfluenceSidManager sidManager) {
        super(templateManager, templateRenderer, macroResolver, userNotificationPreferenceManager);
        this.mailServerManager = mailServerManager;
        this.userAccessor = userAccessor;
        this.mimeBodyPartRecorder = mimeBodyPartRecorder;
        this.transactionTemplate = transactionTemplate;
        this.systemMailFromFieldRenderer = systemMailFromFieldRenderer;
        this.mailContentProcessor = mailContentProcessor;
        this.replyToFieldProvider = replyToFieldProvider;
        this.clusterManager = clusterManager;
        this.version = pluginAccessor.getPlugin(THIS_PLUGIN_KEY).getPluginInformation().getVersion();
        this.sidManager = sidManager;
    }

    public ErrorCollection validateAddConfiguration(I18nResolver i18n, Map<String, String> params) {
        if (StringUtils.isBlank((CharSequence)params.get("template.user.id"))) {
            params.put("template.user.id", "{userName}");
        }
        return new ErrorCollection();
    }

    public Server createServer(ServerConfiguration config) {
        return new ConfluenceSystemMailServer(config, this.mailServerManager, this.userAccessor, this.mailContentProcessor, this.systemMailFromFieldRenderer, this.version, this.replyToFieldProvider, this.clusterManager, this.sidManager);
    }

    public boolean isIndividualNotificationSupported() {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }

    public boolean isGroupNotificationSupported() {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }

    public Option<ServerConfiguration> getStaticConfiguration() {
        if (this.mailServerManager.isDefaultSMTPMailServerDefined()) {
            return Option.some((Object)new StaticServerConfiguration(this, this.mailServerManager));
        }
        return Option.none();
    }

    public Message renderMessage(RecipientType type, Map<String, Object> context, ServerConfiguration config) {
        return (Message)this.transactionTemplate.execute(() -> {
            try {
                Pair recordingResult = this.mimeBodyPartRecorder.record(() -> {
                    ConfluenceUser user = this.userAccessor.getUserByKey((UserKey)context.get("recipientKey"));
                    return (Message)AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(() -> ConfluenceSystemMailServerMedium.super.renderMessage(type, context, config), (User)user);
                });
                return new MimeMultipartMessageDecorator((Message)((Maybe)recordingResult.left()).get(), (Iterable)recordingResult.right());
            }
            catch (Exception e) {
                throw Throwables.propagate((Throwable)e);
            }
        });
    }

    private static class StaticServerConfiguration
    implements ServerConfiguration {
        private final ConfluenceSystemMailServerMedium medium;
        private final MailServerManager mailServerManager;

        private StaticServerConfiguration(ConfluenceSystemMailServerMedium medium, MailServerManager mailServerManager) {
            this.medium = medium;
            this.mailServerManager = mailServerManager;
        }

        public int getId() {
            return -1;
        }

        public NotificationMedium getNotificationMedium() {
            return this.medium;
        }

        public String getServerName() {
            return "System Mail";
        }

        public String getProperty(String propertyKey) {
            return "";
        }

        public boolean isEnabledForAllUsers() {
            return this.mailServerManager.isDefaultSMTPMailServerDefined();
        }

        public String getDefaultUserIDTemplate() {
            return "{userName}";
        }

        public String getFullName(I18nResolver i18n) {
            return "System E-Mail Server";
        }

        public String getCustomTemplatePath() {
            return "";
        }

        public Iterable<String> getGroupsWithAccess() {
            return Lists.newArrayListWithCapacity((int)0);
        }

        public boolean isConfigurable() {
            return false;
        }
    }
}

