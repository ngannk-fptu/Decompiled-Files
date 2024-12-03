/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.extract.ExtractionAnalyticsService;
import com.atlassian.migration.agent.service.extract.UserExtractionService;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ParametersAreNonnullByDefault
public class UserExtractionServiceImpl
implements UserExtractionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserExtractionServiceImpl.class);
    @VisibleForTesting
    static final String USER_EXTRACTION_METRIC_NAME = "migration.sli.user.extraction";
    @VisibleForTesting
    static final String GLOBAL_PAGE_TEMPLATES_USERS_QUERY = "SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and MODULEKEY is null";
    @VisibleForTesting
    static final String CUSTOM_SYSTEM_TEMPLATES_USERS_QUERY = "SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message')";
    @VisibleForTesting
    static final String GLOBAL_TEMPLATES_QUERY = "SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and (MODULEKEY is null or MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message'))";
    @VisibleForTesting
    static final String GLOBAL_TEMPLATE_PERMISSIONS_USERS_QUERY = "SELECT PERMUSERNAME, CREATOR, LASTMODIFIER FROM SPACEPERMISSIONS \nWHERE PERMTYPE in ('SYSTEMADMINISTRATOR', 'ADMINISTRATECONFLUENCE')\nAND SPACEID is null\nAND PERMGROUPNAME is null \nAND PERMUSERNAME is NOT null ";
    @VisibleForTesting
    static final Map<GlobalEntityType, String> templateQueryMap = ImmutableMap.of((Object)((Object)GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES), (Object)"SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and (MODULEKEY is null or MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message'))", (Object)((Object)GlobalEntityType.GLOBAL_TEMPLATES), (Object)"SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and MODULEKEY is null", (Object)((Object)GlobalEntityType.SYSTEM_TEMPLATES), (Object)"SELECT CREATOR, LASTMODIFIER FROM PAGETEMPLATES WHERE SPACEID is null and MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message')");
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExtractionAnalyticsService extractionAnalyticsService;
    private final SpacePermissionStore spacePermissionStore;

    public UserExtractionServiceImpl(ConfluenceWrapperDataSource dataSource, ExtractionAnalyticsService extractionAnalyticsService, SpacePermissionStore spacePermissionStore) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate((DataSource)((Object)dataSource));
        this.extractionAnalyticsService = extractionAnalyticsService;
        this.spacePermissionStore = spacePermissionStore;
    }

    @VisibleForTesting
    UserExtractionServiceImpl(NamedParameterJdbcTemplate jdbcTemplate, ExtractionAnalyticsService extractionAnalyticsService, SpacePermissionStore spacePermissionStore) {
        this.jdbcTemplate = jdbcTemplate;
        this.extractionAnalyticsService = extractionAnalyticsService;
        this.spacePermissionStore = spacePermissionStore;
    }

    @Override
    public Set<String> getUsersWithPermissionFromSpaces(@Nonnull Set<String> spaceKeys) {
        Instant start = Instant.now();
        log.info("Get users with permissions from {} spaces", (Object)spaceKeys.size());
        HashSet<String> usersWithPermissions = new HashSet<String>();
        usersWithPermissions.addAll(this.spacePermissionStore.getUsersWithSpacePermissions(spaceKeys));
        usersWithPermissions.addAll(this.spacePermissionStore.getMembersUnderGroupsWithSpacePermissions(spaceKeys));
        long elapsedTime = start.until(Instant.now(), ChronoUnit.MILLIS);
        this.extractionAnalyticsService.sendExtractionAnalytics(USER_EXTRACTION_METRIC_NAME, elapsedTime, usersWithPermissions.size());
        log.info("Took {} to retrieve {} users with space permissions from {} spaces", new Object[]{elapsedTime, usersWithPermissions.size(), spaceKeys.size()});
        return usersWithPermissions;
    }

    @Override
    public Set<String> getUsersFromGlobalEntities(@Nonnull GlobalEntityType globalEntityType) {
        Set<String> userKeys = this.extractUsersFromQueryResults(Collections.emptyMap(), templateQueryMap.get((Object)globalEntityType));
        return userKeys;
    }

    private Set<String> getUsersFromGroupsWithGTPermission() {
        Instant start = Instant.now();
        log.info("Get users from groups with GT permissions");
        HashSet<String> usersWithPermissions = new HashSet<String>(this.spacePermissionStore.getMembersUnderGroupsWithGlobalEntitiesPermissions());
        Instant end = Instant.now();
        log.info("Took {} to retrieve {} users from groups with GT permissions", (Object)Duration.between(start, end).toMillis(), (Object)usersWithPermissions.size());
        return usersWithPermissions;
    }

    private <T> Set<String> extractUsersFromQueryResults(Map<String, T> stringMap, String ... queries) {
        HashSet<String> userKeys = new HashSet<String>();
        for (String query : queries) {
            Set<String> newUsers = this.extractUsersFromQuery(query, stringMap);
            userKeys.addAll(newUsers);
        }
        return userKeys;
    }

    private <T> Set<String> extractUsersFromQuery(String query, Map<String, T> params) {
        HashSet<String> users = new HashSet<String>();
        for (Map row : this.jdbcTemplate.queryForList(query, params)) {
            for (Object value : row.values()) {
                if (value == null) continue;
                users.add(value.toString());
            }
        }
        return users;
    }
}

