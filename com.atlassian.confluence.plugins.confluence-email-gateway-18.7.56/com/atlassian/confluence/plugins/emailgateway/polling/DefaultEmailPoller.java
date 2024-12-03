/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.Authorization
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.ClientTokenMetadata$ClientTokenStatus
 *  com.atlassian.oauth2.client.api.storage.TokenHandler
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException
 *  com.google.common.base.Predicate
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Lists
 *  javax.mail.AuthenticationFailedException
 *  javax.mail.Flags$Flag
 *  javax.mail.Folder
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.polling.EmailPoller;
import com.atlassian.confluence.plugins.emailgateway.polling.EmailPollingException;
import com.atlassian.confluence.plugins.emailgateway.polling.FolderUtils;
import com.atlassian.confluence.plugins.emailgateway.polling.MimeMessageFilterFactory;
import com.atlassian.confluence.plugins.emailgateway.polling.MimeMessageTransformer;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.ClientTokenMetadata;
import com.atlassian.oauth2.client.api.storage.TokenHandler;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEmailPoller
implements EmailPoller {
    private static final Logger log = LoggerFactory.getLogger(DefaultEmailPoller.class);
    private int batchSize = 10;
    private final InboundMailServerManager inboundMailServerManager;
    private final MimeMessageTransformer mimeMessageTransformer;
    private final MimeMessageFilterFactory mimeMessageFilterFactory;
    private final ClientTokenStorageService clientTokenStorageService;
    private final TokenHandler tokenHandler;

    DefaultEmailPoller(InboundMailServerManager inboundMailServerManager, MimeMessageTransformer mimeMessageTransformer, MimeMessageFilterFactory mimeMessageFilterFactory, ClientTokenStorageService clientTokenStorageService, TokenHandler tokenHandler) {
        this.inboundMailServerManager = inboundMailServerManager;
        this.mimeMessageTransformer = mimeMessageTransformer;
        this.mimeMessageFilterFactory = mimeMessageFilterFactory;
        this.clientTokenStorageService = clientTokenStorageService;
        this.tokenHandler = tokenHandler;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public boolean isAvailable() {
        return this.inboundMailServerManager.getMailServer() != null;
    }

    @Override
    public Collection<ReceivedEmail> pollForIncomingEmails() throws EmailPollingException {
        block11: {
            InboundMailServer inboundMailServer = (InboundMailServer)this.inboundMailServerManager.getMailServer();
            if (inboundMailServer == null) {
                throw new EmailPollingException("No mail server configured");
            }
            FolderUtils.FolderWorker<Collection<ReceivedEmail>> folderWorker = this.getWorker(inboundMailServer);
            try {
                return FolderUtils.execute(folderWorker);
            }
            catch (AuthenticationFailedException authEx) {
                try {
                    Authorization authorization = inboundMailServer.getAuthorization();
                    if (authorization == null) {
                        throw new Exception(String.format("No Authorization for mail server: %s", inboundMailServer.getName()));
                    }
                    ClientTokenEntity tokenEntity = this.clientTokenStorageService.getByIdOrFail(authorization.getTokenId());
                    if (this.isTokenRecoverable(tokenEntity)) {
                        Optional<String> accessTokenOpt = this.refreshAccessToken(tokenEntity.getId());
                        if (!accessTokenOpt.isPresent()) break block11;
                        try {
                            FolderUtils.execute(folderWorker);
                        }
                        catch (Exception ex) {
                            log.error("Issue connecting to store: [{}]", (Object)ex.getMessage(), (Object)ex);
                            throw authEx;
                        }
                    }
                    log.warn("Unable to refresh token from provider ID: {} - token ID: {}", (Object)authorization.getProviderId(), (Object)authorization.getTokenId());
                    throw authEx;
                }
                catch (Exception e) {
                    throw new EmailPollingException("Failed to authenticate against mail folder", e);
                }
            }
            catch (Exception e) {
                if (e.getCause() instanceof ConnectException && e.getCause().getMessage().contains("Connection refused")) {
                    log.debug("Can't open mail server folder: " + e.getMessage());
                    return Lists.newArrayList();
                }
                Throwables.propagateIfPossible((Throwable)e);
                throw new EmailPollingException("Failed to interrogate mail folder", e);
            }
        }
        return Lists.newArrayList();
    }

    private Optional<String> refreshAccessToken(String tokenId) {
        try {
            ClientToken clientToken = this.tokenHandler.getRefreshedToken(tokenId);
            return Optional.ofNullable(clientToken).map(ClientToken::getAccessToken);
        }
        catch (RecoverableTokenException | UnrecoverableTokenException ex) {
            log.debug("Access token can not be refreshed.", ex);
            return Optional.empty();
        }
    }

    private boolean isTokenRecoverable(ClientTokenEntity tokenEntity) {
        return !ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE.equals((Object)tokenEntity.getStatus());
    }

    private FolderUtils.FolderWorker<Collection<ReceivedEmail>> getWorker(final InboundMailServer inboundMailServer) {
        return new FolderUtils.FolderWorker<Collection<ReceivedEmail>>(){

            @Override
            public Folder openFolder(Store store) throws MessagingException {
                Folder folder = store.getFolder("INBOX");
                folder.open(2);
                return folder;
            }

            @Override
            public Collection<ReceivedEmail> doWithFolder(Folder folder) throws MessagingException {
                List<Message> messages = DefaultEmailPoller.this.getNextMessageBatch(folder);
                return DefaultEmailPoller.this.convertMessages(messages, (MailServer)inboundMailServer);
            }

            @Override
            public Session getMailSession() throws MailException {
                return DefaultEmailPoller.getSession(inboundMailServer);
            }

            @Override
            public InboundMailServer getMailServer() {
                return inboundMailServer;
            }

            @Override
            public String getAccessToken() throws TokenNotFoundException {
                if (inboundMailServer.getAuthorization() == null || inboundMailServer.isBasicAuth()) {
                    throw new IllegalStateException("Cannot request access token when Basic Authentication is configured.");
                }
                ClientTokenEntity clientTokenEntity = DefaultEmailPoller.this.clientTokenStorageService.getByIdOrFail(inboundMailServer.getAuthorization().getTokenId());
                return clientTokenEntity.getAccessToken();
            }
        };
    }

    private static Session getSession(InboundMailServer inboundMailServer) throws MailException {
        if (!inboundMailServer.isBasicAuth()) {
            Properties props = new Properties();
            props.setProperty("mail.pop3s.auth.xoauth2.two.line.authentication.format", "true");
            props.setProperty("mail.pop3s.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.pop3s.auth.mechanisms", "XOAUTH2");
            props.setProperty("mail.imaps.auth.mechanisms", "XOAUTH2");
            inboundMailServer.setProperties(props);
        }
        try {
            return inboundMailServer.getSession();
        }
        catch (NamingException e) {
            throw new MailException((Throwable)e);
        }
    }

    private List<Message> getNextMessageBatch(Folder folder) throws MessagingException {
        int messageCount = folder.getMessageCount();
        if (messageCount == 0) {
            return Collections.emptyList();
        }
        log.debug("There are {} messages in folder {}", (Object)messageCount, (Object)folder.getName());
        return Arrays.asList(folder.getMessages(1, Math.min(this.batchSize, messageCount)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<ReceivedEmail> convertMessages(Collection<? extends Message> mimeMessages, MailServer mailServer) {
        ArrayList receivedEmails = Lists.newArrayList();
        if (mimeMessages.size() == 0) {
            return receivedEmails;
        }
        Predicate<MimeMessage> mimeMessageFilter = this.mimeMessageFilterFactory.getFilter();
        for (Message message : mimeMessages) {
            MimeMessage mimeMessage = (MimeMessage)message;
            String messageId = DefaultEmailPoller.getMessageId(mimeMessage);
            try {
                if (mimeMessageFilter.apply((Object)mimeMessage)) {
                    receivedEmails.add(this.mimeMessageTransformer.transformMimeMessage(mimeMessage, mailServer));
                    continue;
                }
                log.warn("Message {} has been rejected by the filter; skipping it", (Object)messageId);
            }
            catch (Exception ex) {
                log.warn("Failed to process inbound MimeMessage " + messageId, (Throwable)ex);
            }
            finally {
                DefaultEmailPoller.deleteMessageFromServer(mimeMessage, messageId);
            }
        }
        return receivedEmails;
    }

    private static String getMessageId(MimeMessage message) {
        try {
            return message.getMessageID();
        }
        catch (MessagingException ex) {
            String randomId = "Random-" + RandomStringUtils.random((int)10);
            log.error("Could not extract messageID from message, using temporary ID " + randomId);
            return randomId;
        }
    }

    private static void deleteMessageFromServer(MimeMessage mimeMessage, String messageID) {
        try {
            mimeMessage.setFlag(Flags.Flag.DELETED, true);
        }
        catch (MessagingException e) {
            log.error("Could not delete email with messageId [{}]. Please delete this message manually, as too many undeleted messages will slow down Confluence", (Object)messageID);
        }
    }
}

