/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.util.concurrent.Callable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class PropertyEntryGardeningJob
implements JobRunner {
    public static final String CREATE_BLUEPRINT_PAGE_DRAFT_REQUEST_KEY = "create.blueprint.page.draft.request";
    private static final Logger log = LoggerFactory.getLogger(PropertyEntryGardeningJob.class);
    private final HibernateTemplate hibernateTemplate;
    private final PlatformTransactionManager transactionManager;

    public PropertyEntryGardeningJob(SessionFactory sessionFactory, PlatformTransactionManager transactionManager) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.transactionManager = transactionManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("Cleaning up create.blueprint.page.draft.request entries for contents which are not drafts...");
        long startTime = Instant.now().toEpochMilli();
        int totalEntries = this.cleanUp();
        log.info("{} entries deleted, total time: {} ms", (Object)totalEntries, (Object)(Instant.now().toEpochMilli() - startTime));
        return JobRunnerResponse.success((String)(totalEntries + " redundant property entries have been cleaned up."));
    }

    @VisibleForTesting
    public int cleanUp() {
        return this.withTransaction(() -> (Integer)this.hibernateTemplate.execute(session -> {
            int createRequestPropertyEntries = this.cleanUpPropertyEntriesOfPublishedEntities(session);
            log.debug("{} Create Page Draft Request entries were deleted", (Object)createRequestPropertyEntries);
            int totalOrphanedEntries = this.cleanUpPropertyEntriesOfDeletedEntities(session);
            log.debug("{} orphaned property entries were deleted", (Object)totalOrphanedEntries);
            return createRequestPropertyEntries + totalOrphanedEntries;
        }));
    }

    private int cleanUpPropertyEntriesOfPublishedEntities(Session session) {
        String deleteStatement = "delete from BucketPropertySetItem PropertyEntry    where PropertyEntry.key = :entityKey and        exists (select 1 from ContentEntityObject Content            where PropertyEntry.entityId = Content.id                and Content.contentStatus <> :contentStatus                and type(Content) <> Draft       )";
        Query query = session.createQuery(deleteStatement);
        query.setParameter("entityKey", (Object)CREATE_BLUEPRINT_PAGE_DRAFT_REQUEST_KEY);
        query.setParameter("contentStatus", (Object)ContentStatus.DRAFT.getValue());
        return query.executeUpdate();
    }

    private int cleanUpPropertyEntriesOfDeletedEntities(Session session) {
        String deleteStatement = "delete from BucketPropertySetItem PropertyEntry    where PropertyEntry.key = :entityKey and    not exists (select 1 from ContentEntityObject Content            where PropertyEntry.entityId = Content.id)";
        Query query = session.createQuery(deleteStatement);
        query.setParameter("entityKey", (Object)CREATE_BLUEPRINT_PAGE_DRAFT_REQUEST_KEY);
        return query.executeUpdate();
    }

    private Integer withTransaction(Callable<Integer> transactionBody) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(0);
        transactionTemplate.setReadOnly(false);
        return (Integer)transactionTemplate.execute(status -> {
            try {
                return (Integer)transactionBody.call();
            }
            catch (Exception e) {
                log.error("Error occurred while cleaning up the property entries", (Throwable)e);
                return 0;
            }
        });
    }
}

