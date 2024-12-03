/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.Entity
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.service.extract.ExtractionAnalyticsService;
import com.atlassian.migration.agent.service.extract.GroupExtractionService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.Entity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class GroupExtractionServiceImpl
implements GroupExtractionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GroupExtractionServiceImpl.class);
    private static final String GROUP_EXTRACTION_METRIC_NAME = "migration.sli.group.extraction";
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionsManager;
    private final TransactionTemplate transactionTemplate;
    private final ExtractionAnalyticsService extractionAnalyticsService;

    public GroupExtractionServiceImpl(SpaceManager spaceManager, SpacePermissionManager spacePermissionsManager, TransactionTemplate transactionTemplate, ExtractionAnalyticsService extractionAnalyticsService) {
        this.spaceManager = spaceManager;
        this.spacePermissionsManager = spacePermissionsManager;
        this.transactionTemplate = transactionTemplate;
        this.extractionAnalyticsService = extractionAnalyticsService;
    }

    @Override
    public Set<String> getGroupsFromSpace(String spaceKey) {
        return this.getGroupNamesWithSpacePermission(spaceKey);
    }

    @Override
    public Map<String, Set<String>> getGroupsFromSpaces(List<String> spaceKeys) {
        return spaceKeys.stream().collect(Collectors.toMap(space -> space, this::getGroupNamesWithSpacePermission));
    }

    private Set<String> getGroupNamesWithSpacePermission(String spaceKey) {
        Instant start = Instant.now();
        Set groupsWithPermission = (Set)this.transactionTemplate.execute(() -> {
            Space space = this.spaceManager.getSpace(spaceKey);
            Collection groups = this.spacePermissionsManager.getGroupsWithPermissions(space);
            return groups.stream().map(Entity::getName).collect(Collectors.toSet());
        });
        long elapsedTime = start.until(Instant.now(), ChronoUnit.MILLIS);
        this.extractionAnalyticsService.sendExtractionAnalytics(GROUP_EXTRACTION_METRIC_NAME, elapsedTime, groupsWithPermission.size());
        return groupsWithPermission;
    }

    @Override
    public Set<String> getGroupsFromGlobalEntities() {
        Instant start = Instant.now();
        Set groupsWithPermission = (Set)this.transactionTemplate.execute(() -> {
            HashSet groupName = new HashSet();
            groupName.addAll(this.spacePermissionsManager.getGroupsForPermissionType("ADMINISTRATECONFLUENCE", null).keySet());
            groupName.addAll(this.spacePermissionsManager.getGroupsForPermissionType("SYSTEMADMINISTRATOR", null).keySet());
            return groupName;
        });
        long elapsedTime = start.until(Instant.now(), ChronoUnit.MILLIS);
        log.info("Took {} to retrieve {} groups with GT permissions", (Object)elapsedTime, (Object)groupsWithPermission.size());
        return groupsWithPermission;
    }
}

