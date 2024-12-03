/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.PopMailServer
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.mail.server.auth.AuthenticationContextAware
 *  com.atlassian.mail.server.auth.Credentials
 *  com.atlassian.mail.server.auth.OAuthCredentials
 *  com.atlassian.mail.server.auth.UserPasswordCredentials
 *  javax.annotation.Nonnull
 *  javax.mail.MessagingException
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.auth.AuthenticationContextAware;
import com.atlassian.mail.server.auth.Credentials;
import com.atlassian.mail.server.auth.OAuthCredentials;
import com.atlassian.mail.server.auth.UserPasswordCredentials;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractSupportMailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.MailQueueItemFactory;
import com.atlassian.troubleshooting.stp.salext.mail.MailServerManagerProvider;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequest;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.mail.MessagingException;

public abstract class AbstractMailUtility
implements MailUtility {
    private static final String PRIORITY_HEADER = "X-Support-Request-Priority";
    private static final String SUPPORT_REQUEST_PROPERTIES_SUFFIX = "-support-request.properties";
    private static final String REQUEST_HEADER_SUFFIX = "-Support-Request-Version";
    private final MailQueueItemFactory mailQueueItemFactory;
    private final MailServerManagerProvider mailServerManagerProvider;

    public AbstractMailUtility(@Nonnull MailQueueItemFactory mailQueueItemFactory, @Nonnull MailServerManagerProvider mailServerManagerProvider) {
        this.mailQueueItemFactory = Objects.requireNonNull(mailQueueItemFactory);
        this.mailServerManagerProvider = Objects.requireNonNull(mailServerManagerProvider);
    }

    protected MailQueueItemFactory getMailQueueItemFactory() {
        return this.mailQueueItemFactory;
    }

    protected MailServerManagerProvider getMailFactoryProvider() {
        return this.mailServerManagerProvider;
    }

    @Override
    public String getDefaultFromAddress() {
        return Optional.ofNullable(this.mailServerManagerProvider.getDefaultSMTPMailServer()).map(SMTPMailServer::getDefaultFrom).orElse("noreply@atlassian.com");
    }

    @Override
    public void sendSupportRequestMail(SupportRequest supportRequest, SupportApplicationInfo info) throws MessagingException, MailException, IOException {
        String propertiesFileName = info.getApplicationName() + SUPPORT_REQUEST_PROPERTIES_SUFFIX;
        String requestVersionHeader = "X-" + info.getApplicationName() + REQUEST_HEADER_SUFFIX;
        String requestVersionNumber = "4.0";
        supportRequest.addHeader(PRIORITY_HEADER, String.valueOf(supportRequest.getPriority()));
        supportRequest.addHeader(requestVersionHeader, "4.0");
        supportRequest.addAttachment(this.mailQueueItemFactory.newAttachment(propertiesFileName, "text/xml", info.saveProperties(SupportDataDetail.FULL)));
        supportRequest.addAttachment(this.mailQueueItemFactory.newAttachment("support-request-details.properties", "text/plain", supportRequest.saveForMail(info)));
        this.sendSupportRequestEmail(supportRequest, info);
    }

    @Override
    public boolean isMailServerConfigured() {
        return this.mailServerManagerProvider.getDefaultSMTPMailServer() != null;
    }

    @Override
    public List<SMTPMailServer> getSmtpMailServers() {
        return this.mailServerManagerProvider.getServerManager().getSmtpMailServers();
    }

    @Override
    public List<PopMailServer> getPopMailServers() {
        return this.mailServerManagerProvider.getServerManager().getPopMailServers();
    }

    @Override
    public boolean isDefaultSmtpMailServer(MailServer mailServer) {
        return mailServer.equals(this.mailServerManagerProvider.getDefaultSMTPMailServer());
    }

    @Override
    public boolean isDefaultPopMailServer(MailServer mailServer) {
        return mailServer.equals(this.mailServerManagerProvider.getDefaultPopMailServer());
    }

    @Override
    public String getAuthenticationMethod(MailServer mailServer) {
        if (mailServer instanceof AuthenticationContextAware) {
            Credentials credentials = ((AuthenticationContextAware)mailServer).getAuthenticationContext().getCredentials();
            if (credentials instanceof UserPasswordCredentials) {
                return "password";
            }
            if (credentials instanceof OAuthCredentials) {
                return "oauth2";
            }
        } else if (mailServer.getUsername() != null && mailServer.getPassword() != null) {
            return "password";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendSupportRequestEmail(SupportRequest requestInfo, SupportApplicationInfo info) throws MailException, MessagingException, IOException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            if (this.mailServerManagerProvider.getServerManager() != null) {
                Thread.currentThread().setContextClassLoader(this.mailServerManagerProvider.getServerManager().getClass().getClassLoader());
            }
            AbstractSupportMailQueueItem item = this.mailQueueItemFactory.newSupportRequestMailQueueItem(requestInfo, info);
            item.send();
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }
}

