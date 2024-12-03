/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.core.ConfluenceSidManager
 *  com.atlassian.confluence.mail.MailContentProcessor
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.confluence.mail.template.MultipartBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailUtils
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.plugin.notifications.api.ErrorCollection
 *  com.atlassian.plugin.notifications.api.medium.Group
 *  com.atlassian.plugin.notifications.api.medium.Message
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.NotificationException
 *  com.atlassian.plugin.notifications.api.medium.Server
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.ServerConnectionException
 *  com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.hash.Hashing
 *  javax.mail.Multipart
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.mail.MailContentProcessor;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.mail.template.MultipartBuilder;
import com.atlassian.confluence.plugins.email.medium.MimeMultipartMessage;
import com.atlassian.confluence.plugins.email.medium.ReplyToFieldProvider;
import com.atlassian.confluence.plugins.email.medium.SystemMailFromFieldRenderer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailUtils;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.Group;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationException;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerConnectionException;
import com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.Multipart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSystemMailServer
implements Server {
    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_HTML = "text/html";
    public static final String X_ATLASSIAN_NOTIFICATIONS_VERSION = "X-ATLASSIAN-NOTIFICATIONS-VERSION";
    public static final String X_ATLASSIAN_CLUSTER_NODE_ID = "X-ATLASSIAN-CLUSTER-NODE-ID";
    public static final String X_ATLASSIAN_CLUSTER_NODE_NAME = "X-ATLASSIAN-CLUSTER-NODE-NAME";
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSystemMailServer.class);
    private final AtomicInteger messageCounter = new AtomicInteger();
    private final String version;
    private final ServerConfiguration config;
    private final MailServerManager mailServerManager;
    private final UserAccessor userAccessor;
    private final SystemMailFromFieldRenderer systemMailFromFieldRenderer;
    private final MailContentProcessor mailContentProcessor;
    private final ReplyToFieldProvider replyToFieldProvider;
    private final ClusterManager clusterManager;
    private final ConfluenceSidManager sidManager;

    public ConfluenceSystemMailServer(ServerConfiguration config, MailServerManager mailServerManager, UserAccessor userAccessor, MailContentProcessor mailContentProcessor, SystemMailFromFieldRenderer systemMailFromFieldRenderer, String headerVersion, ReplyToFieldProvider replyToFieldProvider, ClusterManager clusterManager, ConfluenceSidManager sidManager) {
        this.mailContentProcessor = mailContentProcessor;
        this.version = headerVersion;
        this.config = config;
        this.mailServerManager = mailServerManager;
        this.userAccessor = userAccessor;
        this.systemMailFromFieldRenderer = systemMailFromFieldRenderer;
        this.replyToFieldProvider = replyToFieldProvider;
        this.clusterManager = clusterManager;
        this.sidManager = sidManager;
    }

    @Deprecated
    public static String getLocalHostName() {
        return MailUtils.getLocalHostName();
    }

    public ServerConfiguration getConfig() {
        return this.config;
    }

    public ErrorCollection testConnection(I18nResolver i18n) {
        return new ErrorCollection();
    }

    public void sendIndividualNotification(NotificationAddress notificationAddress, Message message) throws NotificationException {
        block3: {
            try {
                this.send(notificationAddress, message);
            }
            catch (MailException e) {
                throw new NotificationException(e.getMessage(), (Throwable)e);
            }
            catch (Exception e) {
                String errMsg = "Failed to send email to '" + notificationAddress.getAddressData() + "' with message id [" + message.getMessageId() + "]. " + e.getClass().getSimpleName() + ":" + e.getMessage() + ". ";
                log.warn(errMsg);
                if (!log.isDebugEnabled()) break block3;
                log.debug("", (Throwable)e);
            }
        }
    }

    private void send(NotificationAddress notificationAddress, Message message) throws Exception {
        String emailString;
        SMTPMailServer mailServer = this.mailServerManager.getDefaultSMTPMailServer();
        Objects.requireNonNull(mailServer);
        boolean isDirectEmailAddress = !notificationAddress.getMediumKey().isEmpty();
        ConfluenceUser user = isDirectEmailAddress ? null : this.userAccessor.getUserByName(notificationAddress.getAddressData());
        String string = emailString = isDirectEmailAddress ? notificationAddress.getAddressData() : user.getEmail();
        if (StringUtils.isBlank((CharSequence)emailString)) {
            StringBuilder errorMessage = new StringBuilder();
            if (user != null) {
                errorMessage.append("User '").append(user.getName()).append("[").append(user.getKey()).append("]' has no email address set. ");
            }
            errorMessage.append("No email sent for message with id '").append(message.getMessageId()).append("' and subject '").append(message.getSubject()).append("'");
            log.warn(errorMessage.toString());
            return;
        }
        String subject = message.getSubject();
        if (StringUtils.isBlank((CharSequence)subject)) {
            subject = "NotificationUtil message";
        }
        Email email = new Email(emailString);
        String instanceHash = Hashing.sha256().hashUnencodedChars((CharSequence)this.sidManager.getSid()).toString();
        String messageId = message.getMessageId();
        Option<String> replyToAddress = this.replyToFieldProvider.getReplyToField(message);
        if (replyToAddress.isDefined()) {
            email.setReplyTo((String)replyToAddress.get());
        }
        email.setSubject(subject);
        String processedBody = this.mailContentProcessor.process(message.getBody());
        email.setBody(processedBody);
        email.setMultipart(this.getMultipart(message));
        email.addHeader(X_ATLASSIAN_NOTIFICATIONS_VERSION, this.version);
        ClusterNodeInformation node = this.clusterManager.getThisNodeInformation();
        if (node != null) {
            email.addHeader(X_ATLASSIAN_CLUSTER_NODE_ID, node.getAnonymizedNodeIdentifier());
            Maybe nodeName = node.getHumanReadableNodeName();
            if (nodeName.isDefined()) {
                email.addHeader(X_ATLASSIAN_CLUSTER_NODE_NAME, (String)nodeName.get());
            }
        }
        UserProfile originatingUser = message.getOriginatingUser();
        String fromField = (String)message.getMetadata().get("OVERRIDE_SYSTEM_FROM_FIELD");
        fromField = fromField == null ? this.systemMailFromFieldRenderer.renderFromField(originatingUser, (User)user) : fromField;
        email.setFromName(fromField);
        email.setMimeType(MIME_TYPE_HTML);
        ContextClassLoaderSwitchingUtil.runInContext((ClassLoader)User.class.getClassLoader(), () -> {
            if (!StringUtils.isEmpty((CharSequence)messageId)) {
                String uniqueMessageId = "CONFLUENCE." + messageId + "." + this.messageCounter.incrementAndGet() + "." + System.currentTimeMillis() + "@" + instanceHash;
                email.setMessageId(uniqueMessageId);
                String threadId = "<" + messageId + "@" + instanceHash + ">";
                email.setInReplyTo(threadId);
                email.addHeader("References", threadId);
            }
            mailServer.send(email);
            return null;
        });
    }

    private Multipart getMultipart(Message message) {
        if (!(message instanceof MimeMultipartMessage)) {
            return null;
        }
        MimeMultipartMessage mimeMultipartMessage = (MimeMultipartMessage)message;
        if (Iterables.isEmpty(mimeMultipartMessage.getRelatedBodyPartReferences())) {
            return null;
        }
        return MultipartBuilder.INSTANCE.makeMultipart((Collection)Lists.newArrayList((Iterable)Iterables.transform(mimeMultipartMessage.getRelatedBodyPartReferences(), MimeBodyPartReference::getSource)));
    }

    public void sendGroupNotification(NotificationAddress toID, Message message) throws NotificationException {
        this.sendIndividualNotification(toID, message);
    }

    public List<Group> getAvailableGroups(String filter) throws ServerConnectionException {
        return null;
    }

    public ErrorCollection validateGroup(I18nResolver i18n, String groupId) {
        return null;
    }

    public void terminate() {
    }
}

