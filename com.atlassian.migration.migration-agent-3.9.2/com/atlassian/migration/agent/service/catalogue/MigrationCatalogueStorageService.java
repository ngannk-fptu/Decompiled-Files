/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.Tracker;
import com.atlassian.migration.agent.service.MCSUploadPath;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.model.CreateSinglepartFileResponse;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.catalogue.model.StorageFileDownloadResponse;
import com.atlassian.migration.agent.service.catalogue.model.StorageFileResponse;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import java.nio.file.Path;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class MigrationCatalogueStorageService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationCatalogueStorageService.class);
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;

    public MigrationCatalogueStorageService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
    }

    public MigrationCatalogueStorageFile uploadFileToMCS(String cloudId, String migrationId, Path filePath, String prefix, Tracker tracker) {
        return this.uploadFile(cloudId, migrationId, filePath, MCSUploadPath.MIGRATION_ID, prefix, tracker);
    }

    public MigrationCatalogueStorageFile uploadFileToMCS(String cloudId, String migrationId, Path filePath) {
        return this.uploadFile(cloudId, migrationId, filePath, MCSUploadPath.MIGRATION_ID, "", new Tracker[0]);
    }

    public MigrationCatalogueStorageFile uploadFileToMCSByMigrationScopeId(String cloudId, String migrationScopeId, Path filePath) {
        return this.uploadFile(cloudId, migrationScopeId, filePath, MCSUploadPath.MIGRATION_SCOPE_ID, "", new Tracker[0]);
    }

    private MigrationCatalogueStorageFile uploadFile(String cloudId, String migrationId, Path filePath, MCSUploadPath mcsUploadPath, String prefix, Tracker ... trackers) {
        try {
            String filename = filePath.getFileName().toString();
            long fileSize = filePath.toFile().length();
            log.info("Filesize of {} = {}", (Object)filename, (Object)fileSize);
            if (fileSize >= 0x6400000L) {
                StorageFileResponse storageFileResponse = this.enterpriseGatekeeperClient.createStorageFileInMCS(cloudId, migrationId, filename, mcsUploadPath, prefix);
                log.info("MULTIPART upload to MCS, fileName: {}", (Object)filename);
                this.enterpriseGatekeeperClient.uploadFileToMCS(cloudId, migrationId, storageFileResponse.getFileId(), storageFileResponse.getUploadId(), filePath, mcsUploadPath, trackers);
                return new MigrationCatalogueStorageFile(storageFileResponse.getFileId(), filename, fileSize);
            }
            CreateSinglepartFileResponse singlepartFileResponse = this.enterpriseGatekeeperClient.createStorageFileInMCSSinglePart(cloudId, migrationId, filename, mcsUploadPath, prefix);
            String uploadUrl = singlepartFileResponse.getUploadUrl();
            log.info("SINGLEPART upload to MCS, fileName: {}, url: {}", (Object)filename, (Object)uploadUrl);
            this.enterpriseGatekeeperClient.uploadFileToMCSSinglePart(uploadUrl, filePath.toFile(), trackers);
            return new MigrationCatalogueStorageFile(singlepartFileResponse.getFileId(), filename, fileSize);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Error upload file to MCS: {}", (Object)filePath.toAbsolutePath(), (Object)e);
            throw new RuntimeException(String.format("Error occurred when uploading file to MCS: %s", filePath.toAbsolutePath()), e);
        }
    }

    public String getFileDownloadUrlFromMCS(String cloudId, String migrationId, String fileId) {
        try {
            StorageFileDownloadResponse storageFileDownloadResponse = this.enterpriseGatekeeperClient.getFileDownloadUrlFromMCS(cloudId, migrationId, fileId, MCSUploadPath.MIGRATION_ID);
            return storageFileDownloadResponse.getDownloadUrl();
        }
        catch (Exception e) {
            log.error("Error retrieving file download URL from MCS. migrationId: {}, fileId: {}", new Object[]{migrationId, fileId, e});
            throw new RuntimeException("Error occurred when retrieving file download URL from MCS", e);
        }
    }

    public String getFileDownloadUrlFromMCSByMigrationScopeId(String cloudId, String migrationScopeId, String fileId) {
        try {
            StorageFileDownloadResponse storageFileDownloadResponse = this.enterpriseGatekeeperClient.getFileDownloadUrlFromMCS(cloudId, migrationScopeId, fileId, MCSUploadPath.MIGRATION_SCOPE_ID);
            return storageFileDownloadResponse.getDownloadUrl();
        }
        catch (Exception e) {
            log.error("Error retrieving file download URL from MCS by scope id. migrationScopeId: {}, fileId: {}", new Object[]{migrationScopeId, fileId, e});
            throw new RuntimeException("Error occurred when retrieving file download URL from MCS", e);
        }
    }
}

