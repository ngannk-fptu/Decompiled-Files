/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.validation.MessageHolder
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.security.random.SecureTokenGenerator
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingService;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadAdminService;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.plugins.emailgateway.events.EmailThreadStagedForUserEvent;
import com.atlassian.confluence.plugins.emailgateway.service.EmailThreadConversionStartedEvent;
import com.atlassian.confluence.plugins.emailgateway.service.EmailThreadConvertedEvent;
import com.atlassian.confluence.plugins.emailgateway.service.StagedEmailThreadManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.security.random.SecureTokenGenerator;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

public class DefaultEmailThreadConverterService
implements StagedEmailThreadAdminService,
EmailStagingService {
    private final StagedEmailThreadManager stagedEmailThreadManager;
    private final UsersByEmailService usersByEmailService;
    private final SecureTokenGenerator secureTokenGenerator;
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final SpaceService spaceService;

    public DefaultEmailThreadConverterService(StagedEmailThreadManager stagedEmailThreadManager, UsersByEmailService usersByEmailService, SecureTokenGenerator secureTokenGenerator, EventPublisher eventPublisher, UserAccessor userAccessor, SpaceService spaceService) {
        this.stagedEmailThreadManager = stagedEmailThreadManager;
        this.usersByEmailService = usersByEmailService;
        this.secureTokenGenerator = secureTokenGenerator;
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
        this.spaceService = spaceService;
    }

    @Override
    public StagedEmailThreadKey stageEmailThread(ReceivedEmail receivedEmail) throws EmailStagingException {
        return this.doStageEmailThread(receivedEmail).getKey();
    }

    StagedEmailThread doStageEmailThread(ReceivedEmail receivedEmail) throws EmailStagingException {
        User user;
        StagedEmailThreadKey stagedEmailThreadKey = new StagedEmailThreadKey(this.secureTokenGenerator.generateToken());
        String userAddress = receivedEmail.getSender().getAddress();
        try {
            user = this.usersByEmailService.getUniqueUserByEmail(userAddress);
        }
        catch (EntityException e) {
            throw new EmailStagingException(e.getMessage(), e);
        }
        StagedEmailThread stagedEmailThread = new StagedEmailThread(stagedEmailThreadKey, DefaultEmailThreadConverterService.getSpaceKeyForUser(user), receivedEmail);
        ConfluenceUser confluenceUser = this.userAccessor.getUserByName(user.getName());
        Optional space = Optional.empty();
        try (AutoCloseable ignored = AuthenticatedUserThreadLocal.asUser((ConfluenceUser)confluenceUser);){
            space = this.spaceService.find(new Expansion[0]).withKeys(new String[]{DefaultEmailThreadConverterService.getSpaceKeyForUser((User)confluenceUser)}).withType(SpaceType.PERSONAL).fetch();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!space.isPresent()) {
            this.eventPublisher.publish((Object)new EmailThreadStagedForUserEvent(stagedEmailThread, confluenceUser.getKey(), true));
            throw new NotValidException("Space does not exist");
        }
        this.stagedEmailThreadManager.storeStagedEmailThread(stagedEmailThread);
        this.eventPublisher.publish((Object)new EmailThreadStagedForUserEvent(stagedEmailThread, confluenceUser.getKey()));
        return stagedEmailThread;
    }

    @Override
    public int clearExpiredEmailThreads(DateTime expiry) {
        int count = 0;
        for (StagedEmailThread thread : this.stagedEmailThreadManager) {
            if (!expiry.isAfter((ReadableInstant)thread.getStagingDate())) continue;
            this.stagedEmailThreadManager.deleteStagedEmailThread(thread.getKey());
            ++count;
        }
        return count;
    }

    private static String getSpaceKeyForUser(User user) {
        return "~" + user.getName();
    }

    @Override
    public <C extends ContentEntityObject> C convertAndPublishStagedEmailThread(StagedEmailThreadKey key, MessageHolder messageHolder, EmailToContentConverter<C> emailToContentConverter) {
        this.eventPublisher.publish((Object)new EmailThreadConversionStartedEvent(key));
        StagedEmailThread thread = this.stagedEmailThreadManager.findStagedEmailThread(key);
        if (thread == null) {
            throw new IllegalArgumentException("Could not locate a staged email thread using key " + key);
        }
        C content = emailToContentConverter.publish(thread, messageHolder);
        this.eventPublisher.publish((Object)new EmailThreadConvertedEvent(thread, (ContentEntityObject)content));
        this.stagedEmailThreadManager.deleteStagedEmailThread(key);
        return content;
    }

    @Override
    public void deleteStagedEmailThread(StagedEmailThreadKey key) {
        this.stagedEmailThreadManager.deleteStagedEmailThread(key);
    }
}

