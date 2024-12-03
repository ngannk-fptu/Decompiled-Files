/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimaps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.migration.agent.entity.UserMapping;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.email.InvalidEmailUserService;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.user.UsersGroupsMigrationFileManager;
import com.atlassian.migration.agent.service.user.UsersMigrationException;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.atlassian.migration.agent.store.UserMappingStore;
import com.atlassian.user.User;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UserService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserAccessor userAccessor;
    private final InvalidEmailUserService invalidEmailUserService;
    private final UsersGroupsMigrationFileManager usersGroupsMigrationFileManager;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;
    private final UserMappingStore userMappingStore;

    public UserService(UserAccessor userAccessor, InvalidEmailUserService invalidEmailUserService, UsersGroupsMigrationFileManager usersGroupsMigrationFileManager, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMappingStore userMappingStore) {
        this.userAccessor = userAccessor;
        this.invalidEmailUserService = invalidEmailUserService;
        this.usersGroupsMigrationFileManager = usersGroupsMigrationFileManager;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
        this.userMappingStore = userMappingStore;
    }

    public List<MigrationUser> getAllUsers() {
        ListMultimap<String, UserMapping> userMappingsByLowerUsername = this.getUserMappingsByLowerUsername();
        return StreamSupport.stream(this.userAccessor.getUsers().spliterator(), false).map(user -> this.userToMigrationUser((User)user, userMappingsByLowerUsername)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public List<MigrationUser> getAllUsersExcludingInvalidEmailUsers() {
        Set<String> userNamesOfInvalidEmailUsers = this.invalidEmailUserService.findAllUserNamesOfInvalidEmailUsers();
        return this.getAllUsers().stream().filter(user -> user.getEmail() != null && !user.getEmail().trim().isEmpty()).filter(migrationUser -> !userNamesOfInvalidEmailUsers.contains(migrationUser.getUsername())).collect(Collectors.toList());
    }

    public String saveUsersMigrationV2PayloadToFileAndFetchDownloadUrl(String migrationId, String fileId, String cloudId, UsersMigrationV2FilePayload usersMigrationV2FilePayload) {
        try {
            this.usersGroupsMigrationFileManager.saveUsersMigrationPayloadToFile(fileId, usersMigrationV2FilePayload);
            return this.uploadAndFetchDownloadUrlFromMCS(cloudId, migrationId, fileId);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception exception) {
            throw new UsersMigrationException("Couldn't save request to file or fetch download url from MCS for migration: " + migrationId, exception);
        }
    }

    private ListMultimap<String, UserMapping> getUserMappingsByLowerUsername() {
        return Multimaps.index(this.userMappingStore.getUserMappings().stream().filter(userMapping -> userMapping.getLowerUsername() != null).iterator(), UserMapping::getLowerUsername);
    }

    private Optional<MigrationUser> userToMigrationUser(User user, ListMultimap<String, UserMapping> userMappingsByLowerUsername) {
        return this.getUserKey(user, userMappingsByLowerUsername).map(userKey -> this.createMigrationUser(user, (String)userKey));
    }

    private Optional<String> getUserKey(User user, ListMultimap<String, UserMapping> userMappingsByLowerUsername) {
        List userMappings = userMappingsByLowerUsername.get((Object)user.getName().toLowerCase());
        if (userMappings.size() == 1) {
            return Optional.of(((UserMapping)userMappings.get(0)).getUserKey());
        }
        ConfluenceUser confluenceUser = this.userAccessor.getUserByName(user.getName());
        if (confluenceUser == null) {
            return Optional.empty();
        }
        return Optional.of(confluenceUser.getKey().getStringValue());
    }

    private MigrationUser createMigrationUser(User user, String userKey) {
        boolean isActive = !this.userAccessor.isDeactivated(user);
        return new MigrationUser(userKey, user.getName(), Optional.ofNullable(user.getFullName()).orElse(""), Optional.ofNullable(user.getEmail()).orElse(""), isActive);
    }

    private String uploadAndFetchDownloadUrlFromMCS(String cloudId, String migrationId, String localFileId) {
        Path userGroupMigrationFile = this.usersGroupsMigrationFileManager.getUsersMigrationFile(localFileId);
        try {
            MigrationCatalogueStorageFile uploadedFile = this.migrationCatalogueStorageService.uploadFileToMCS(cloudId, migrationId, userGroupMigrationFile);
            log.info("File uploaded to MCS fileId: {}, name: {}, size: {}", new Object[]{uploadedFile.getFileId(), uploadedFile.getName(), uploadedFile.getSize()});
            String string = this.migrationCatalogueStorageService.getFileDownloadUrlFromMCS(cloudId, migrationId, uploadedFile.getFileId());
            return string;
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UsersMigrationException("Couldn't upload file and fetch downloadable url from MCS for migrationId: " + migrationId, e);
        }
        finally {
            this.usersGroupsMigrationFileManager.cleanupUsersMigrationPayloadFile(localFileId);
        }
    }
}

