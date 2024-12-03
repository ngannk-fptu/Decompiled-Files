/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpacePermissionDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

@Internal
class OrphanFastSpacePermissionsRemover {
    private static final Logger log = LoggerFactory.getLogger(OrphanFastSpacePermissionsRemover.class);
    private static final int GETTING_ORPHAN_OBJECTS_LIMIT = Integer.getInteger("confluence.denormalised_space_permissions.orphan_removal.query_limit", 100000);
    private static final int ORPHAN_SIMPLE_OBJECTS_DELETION_BATCH_SIZE = Integer.getInteger("confluence.denormalised_space_permissions.orphan_removal.batch_size", 1000);
    private final ExecutorService executor;
    private final PlatformTransactionManager platformTransactionManager;
    private final DenormalisedSpacePermissionDao denormalisedSpacePermissionDao;

    public OrphanFastSpacePermissionsRemover(ExecutorService executor, PlatformTransactionManager platformTransactionManager, DenormalisedSpacePermissionDao denormalisedSpacePermissionDao) {
        this.executor = executor;
        this.platformTransactionManager = platformTransactionManager;
        this.denormalisedSpacePermissionDao = denormalisedSpacePermissionDao;
    }

    void removeAllOrphanSpacePermissions(AtomicBoolean schedulingEnabled) throws ExecutionException, InterruptedException {
        for (SpacePermissionType spacePermissionType : SpacePermissionType.values()) {
            if (!schedulingEnabled.get()) {
                return;
            }
            StopWatch watch = StopWatch.createStarted();
            int numberOfSpaces = this.removeAllOrphanSpacePermissionsForPermissionType(spacePermissionType, schedulingEnabled);
            log.debug("All fast space permissions for {} orphan spaces have been removed. Space permission type is {}. {}Duration: {}", new Object[]{numberOfSpaces, spacePermissionType, schedulingEnabled.get() ? "" : "The deletion process was not finished because it was interrupted. ", watch});
        }
    }

    private int removeAllOrphanSpacePermissionsForPermissionType(SpacePermissionType spacePermissionType, AtomicBoolean schedulingEnabled) throws ExecutionException, InterruptedException {
        List<Long> spaceIdsToRemove;
        int processedSpaceNumber = 0;
        do {
            StopWatch gettingDataWatch = StopWatch.createStarted();
            spaceIdsToRemove = this.getOrphanSpaceIdsInSeparateReadOnlyTransaction(spacePermissionType, GETTING_ORPHAN_OBJECTS_LIMIT);
            log.debug("Found {} orphan spaces in space permissions ({}). Duration: {}", new Object[]{spaceIdsToRemove.size(), spacePermissionType, gettingDataWatch});
            List partitions = Lists.partition(spaceIdsToRemove, (int)ORPHAN_SIMPLE_OBJECTS_DELETION_BATCH_SIZE);
            for (List records : partitions) {
                if (!schedulingEnabled.get()) {
                    return processedSpaceNumber;
                }
                processedSpaceNumber += this.removeSimpleRecordsAndTheirPermissionsInSeparateTransaction(spacePermissionType, records);
            }
        } while (spaceIdsToRemove.size() >= GETTING_ORPHAN_OBJECTS_LIMIT && schedulingEnabled.get());
        return processedSpaceNumber;
    }

    private int removeSimpleRecordsAndTheirPermissionsInSeparateTransaction(SpacePermissionType spacePermissionType, List<Long> spaceIds) throws ExecutionException, InterruptedException {
        log.trace("Removing orphan fast space permissions for {} objects", (Object)spaceIds.size());
        if (spaceIds.size() == 0) {
            return 0;
        }
        return this.executor.submit(() -> {
            TransactionTemplate template = new TransactionTemplate(this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
            return (Integer)template.execute(session -> {
                this.denormalisedSpacePermissionDao.deleteFastSpacePermissionsForSpaces(spacePermissionType, spaceIds);
                return spaceIds.size();
            });
        }).get();
    }

    private List<Long> getOrphanSpaceIdsInSeparateReadOnlyTransaction(SpacePermissionType spacePermissionType, int queryLimit) throws ExecutionException, InterruptedException {
        return this.executor.submit(() -> {
            TransactionTemplate template = new TransactionTemplate(this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
            template.setReadOnly(true);
            return (List)template.execute(session -> this.denormalisedSpacePermissionDao.getOrphanSpacesInFastPermissions(spacePermissionType, queryLimit));
        }).get();
    }
}

