/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.CreateCommentAttachmentHandleException;
import com.atlassian.confluence.plugins.emailgateway.api.CreatePageAttachmentHandleException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;
import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCommentException;
import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCreatePageException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.plugins.emailgateway.blacklist.Blacklist;
import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionEvent;
import com.atlassian.confluence.plugins.emailgateway.polling.EmailPoller;
import com.atlassian.confluence.plugins.emailgateway.polling.EmailPollingException;
import com.atlassian.confluence.plugins.emailgateway.service.BulkEmailProcessingService;
import com.atlassian.confluence.plugins.emailgateway.service.EmailHandlerService;
import com.atlassian.confluence.plugins.emailgateway.service.UserBlacklistedException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkPollingEmailProcessingService
implements BulkEmailProcessingService {
    private static final Logger log = LoggerFactory.getLogger(BulkPollingEmailProcessingService.class);
    private final EmailPoller emailPoller;
    private final EmailHandlerService emailHandlerService;
    private final UsersByEmailService usersByEmailService;
    private final Blacklist<User> blacklist;
    private final EventPublisher eventPublisher;

    public BulkPollingEmailProcessingService(EmailPoller emailPoller, EmailHandlerService emailHandlerService, UsersByEmailService usersByEmailService, Blacklist<User> blacklist, EventPublisher eventPublisher) {
        this.emailPoller = emailPoller;
        this.emailHandlerService = emailHandlerService;
        this.usersByEmailService = usersByEmailService;
        this.blacklist = blacklist;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean isAvailable() {
        return this.emailPoller.isAvailable();
    }

    @Override
    public int processInboundEmail() {
        try {
            Collection<ReceivedEmail> receivedMessages = this.emailPoller.pollForIncomingEmails();
            log.debug("Received {} messages", (Object)receivedMessages.size());
            int processedEmails = 0;
            for (ReceivedEmail receivedMessage : receivedMessages) {
                try {
                    User user = null;
                    String userAddress = receivedMessage.getSender().getAddress();
                    try {
                        user = this.usersByEmailService.getUniqueUserByEmail(userAddress);
                    }
                    catch (EntityException entityException) {
                        // empty catch block
                    }
                    if (user != null && this.blacklist.incrementAndCheckBlacklist(user)) {
                        throw new UserBlacklistedException(user);
                    }
                    this.emailHandlerService.handle(receivedMessage);
                    ++processedEmails;
                }
                catch (EmailHandlingException | EmailStagingException e) {
                    if (e instanceof NoMatchingUserToCommentException) {
                        this.eventPublisher.publish((Object)new EmailHandlingExceptionEvent(receivedMessage.getSender().getAddress(), receivedMessage.getSubject(), false, false, false));
                    } else if (e instanceof NoMatchingUserToCreatePageException) {
                        this.eventPublisher.publish((Object)new EmailHandlingExceptionEvent(receivedMessage.getSender().getAddress(), receivedMessage.getSubject(), true, false, false));
                    } else if (e instanceof CreatePageAttachmentHandleException) {
                        this.eventPublisher.publish((Object)new EmailHandlingExceptionEvent(receivedMessage.getSender().getAddress(), receivedMessage.getSubject(), true, true, false));
                    } else if (e instanceof CreateCommentAttachmentHandleException) {
                        this.eventPublisher.publish((Object)new EmailHandlingExceptionEvent(receivedMessage.getSender().getAddress(), receivedMessage.getSubject(), false, true, false));
                    }
                    log.warn("Unable to handle received email", (Throwable)e);
                }
            }
            log.debug("Processed {} messages", (Object)processedEmails);
            return processedEmails;
        }
        catch (EmailPollingException e) {
            log.error("Failed to poll mail server", (Throwable)e);
            return 0;
        }
    }
}

