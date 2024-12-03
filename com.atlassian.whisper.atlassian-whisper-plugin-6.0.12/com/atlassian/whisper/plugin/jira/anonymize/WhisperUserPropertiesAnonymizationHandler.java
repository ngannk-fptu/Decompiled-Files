/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.jira.bc.ServiceResult
 *  com.atlassian.jira.bc.ServiceResultImpl
 *  com.atlassian.jira.task.context.Context$Task
 *  com.atlassian.jira.user.anonymize.AffectedEntity
 *  com.atlassian.jira.user.anonymize.AffectedEntityType
 *  com.atlassian.jira.user.anonymize.UserAnonymizationHandler
 *  com.atlassian.jira.user.anonymize.UserAnonymizationParameter
 *  com.atlassian.jira.util.ErrorCollection
 *  com.atlassian.jira.util.I18nHelper
 *  com.atlassian.jira.util.SimpleErrorCollection
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.whisper.plugin.api.UserPropertyManager
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
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
import com.atlassian.jira.user.anonymize.AffectedEntity;
import com.atlassian.jira.user.anonymize.AffectedEntityType;
import com.atlassian.jira.user.anonymize.UserAnonymizationHandler;
import com.atlassian.jira.user.anonymize.UserAnonymizationParameter;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.whisper.plugin.ao.UserPropertyAO;
import com.atlassian.whisper.plugin.api.UserPropertyManager;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhisperUserPropertiesAnonymizationHandler
implements UserAnonymizationHandler {
    private static final Logger log = LoggerFactory.getLogger(WhisperUserPropertiesAnonymizationHandler.class);
    static final String DESCRIPTION_KEY = "atlassian.whisper.anonymize.user.properties.affected.entity";
    private final I18nHelper i18n;
    private final ActiveObjects activeObjects;
    private final UserPropertyManager userPropertyManager;

    @Inject
    public WhisperUserPropertiesAnonymizationHandler(@ComponentImport I18nHelper i18n, @ComponentImport ActiveObjects activeObjects, UserPropertyManager userPropertyManager) {
        this.i18n = i18n;
        this.activeObjects = activeObjects;
        this.userPropertyManager = userPropertyManager;
    }

    @Nonnull
    public Collection<AffectedEntity> getAffectedEntities(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        long count = this.getNumberOfPropertiesForUser(userAnonymizationParameter.getUserKey());
        return count > 0L ? Lists.newArrayList((Object[])new AffectedEntity[]{AffectedEntity.newBuilder((AffectedEntityType)AffectedEntityType.REMOVE).descriptionKey(DESCRIPTION_KEY).numberOfOccurrences(Long.valueOf(count)).build()}) : Collections.emptyList();
    }

    @Nonnull
    public ServiceResult update(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        try (Context.Task ignored = userAnonymizationParameter.getContext().start(null);){
            UserKey userKey = new UserKey(userAnonymizationParameter.getUserKey());
            this.userPropertyManager.clear(userKey);
        }
        catch (Exception e) {
            log.error("Exception when removing user properties in Atlassian Notifications plugin", (Throwable)e);
            errorCollection.addErrorMessage(this.i18n.getText("change.handler.processing.exception", e.getMessage()));
        }
        return new ServiceResultImpl((ErrorCollection)errorCollection);
    }

    public int getNumberOfTasks(@Nonnull UserAnonymizationParameter userAnonymizationParameter) {
        return 1;
    }

    private long getNumberOfPropertiesForUser(@Nonnull String userKey) {
        return this.activeObjects.count(UserPropertyAO.class, Query.select().where("USER = ?", new Object[]{userKey}));
    }
}

