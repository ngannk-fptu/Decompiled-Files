/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.domain.Product
 *  com.atlassian.cmpt.domain.ProductType
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.domain.Product;
import com.atlassian.cmpt.domain.ProductType;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.UserService;
import com.atlassian.migration.agent.service.stepexecutor.space.CloudSiteInfo;
import com.atlassian.migration.agent.service.stepexecutor.space.CreateTombstoneAccountRequest;
import com.atlassian.migration.agent.service.user.GlobalEmailFixesService;
import com.atlassian.migration.agent.service.user.GroupPermission;
import com.atlassian.migration.agent.service.user.MigrationUserDto;
import com.atlassian.migration.agent.service.user.MigrationUsers;
import com.atlassian.migration.agent.service.user.TombstoneFileParameters;
import com.atlassian.migration.agent.service.user.UsersMigrationException;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileManager;
import com.atlassian.migration.agent.service.user.request.v2.GroupProductPermission;
import com.atlassian.migration.agent.service.user.request.v2.MigrationGroupV2Dto;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UsersMigrationRequestBuilder {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UsersMigrationRequestBuilder.class);
    private final UserService userService;
    private final GroupManager groupManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final SENSupplier senSupplier;
    private final PluginVersionManager pluginVersionManager;
    private final SystemInformationService systemInformationService;
    private final GlobalEmailFixesService globalEmailFixesService;
    private final UsersToTombstoneFileManager usersToTombstoneFileManager;

    public UsersMigrationRequestBuilder(GroupManager groupManager, SpacePermissionManager spacePermissionManager, UserService userService, UserGroupExtractFacade userGroupExtractFacade, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SENSupplier senSupplier, PluginVersionManager pluginVersionManager, SystemInformationService systemInformationService, GlobalEmailFixesService globalEmailFixesService, UsersToTombstoneFileManager usersToTombstoneFileManager) {
        this.groupManager = groupManager;
        this.spacePermissionManager = spacePermissionManager;
        this.userService = userService;
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.senSupplier = senSupplier;
        this.pluginVersionManager = pluginVersionManager;
        this.systemInformationService = systemInformationService;
        this.globalEmailFixesService = globalEmailFixesService;
        this.usersToTombstoneFileManager = usersToTombstoneFileManager;
    }

    public UsersMigrationV2FilePayload createUsersMigrationRequestV2FilePayload(Set<String> spaceKeys, String cloudId, TombstoneFileParameters params, Optional<GlobalEntityType> globalEntityType) {
        if (spaceKeys.isEmpty()) {
            return this.createMigrateAllUsersMigrationRequestV2(cloudId, params);
        }
        return this.createScopedUsersMigrationRequestV2(spaceKeys, cloudId, params, globalEntityType);
    }

    public UsersMigrationV2FilePayload createContextForInvalidEmailChecker(Set<String> spaceKeys, Optional<GlobalEntityType> globalEntityType) {
        if (spaceKeys.isEmpty()) {
            return this.createInvalidEmailCheckerPayload();
        }
        return this.createScopedInvalidEmailCheckerPayload(spaceKeys, globalEntityType);
    }

    public UsersMigrationV2FilePayload createUsersMigrationRequestFilePayloadForEmails(List<String> emails) {
        HashSet<String> emailsSet = new HashSet<String>(emails);
        List<MigrationUser> users = this.userService.getAllUsers().stream().filter(user -> emailsSet.contains(user.getEmail())).collect(Collectors.toList());
        Map<MigrationUser, List<MigrationUser>> distinctUsers = this.mergeDuplicateUsers(users);
        return new UsersMigrationV2FilePayload(this.toMigrationRequestUsersWithAdditionalMappings(distinctUsers), Collections.emptyList(), Collections.emptyMap());
    }

    public UsersMigrationV2Request createUsersMigrationRequestV2(String migrationScopeId, String migrationId, String fileId, String cloudId, UsersMigrationV2FilePayload usersMigrationV2FilePayload) {
        try {
            String userMigrationUploadedUrl = this.userService.saveUsersMigrationV2PayloadToFileAndFetchDownloadUrl(migrationId, fileId, cloudId, usersMigrationV2FilePayload);
            return new UsersMigrationV2Request(this.senSupplier.get(), ProductType.CONFLUENCE, migrationScopeId, new URL(userMigrationUploadedUrl), this.pluginVersionManager.getPluginVersion(), this.systemInformationService.getConfluenceInfo().getVersion(), this.systemInformationService.getDatabaseInfo().getVersion());
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception exception) {
            throw new UsersMigrationException("Error while creating user migration request for migration: " + migrationId, exception);
        }
    }

    public CreateTombstoneAccountRequest createTombstoneAccountCreationRequest(CloudSite cloudSite) {
        CloudSiteInfo cloudSiteInfo = new CloudSiteInfo(cloudSite.getCloudId(), cloudSite.getCloudUrl());
        return new CreateTombstoneAccountRequest(this.senSupplier.get(), cloudSiteInfo);
    }

    private UsersMigrationV2FilePayload createMigrateAllUsersMigrationRequestV2(String cloudId, TombstoneFileParameters params) {
        MigrationUsers migrationUsers = this.getUsersForMigration(cloudId);
        Collection<Group> serverGroups = this.getAllServerGroups();
        if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
            return this.createUsersMigrationRequestV2ForGlobalEmailFixes(migrationUsers, serverGroups, params);
        }
        return new UsersMigrationV2FilePayload(this.toMigrationRequestUsers(migrationUsers.getUsersToMigrate()), this.toMigrationRequestGroupsV2(serverGroups), this.getGroupsMembership(serverGroups, migrationUsers.getUsersToMigrate()));
    }

    private UsersMigrationV2FilePayload createInvalidEmailCheckerPayload() {
        List<MigrationUser> users = this.userService.getAllUsers();
        Collection<Group> serverGroups = this.getAllServerGroups();
        return new UsersMigrationV2FilePayload(this.toMigrationRequestUsers(users), this.toMigrationRequestGroupsV2(serverGroups), this.getGroupsMembership(serverGroups, users));
    }

    private UsersMigrationV2FilePayload createScopedUsersMigrationRequestV2(Set<String> spaceKeys, String cloudId, TombstoneFileParameters params, Optional<GlobalEntityType> globalEntityType) {
        ArrayList<String> spaceKeyList = new ArrayList<String>(spaceKeys);
        Set<String> userKeys = this.getUserKeysForSpacesAndGlobalEntities(spaceKeyList, globalEntityType);
        MigrationUsers migrationUsers = this.getMigrationUsers(userKeys, cloudId);
        List<Group> filteredGroups = this.getFilteredGroups(spaceKeyList, globalEntityType);
        if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
            return this.createUsersMigrationRequestV2ForGlobalEmailFixes(migrationUsers, filteredGroups, params);
        }
        return new UsersMigrationV2FilePayload(this.toMigrationRequestUsers(migrationUsers.getUsersToMigrate()), this.toMigrationRequestGroupsV2(filteredGroups), this.getGroupsMembership(filteredGroups, migrationUsers.getUsersToMigrate()));
    }

    private UsersMigrationV2FilePayload createUsersMigrationRequestV2ForGlobalEmailFixes(MigrationUsers migrationUsers, Collection<Group> groups, TombstoneFileParameters params) {
        Map<MigrationUser, List<MigrationUser>> distinctUsers = this.mergeDuplicateUsers(migrationUsers.getUsersToMigrate());
        Collection<MigrationUserDto> distinctMigrationRequestUsers = this.toMigrationRequestUsersWithAdditionalMappings(distinctUsers);
        List<MigrationUser> users = this.fixEmailCasingForDuplicateEmails(distinctUsers.keySet(), migrationUsers.getUsersToMigrate());
        String planId = params.getPlanId();
        if (planId != null) {
            this.saveUsersToTombstoneToFile(migrationUsers.getUsersToTombstone(), planId);
        }
        return new UsersMigrationV2FilePayload(distinctMigrationRequestUsers, this.toMigrationRequestGroupsV2(groups), this.getGroupsMembership(groups, users));
    }

    private UsersMigrationV2FilePayload createScopedInvalidEmailCheckerPayload(Set<String> spaceKeys, Optional<GlobalEntityType> globalEntityType) {
        ArrayList<String> spaceKeyList = new ArrayList<String>(spaceKeys);
        Collection<MigrationUser> users = this.getMigrationUsersForInvalidEmailsChecker(spaceKeyList, globalEntityType);
        List<Group> filteredGroups = this.getFilteredGroups(spaceKeyList, globalEntityType);
        return new UsersMigrationV2FilePayload(this.toMigrationRequestUsers(users), this.toMigrationRequestGroupsV2(filteredGroups), this.getGroupsMembership(filteredGroups, users));
    }

    private List<Group> getFilteredGroups(List<String> spaceKeyList, Optional<GlobalEntityType> globalEntityType) {
        if (this.migrationDarkFeaturesManager.disableScopedGroupMigration()) {
            return Collections.emptyList();
        }
        Set<String> groups = this.userGroupExtractFacade.getGroupsFromSpacesAndGlobalEntities(spaceKeyList, globalEntityType);
        return this.getFilteredGroups(groups);
    }

    private Set<String> getUserKeysForSpacesAndGlobalEntities(List<String> spaceKeyList, Optional<GlobalEntityType> globalEntityType) {
        return this.userGroupExtractFacade.getUsersFromSpacesAndGlobalEntities(spaceKeyList, globalEntityType);
    }

    private MigrationUsers getMigrationUsers(Set<String> userKeys, String cloudId) {
        MigrationUsers migrationUsers = this.getUsersForMigration(cloudId);
        List<MigrationUser> usersToMigrate = migrationUsers.getUsersToMigrate().stream().filter(migrationUser -> userKeys.contains(migrationUser.getUserKey())).collect(Collectors.toList());
        List<MigrationUser> usersToTombstone = migrationUsers.getUsersToTombstone().stream().filter(migrationUser -> userKeys.contains(migrationUser.getUserKey())).collect(Collectors.toList());
        return new MigrationUsers(usersToMigrate, usersToTombstone);
    }

    private Collection<MigrationUser> getMigrationUsersForInvalidEmailsChecker(List<String> spaceKeyList, Optional<GlobalEntityType> globalEntityType) {
        Set<String> userKeys = this.userGroupExtractFacade.getUsersFromSpacesAndGlobalEntities(spaceKeyList, globalEntityType);
        return this.userService.getAllUsers().stream().filter(migrationUser -> userKeys.contains(migrationUser.getUserKey())).collect(Collectors.toList());
    }

    private List<Group> getFilteredGroups(Set<String> groups) {
        Collection<Group> serverGroups = this.getAllServerGroups();
        return serverGroups.stream().filter(serverGroup -> groups.contains(serverGroup.getName())).collect(Collectors.toList());
    }

    @VisibleForTesting
    MigrationUsers getUsersForMigration(String cloudId) {
        if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
            return this.globalEmailFixesService.getUsersForGlobalEmailFixes(this.userService.getAllUsers(), cloudId);
        }
        if (!this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes() && this.migrationDarkFeaturesManager.shouldHandleInvalidAndDuplicateEmailUsers()) {
            Collection<MigrationUser> usersToMigrate = this.userService.getAllUsersExcludingInvalidEmailUsers().stream().collect(Collectors.toMap(user -> IdentityAcceptedEmailValidator.cleanse((String)user.getEmail()), Function.identity(), (older, newer) -> older)).values();
            return new MigrationUsers(usersToMigrate, Collections.emptyList());
        }
        return new MigrationUsers(this.userService.getAllUsers(), Collections.emptyList());
    }

    private void saveUsersToTombstoneToFile(Collection<MigrationUser> usersToTombstone, String planId) {
        this.usersToTombstoneFileManager.saveToFile(planId, usersToTombstone);
    }

    private Collection<MigrationGroupV2Dto> toMigrationRequestGroupsV2(Collection<Group> groups) {
        return groups.stream().map(group -> {
            Collection groupPermissions = this.spacePermissionManager.getAllPermissionsForGroup(group.getName()).stream().flatMap(UsersMigrationRequestBuilder::mapSpacePermissionToGroupPermissions).collect(Collectors.toSet());
            List<GroupProductPermission> groupProductPermissions = Collections.emptyList();
            if (!groupPermissions.isEmpty()) {
                groupProductPermissions = Collections.singletonList(new GroupProductPermission(Product.CONFLUENCE, groupPermissions));
            }
            return new MigrationGroupV2Dto(group.getName(), groupProductPermissions);
        }).collect(Collectors.toList());
    }

    private Map<String, Collection<String>> getGroupsMembership(Collection<Group> groups, Collection<MigrationUser> users) {
        return groups.stream().collect(Collectors.toMap(Entity::getName, group -> {
            try {
                HashSet userIdsOfGroup = Sets.newHashSet((Iterable)this.groupManager.getMemberNames(group));
                return users.stream().filter(it -> userIdsOfGroup.contains(it.getUsername())).map(MigrationUser::getEmail).collect(Collectors.toSet());
            }
            catch (EntityException e) {
                throw new RuntimeException(String.format("Couldn't retrieve members of group %s", group.getName()), e);
            }
        }));
    }

    private Collection<Group> getAllServerGroups() {
        ArrayList serverGroups;
        try {
            serverGroups = Lists.newArrayList((Iterable)this.groupManager.getGroups());
        }
        catch (EntityException e) {
            throw new RuntimeException("Couldn't retrieve groups", e);
        }
        return serverGroups;
    }

    private Collection<MigrationUserDto> toMigrationRequestUsers(Collection<MigrationUser> users) {
        return users.stream().map(MigrationUserDto::from).collect(Collectors.toList());
    }

    private Collection<MigrationUserDto> toMigrationRequestUsersWithAdditionalMappings(Map<MigrationUser, List<MigrationUser>> users) {
        return users.entrySet().stream().map(user -> MigrationUserDto.from((MigrationUser)user.getKey(), (List)user.getValue())).collect(Collectors.toList());
    }

    private Map<MigrationUser, List<MigrationUser>> mergeDuplicateUsers(Collection<MigrationUser> users) {
        return users.stream().sorted(Comparator.comparing(MigrationUser::isActive, Comparator.reverseOrder())).collect(Collectors.groupingBy(v -> UsersMigrationRequestBuilder.sanitizeDuplicatedEmailForMerge(v.getEmail()))).entrySet().stream().collect(Collectors.toMap(e -> (MigrationUser)((List)e.getValue()).get(0), e -> ((List)e.getValue()).size() > 1 ? ((List)e.getValue()).subList(1, ((List)e.getValue()).size()) : Collections.emptyList()));
    }

    private List<MigrationUser> fixEmailCasingForDuplicateEmails(Collection<MigrationUser> distinctUsers, Collection<MigrationUser> allUsers) {
        Map<String, String> distinctUsersEmailMap = distinctUsers.stream().collect(Collectors.toMap(user -> UsersMigrationRequestBuilder.sanitizeDuplicatedEmailForMerge(user.getEmail()), MigrationUser::getEmail));
        return allUsers.stream().map(user -> new MigrationUser(user.getUserKey(), user.getUsername(), user.getFullName(), (String)distinctUsersEmailMap.get(UsersMigrationRequestBuilder.sanitizeDuplicatedEmailForMerge(user.getEmail())), user.isActive())).collect(Collectors.toList());
    }

    private static String sanitizeDuplicatedEmailForMerge(String email) {
        return IdentityAcceptedEmailValidator.cleanse((String)email.toLowerCase(Locale.ENGLISH));
    }

    private static Stream<GroupPermission> mapSpacePermissionToGroupPermissions(SpacePermission spacePermission) {
        switch (spacePermission.getType()) {
            case "USECONFLUENCE": {
                return Stream.of(GroupPermission.HAS_PRODUCT_ACCESS);
            }
            case "ADMINISTRATECONFLUENCE": {
                return Stream.of(GroupPermission.PRODUCT_ADMIN);
            }
            case "SYSTEMADMINISTRATOR": {
                return Stream.of(GroupPermission.SYSTEM_ADMIN);
            }
        }
        return Stream.empty();
    }
}

