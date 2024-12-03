/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.jira.bc.ServiceResult
 *  com.atlassian.jira.bc.ServiceResultImpl
 *  com.atlassian.jira.task.context.Context$Task
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.jira.user.anonymize.AffectedEntity
 *  com.atlassian.jira.user.anonymize.AffectedEntityType
 *  com.atlassian.jira.user.anonymize.UserAnonymizationHandler
 *  com.atlassian.jira.user.anonymize.UserAnonymizationParameter
 *  com.atlassian.jira.util.ErrorCollection
 *  com.atlassian.jira.util.I18nHelper
 *  com.atlassian.jira.util.SimpleErrorCollection
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.whisper.plugin.api.HashCalculator
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.jira.anonymize;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.ServiceResult;
import com.atlassian.jira.bc.ServiceResultImpl;
import com.atlassian.jira.task.context.Context;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.jira.user.anonymize.AffectedEntity;
import com.atlassian.jira.user.anonymize.AffectedEntityType;
import com.atlassian.jira.user.anonymize.UserAnonymizationHandler;
import com.atlassian.jira.user.anonymize.UserAnonymizationParameter;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.whisper.plugin.ao.MessageMappingAO;
import com.atlassian.whisper.plugin.api.HashCalculator;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhisperMessagesAnonymizationHandler
implements UserAnonymizationHandler {
    private static final Logger log = LoggerFactory.getLogger(WhisperMessagesAnonymizationHandler.class);
    static final String DESCRIPTION_KEY = "atlassian.whisper.anonymize.messages.affected.entity";
    private final I18nHelper i18n;
    private final ActiveObjects activeObjects;
    private final UserKeyService userKeyService;
    private final MessagesManager messagesManager;
    private final HashCalculator hashCalculator;

    @Inject
    public WhisperMessagesAnonymizationHandler(@ComponentImport I18nHelper i18n, @ComponentImport ActiveObjects activeObjects, @ComponentImport UserKeyService userKeyService, MessagesManager messagesManager, HashCalculator hashCalculator) {
        this.i18n = i18n;
        this.activeObjects = activeObjects;
        this.userKeyService = userKeyService;
        this.messagesManager = messagesManager;
        this.hashCalculator = hashCalculator;
    }

    @Nonnull
    public Collection<AffectedEntity> getAffectedEntities(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        String userName = this.userKeyService.getUsernameForKey(userAnonymizationParameter.getUserKey());
        long count = this.getNumberOfMessagesForUser(userName);
        return count > 0L ? Lists.newArrayList((Object[])new AffectedEntity[]{AffectedEntity.newBuilder((AffectedEntityType)AffectedEntityType.REMOVE).descriptionKey(DESCRIPTION_KEY).numberOfOccurrences(Long.valueOf(count)).build()}) : Collections.emptyList();
    }

    @Nonnull
    public ServiceResult update(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        try (Context.Task ignored = userAnonymizationParameter.getContext().start(null);){
            String userName = this.userKeyService.getUsernameForKey(userAnonymizationParameter.getUserKey());
            if (userName != null) {
                this.messagesManager.removeUserMessages(this.hashCalculator.calculateUserHash(userName));
            } else {
                log.warn("Unable to retrieve username of the user being anonymized ({}), possibly because they were deleted. Will not remove their messages from Atlassian Notifications app.", (Object)userAnonymizationParameter.getUserKey());
            }
        }
        catch (Exception e) {
            log.error("Exception when removing messages in Atlassian Notifications plugin", (Throwable)e);
            errorCollection.addErrorMessage(this.i18n.getText("change.handler.processing.exception", e.getMessage()));
        }
        return new ServiceResultImpl((ErrorCollection)errorCollection);
    }

    public int getNumberOfTasks(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        return 1;
    }

    private long getNumberOfMessagesForUser(@Nullable String userName) {
        if (userName == null) {
            return 0L;
        }
        String userHash = this.hashCalculator.calculateUserHash(userName);
        return this.activeObjects.count(MessageMappingAO.class, Query.select().where("USER_HASH = ?", new Object[]{userHash}));
    }
}

