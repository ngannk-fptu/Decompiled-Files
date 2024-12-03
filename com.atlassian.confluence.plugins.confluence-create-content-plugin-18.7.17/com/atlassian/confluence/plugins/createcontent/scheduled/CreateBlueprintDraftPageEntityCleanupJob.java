/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.scheduled;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintSanitiserManager;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="createBlueprintPageEntityCleanupJob")
public class CreateBlueprintDraftPageEntityCleanupJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CreateBlueprintDraftPageEntityCleanupJob.class);
    private static final String JOB_BATCH_SIZE_KEY = CreateBlueprintDraftPageEntityCleanupJob.class.getSimpleName() + "-batchSize";
    public static final String CLEANED_RECORD_KEY = "create.blueprint.page.draft.cleaned";
    @VisibleForTesting
    static final String LATEST_CLEANED_VERSION = "3";
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private final String getEntitiesToCleanupQueryString = "select item from BucketPropertySetItem item where item.key = :entityKey and not exists ( select 1 from BucketPropertySetItem item2 where item.entityId = item2.entityId and item2.key = :cleanedRecordKey and item2.stringVal = :cleanVal)";
    private final TransactionTemplate transactionTemplate;
    private final EntityManagerProvider entityManagerProvider;
    private final ContentBlueprintSanitiserManager sanitiserManager;
    private final ContentPropertyManager contentPropertyManager;
    private final ContentEntityManager contentEntityManager;

    @Autowired
    public CreateBlueprintDraftPageEntityCleanupJob(@ComponentImport EntityManagerProvider entityManagerProvider, @ComponentImport TransactionTemplate transactionTemplate, ContentBlueprintSanitiserManager sanitiserManager, @ComponentImport ContentPropertyManager contentPropertyManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.entityManagerProvider = entityManagerProvider;
        this.transactionTemplate = transactionTemplate;
        this.sanitiserManager = sanitiserManager;
        this.contentPropertyManager = contentPropertyManager;
        this.contentEntityManager = contentEntityManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        return (JobRunnerResponse)this.transactionTemplate.execute(() -> {
            try {
                List<BucketPropertySetItem> propertySetItems = this.getEntitiesToCleanup();
                if (propertySetItems.size() > 0) {
                    int totalCleaned = this.performCleanup(propertySetItems);
                    String summaryMessage = String.format("Cleaned up %d entries in %d ms", totalCleaned, System.currentTimeMillis() - jobRunnerRequest.getStartTime().getTime());
                    return JobRunnerResponse.success((String)summaryMessage);
                }
                return JobRunnerResponse.success((String)"No entries were found to cleanup");
            }
            catch (PersistenceException e) {
                return JobRunnerResponse.failed((Throwable)e);
            }
        });
    }

    private List<BucketPropertySetItem> getEntitiesToCleanup() throws PersistenceException {
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        int batchSize = this.getCurrentBatchSize().orElse(1000);
        Query getEntitiesToCleanupQuery = entityManager.createQuery("select item from BucketPropertySetItem item where item.key = :entityKey and not exists ( select 1 from BucketPropertySetItem item2 where item.entityId = item2.entityId and item2.key = :cleanedRecordKey and item2.stringVal = :cleanVal)").setParameter("entityKey", (Object)"create.blueprint.page.draft.request").setParameter("cleanedRecordKey", (Object)CLEANED_RECORD_KEY).setParameter("cleanVal", (Object)LATEST_CLEANED_VERSION).setMaxResults(batchSize);
        List propertySetItems = getEntitiesToCleanupQuery.getResultList();
        return propertySetItems;
    }

    @VisibleForTesting
    int performCleanup(List<BucketPropertySetItem> entriesToClean) throws PersistenceException {
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        int cleanedCount = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        for (BucketPropertySetItem propertySetItem : entriesToClean) {
            ContentEntityObject ceo = this.contentEntityManager.getById(propertySetItem.getEntityId());
            if (ceo != null) {
                if (!ceo.isDraft() && !ContentTypeEnum.DRAFT.equals((Object)ceo.getTypeEnum())) continue;
                String entityString = propertySetItem.getTextVal();
                try {
                    CreateBlueprintPageEntity entity = (CreateBlueprintPageEntity)objectMapper.readValue(entityString, CreateBlueprintPageRestEntity.class);
                    propertySetItem.setTextVal(objectMapper.writeValueAsString((Object)this.sanitiserManager.sanitise(entity)));
                    entityManager.persist((Object)propertySetItem);
                    this.contentPropertyManager.setStringProperty(ceo, CLEANED_RECORD_KEY, LATEST_CLEANED_VERSION);
                    ++cleanedCount;
                }
                catch (IOException e) {
                    log.error("Could not process the CreateBlueprintPageEntity for cleanup: ", (Throwable)e);
                }
                continue;
            }
            entityManager.remove((Object)propertySetItem);
            ++cleanedCount;
        }
        return cleanedCount;
    }

    @VisibleForTesting
    Optional<Integer> getCurrentBatchSize() {
        return Optional.ofNullable(System.getProperty(JOB_BATCH_SIZE_KEY)).map(Integer::parseInt);
    }
}

