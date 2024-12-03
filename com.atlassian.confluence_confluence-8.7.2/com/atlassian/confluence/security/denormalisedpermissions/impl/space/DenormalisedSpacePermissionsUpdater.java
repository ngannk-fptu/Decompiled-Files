/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.PostConstruct
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space;

import com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.EffectiveSpacePermissionsCalculator;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.OrphanFastSpacePermissionsRemover;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpacePermissionDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpacePermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.DenormalisedSidManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.security.persistence.dao.hibernate.SpacePermissionDTOLight;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedSpacePermissionsUpdater
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedSpacePermissionsUpdater.class);
    private static final List<String> SUPPORTED_SPACE_PERMISSIONS = ImmutableList.of((Object)"VIEWSPACE", (Object)"EDITSPACE");
    @VisibleForTesting
    public static final int DEFAULT_INITIAL_SPACE_PROCESSING_LIMIT = 1000;
    private static final AtomicInteger INITIAL_SPACE_PROCESSING_LIMIT = new AtomicInteger(Integer.getInteger("confluence.denormalised_space_permissions.initial_log_processing_batch_size", 1000));
    private final EventPublisher eventPublisher;
    private final PlatformTransactionManager platformTransactionManager;
    private final SpaceDaoInternal spaceDao;
    private final SpacePermissionDao spacePermissionDao;
    private final DenormalisedSpacePermissionDao denormalisedSpacePermissionDao;
    private final DenormalisedSidManager denormalisedSidManager;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));

    @VisibleForTesting
    public static void setInitialSpaceProcessingLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Initial space processing limit can't be negative or zero.");
        }
        INITIAL_SPACE_PROCESSING_LIMIT.set(limit);
    }

    public DenormalisedSpacePermissionsUpdater(EventPublisher eventPublisher, DenormalisedSpacePermissionDao denormalisedSpacePermissionDao, SpacePermissionDao spacePermissionDao, DenormalisedSidManager denormalisedSidManager, PlatformTransactionManager platformTransactionManager, SpaceDaoInternal spaceDao) {
        this.eventPublisher = eventPublisher;
        this.denormalisedSpacePermissionDao = denormalisedSpacePermissionDao;
        this.spacePermissionDao = spacePermissionDao;
        this.denormalisedSidManager = denormalisedSidManager;
        this.platformTransactionManager = platformTransactionManager;
        this.spaceDao = spaceDao;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void handleSpacePermissionChangeEvent(SpacePermissionChangeEvent spacePermissionChangeEvent) {
    }

    public Set<Long> updateSpacePermissions(Set<Long> spaceIds) {
        return this.updateSpacePermissions(spaceIds, null);
    }

    public Set<Long> updateSpacePermissions(Set<Long> spaceIds, Long deadline) {
        if (spaceIds.size() == 0) {
            return Collections.emptySet();
        }
        List<SpacePermissionDTOLight> realSpacePermissions = this.spacePermissionDao.findPermissionsForSpacesAndTypes(spaceIds, SUPPORTED_SPACE_PERMISSIONS);
        Map realSpacePermissionsGroupedBySpaceId = realSpacePermissions.stream().collect(Collectors.groupingBy(SpacePermissionDTOLight::getSpaceId, Collectors.toList()));
        Map<String, Long> userKeyToSidMap = this.getOrCreateAllUserKeysForRealPermissions(realSpacePermissions);
        Map<String, Long> groupNameToSidMap = this.getOrCreateAllGroupNamesForRealPermissions(realSpacePermissions);
        Map<Long, List<DenormalisedSpacePermission>> existingDenormalisedSpaceViewPermissions = this.denormalisedSpacePermissionDao.findPermissionsForSpaces(spaceIds, SpacePermissionType.VIEWSPACE);
        Map<Long, List<DenormalisedSpacePermission>> existingDenormalisedSpaceEditPermissions = this.denormalisedSpacePermissionDao.findPermissionsForSpaces(spaceIds, SpacePermissionType.EDITSPACE);
        HashSet<Long> processedSpaceIds = new HashSet<Long>();
        for (Long spaceId : spaceIds) {
            List<DenormalisedSpacePermission> denormalisedViewPermissionsForSpace = existingDenormalisedSpaceViewPermissions.get(spaceId);
            List<DenormalisedSpacePermission> denormalisedEditPermissionsForSpace = existingDenormalisedSpaceEditPermissions.get(spaceId);
            List<SpacePermissionDTOLight> realPermissionsForSpace = realSpacePermissionsGroupedBySpaceId.get(spaceId);
            this.updateDenormalisedPermissionsForSpace(spaceId, realPermissionsForSpace != null ? realPermissionsForSpace : Collections.emptyList(), denormalisedViewPermissionsForSpace != null ? denormalisedViewPermissionsForSpace : Collections.emptyList(), denormalisedEditPermissionsForSpace != null ? denormalisedEditPermissionsForSpace : Collections.emptyList(), userKeyToSidMap, groupNameToSidMap);
            processedSpaceIds.add(spaceId);
            if (processedSpaceIds.size() >= spaceIds.size() || !this.isDeadlineMissed(deadline)) continue;
            log.debug("Due to a timeout only {} spaces (of {}) were updated. Note that the rest of the records could be updated by another node.", (Object)processedSpaceIds.size(), (Object)spaceIds.size());
            break;
        }
        return processedSpaceIds;
    }

    public int updateAllSpacePermissions(AtomicBoolean schedulingEnabled, Supplier<DenormalisedPermissionServiceState> spaceServiceStateSupplier) throws ExecutionException, InterruptedException {
        log.info("Started updating all spaces");
        Long lastSpaceId = null;
        int stepCounter = 0;
        int processedRecordsCounter = 0;
        long globalStart = System.currentTimeMillis();
        this.removeOrphanSpacePermissions(schedulingEnabled);
        while (schedulingEnabled.get()) {
            StopWatch watch = StopWatch.createStarted();
            DenormalisedPermissionServiceState currentSpaceServiceState = spaceServiceStateSupplier.get();
            if (!DenormalisedPermissionServiceState.INITIALISING.equals((Object)currentSpaceServiceState)) {
                log.warn("Initialisation of the denormalised space service was interrupted. Current state: {}. Processed {} batches so far. Duration: {}", new Object[]{currentSpaceServiceState, stepCounter, watch});
                return processedRecordsCounter;
            }
            List<Long> spaceIdList = this.executor.submit(new NextSpaceGetter(lastSpaceId)).get();
            if (spaceIdList.isEmpty()) break;
            lastSpaceId = spaceIdList.get(spaceIdList.size() - 1);
            this.executor.submit(new SpaceDenormalisedPermissionsUpdaterWithoutTimeLimits(spaceIdList)).get();
            log.info("Processed {} spaces (batch number {}). Duration: {}", new Object[]{spaceIdList.size(), stepCounter++, watch});
            processedRecordsCounter += spaceIdList.size();
        }
        log.info("Processed {} spaces in {} ms", (Object)processedRecordsCounter, (Object)(System.currentTimeMillis() - globalStart));
        return processedRecordsCounter;
    }

    private void removeOrphanSpacePermissions(AtomicBoolean schedulingEnabled) throws ExecutionException, InterruptedException {
        OrphanFastSpacePermissionsRemover orphanFastSpacePermissionsRemover = new OrphanFastSpacePermissionsRemover(this.executor, this.platformTransactionManager, this.denormalisedSpacePermissionDao);
        orphanFastSpacePermissionsRemover.removeAllOrphanSpacePermissions(schedulingEnabled);
    }

    private void updateDenormalisedPermissionsForSpace(long spaceId, List<SpacePermissionDTOLight> realSpacePermissions, List<DenormalisedSpacePermission> denormalisedSpaceViewPermissions, List<DenormalisedSpacePermission> denormalisedSpaceEditPermissions, Map<String, Long> userKeyToSidMap, Map<String, Long> groupNameToSidMap) {
        Map<SpacePermissionType, List<SpacePermissionDTOLight>> realPermissionsGroupedByType = realSpacePermissions.stream().collect(Collectors.groupingBy(SpacePermissionDTOLight::getType));
        this.updateDenormalisedPermissionsForSpaceAndPermissionType(spaceId, SpacePermissionType.VIEWSPACE, realPermissionsGroupedByType.get((Object)SpacePermissionType.VIEWSPACE), denormalisedSpaceViewPermissions, userKeyToSidMap, groupNameToSidMap);
        this.updateDenormalisedPermissionsForSpaceAndPermissionType(spaceId, SpacePermissionType.EDITSPACE, realPermissionsGroupedByType.get((Object)SpacePermissionType.EDITSPACE), denormalisedSpaceEditPermissions, userKeyToSidMap, groupNameToSidMap);
    }

    private void updateDenormalisedPermissionsForSpaceAndPermissionType(long spaceId, SpacePermissionType permissionType, List<SpacePermissionDTOLight> realSpacePermissions, List<DenormalisedSpacePermission> denormalisedSpacePermissions, Map<String, Long> userKeyToSidMap, Map<String, Long> groupNameToSidMap) {
        EffectiveSpacePermissionsCalculator.EffectivePermissions effectivePermissions = EffectiveSpacePermissionsCalculator.calculateEffectivePermissions(realSpacePermissions);
        EffectiveSpacePermissionsCalculator.AccessType accessType = effectivePermissions.getAccessType();
        switch (accessType) {
            case RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS: {
                this.setDenormalisedPermissionsForRestrictedSpace(spaceId, permissionType, denormalisedSpacePermissions, userKeyToSidMap, groupNameToSidMap, effectivePermissions);
                break;
            }
            case ANONYMOUS: {
                this.makeSpaceAvailableForTheSpecialTypeOfUsers(spaceId, permissionType, denormalisedSpacePermissions, -1L);
                break;
            }
            case AUTHENTICATED_USER: {
                this.makeSpaceAvailableForTheSpecialTypeOfUsers(spaceId, permissionType, denormalisedSpacePermissions, -2L);
                break;
            }
            default: {
                throw new IllegalStateException("Undefined access type found: " + accessType + " while effective permissions were calculated");
            }
        }
    }

    private void setDenormalisedPermissionsForRestrictedSpace(long spaceId, SpacePermissionType permissionType, List<DenormalisedSpacePermission> denormalisedSpacePermissions, Map<String, Long> userKeyToSidMap, Map<String, Long> groupNameToSidMap, EffectiveSpacePermissionsCalculator.EffectivePermissions effectivePermissions) {
        HashSet fullListOfSidsWithRealAccess = new HashSet();
        fullListOfSidsWithRealAccess.addAll(effectivePermissions.getGroupsWithAccess().stream().map(groupNameToSidMap::get).collect(Collectors.toSet()));
        fullListOfSidsWithRealAccess.addAll(effectivePermissions.getUsersWithAccess().stream().map(userKeyToSidMap::get).collect(Collectors.toSet()));
        HashSet listOfSidWhichHaveToBeAddedAtTheEnd = new HashSet(fullListOfSidsWithRealAccess);
        int removedRecordsCount = 0;
        for (DenormalisedSpacePermission denormalisedSpacePermission : denormalisedSpacePermissions) {
            long sidId2 = denormalisedSpacePermission.getSpaceToSidMapId().getSidId();
            if (fullListOfSidsWithRealAccess.contains(sidId2)) {
                listOfSidWhichHaveToBeAddedAtTheEnd.remove(sidId2);
                continue;
            }
            this.denormalisedSpacePermissionDao.removeRecord(denormalisedSpacePermission, permissionType);
            ++removedRecordsCount;
        }
        listOfSidWhichHaveToBeAddedAtTheEnd.forEach(sidId -> this.denormalisedSpacePermissionDao.addRecord(new DenormalisedSpacePermission(spaceId, (long)sidId), permissionType));
        log.debug("Added {} records, removed {} records for space {}", new Object[]{listOfSidWhichHaveToBeAddedAtTheEnd.size(), removedRecordsCount, spaceId});
    }

    private void makeSpaceAvailableForTheSpecialTypeOfUsers(long spaceId, SpacePermissionType permissionType, List<DenormalisedSpacePermission> existingDenormalisedSpacePermissions, long specialSidId) {
        boolean foundFullAccessRecord = false;
        for (DenormalisedSpacePermission spacePermission : existingDenormalisedSpacePermissions) {
            if (spacePermission.getSpaceToSidMapId().getSidId() != specialSidId) {
                this.denormalisedSpacePermissionDao.removeRecord(spacePermission, permissionType);
                continue;
            }
            foundFullAccessRecord = true;
        }
        if (!foundFullAccessRecord) {
            this.denormalisedSpacePermissionDao.addRecord(new DenormalisedSpacePermission(spaceId, specialSidId), permissionType);
        }
    }

    private Map<String, Long> getOrCreateAllUserKeysForRealPermissions(List<SpacePermissionDTOLight> realSpacePermissions) {
        Set<String> allUserKeys = realSpacePermissions.stream().map(permission -> permission.getUserKey() != null ? permission.getUserKey().getStringValue() : null).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        return this.denormalisedSidManager.getAllUserSidsAndCreateThemIfRequired(allUserKeys);
    }

    private Map<String, Long> getOrCreateAllGroupNamesForRealPermissions(List<SpacePermissionDTOLight> realSpacePermissions) {
        Set<String> allGroupNames = realSpacePermissions.stream().map(SpacePermissionDTOLight::getGroupName).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        return this.denormalisedSidManager.getAllGroupSidsAndCreateThemIfRequired(allGroupNames);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    private boolean isDeadlineMissed(Long deadline) {
        return deadline != null && System.currentTimeMillis() > deadline;
    }

    private class SpaceDenormalisedPermissionsUpdaterWithoutTimeLimits
    implements Callable<Void> {
        private final List<Long> spaceIdList;

        public SpaceDenormalisedPermissionsUpdaterWithoutTimeLimits(List<Long> spaceIdList) {
            this.spaceIdList = spaceIdList;
        }

        @Override
        public Void call() {
            TransactionTemplate template = new TransactionTemplate(DenormalisedSpacePermissionsUpdater.this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
            template.execute(session -> DenormalisedSpacePermissionsUpdater.this.updateSpacePermissions(new HashSet<Long>(this.spaceIdList)));
            return null;
        }
    }

    private class NextSpaceGetter
    implements Callable<List<Long>> {
        private final Long latestProcessedId;

        public NextSpaceGetter(Long latestProcessedId) {
            this.latestProcessedId = latestProcessedId;
        }

        @Override
        public List<Long> call() {
            TransactionTemplate template = new TransactionTemplate(DenormalisedSpacePermissionsUpdater.this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
            return (List)template.execute(session -> this.getNextSpaceIds(this.latestProcessedId));
        }

        private List<Long> getNextSpaceIds(Long lastProcessedSpaceId) {
            Long startingId = lastProcessedSpaceId != null ? Long.valueOf(lastProcessedSpaceId + 1L) : null;
            return DenormalisedSpacePermissionsUpdater.this.spaceDao.findSpaceIdListWithIdGreaterOrEqual(startingId, INITIAL_SPACE_PROCESSING_LIMIT.get());
        }
    }
}

