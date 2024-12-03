/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.jira.bc.ServiceResult
 *  com.atlassian.jira.bc.ServiceResultImpl
 *  com.atlassian.jira.task.context.Context
 *  com.atlassian.jira.user.anonymize.AffectedEntity
 *  com.atlassian.jira.user.anonymize.AffectedEntityType
 *  com.atlassian.jira.user.anonymize.UserNameChangeHandler
 *  com.atlassian.jira.user.anonymize.UserPropertyChangeParameter
 *  com.atlassian.jira.util.ErrorCollection
 *  com.atlassian.jira.util.SimpleErrorCollection
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.thirdparty.jira.anonymization;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.ServiceResult;
import com.atlassian.jira.bc.ServiceResultImpl;
import com.atlassian.jira.task.context.Context;
import com.atlassian.jira.user.anonymize.AffectedEntity;
import com.atlassian.jira.user.anonymize.AffectedEntityType;
import com.atlassian.jira.user.anonymize.UserNameChangeHandler;
import com.atlassian.jira.user.anonymize.UserPropertyChangeParameter;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.thirdparty.ao.ActivityEntity;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class JiraStreamsUsernameChangeHandler
implements UserNameChangeHandler {
    private static final Logger log = LoggerFactory.getLogger(JiraStreamsUsernameChangeHandler.class);
    private final ActiveObjects ao;
    private final StreamsI18nResolver i18nResolver;

    public JiraStreamsUsernameChangeHandler(ActiveObjects ao, StreamsI18nResolver i18nResolver) {
        this.ao = Objects.requireNonNull(ao);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    @Nonnull
    public Collection<AffectedEntity> getAffectedEntities(UserPropertyChangeParameter changeParameter) {
        int affectedEntitiesCount = this.getAffectedEntitiesCount(changeParameter.getOriginal());
        if (affectedEntitiesCount > 0) {
            return ImmutableList.of((Object)AffectedEntity.newBuilder((AffectedEntityType)AffectedEntityType.ANONYMIZE).numberOfOccurrences(Long.valueOf(affectedEntitiesCount)).descriptionKey("streams.jira.username_change_handler.entry").build());
        }
        return Collections.emptyList();
    }

    private int getAffectedEntitiesCount(String originalUserName) {
        int posters = this.ao.count(ActivityEntity.class, this.getQueryForPoster(originalUserName));
        int userNames = this.ao.count(ActivityEntity.class, this.getQueryForUsername(originalUserName));
        return posters + userNames;
    }

    @Nonnull
    public ServiceResult update(UserPropertyChangeParameter changeParameter) {
        String originalName = changeParameter.getOriginal();
        String targetName = changeParameter.getTarget();
        ErrorHandlingRunner runner = new ErrorHandlingRunner((ErrorCollection)new SimpleErrorCollection(), changeParameter.getContext());
        this.updateActiveEntityField(runner, this.getQueryForPoster(originalName), e -> e.setPoster(targetName));
        this.updateActiveEntityField(runner, this.getQueryForUsername(originalName), e -> e.setUsername(targetName));
        return new ServiceResultImpl(runner.getErrorCollection());
    }

    public int getNumberOfTasks(UserPropertyChangeParameter changeParameter) {
        return this.getAffectedEntitiesCount(changeParameter.getOriginal());
    }

    @Nonnull
    private Query getQueryForPoster(String username) {
        return Query.select().where("POSTER = ?", new Object[]{username});
    }

    @Nonnull
    private Query getQueryForUsername(String username) {
        return Query.select().where("USERNAME = ?", new Object[]{username});
    }

    private void updateActiveEntityField(ErrorHandlingRunner runner, Query query, Consumer<ActivityEntity> updateEntity) {
        LinkedHashSet ids = new LinkedHashSet();
        this.ao.stream(ActivityEntity.class, query, activityEntity -> ids.add(activityEntity.getActivityId()));
        Iterator iterator = ids.iterator();
        while (iterator.hasNext()) {
            long id = (Long)iterator.next();
            ActivityEntity entity = runner.execute(() -> (ActivityEntity)this.ao.get(ActivityEntity.class, (Object)id));
            if (entity == null) {
                log.warn("Wanted to update username in activity entity with id {}, but failed to retrieve it", (Object)id);
                continue;
            }
            runner.executeAndUpdateProgress(() -> updateEntity.andThen(RawEntity::save).accept(entity));
        }
    }

    @ParametersAreNonnullByDefault
    private class ErrorHandlingRunner {
        private final ErrorCollection errorCollection;
        private final Context context;

        private ErrorHandlingRunner(ErrorCollection errorCollection, Context context) {
            this.errorCollection = Objects.requireNonNull(errorCollection);
            this.context = Objects.requireNonNull(context);
        }

        @Nullable
        ActivityEntity execute(Callable<ActivityEntity> callable) {
            try {
                return callable.call();
            }
            catch (Exception e) {
                log.error("There was an exception during username change in streams thirdparty plugin", (Throwable)e);
                this.errorCollection.addErrorMessage(JiraStreamsUsernameChangeHandler.this.i18nResolver.getText("streams.jira.change.handler.processing.exception", new Serializable[]{e.getMessage()}));
                return null;
            }
        }

        void executeAndUpdateProgress(Runnable runnable) {
            try {
                this.execute(Executors.callable(runnable, null));
            }
            finally {
                this.context.start(null).complete();
            }
        }

        @Nonnull
        ErrorCollection getErrorCollection() {
            return this.errorCollection;
        }
    }
}

