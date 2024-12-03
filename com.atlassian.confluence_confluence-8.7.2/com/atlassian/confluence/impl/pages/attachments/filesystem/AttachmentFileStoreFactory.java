/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.impl.s3.S3Config
 *  com.atlassian.dc.filestore.impl.s3.S3FileStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemAdapter;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV003;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV004;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.ObjectStorageAttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.S3ConfigFactory;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.impl.s3.S3Config;
import com.atlassian.dc.filestore.impl.s3.S3FileStore;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentFileStoreFactory {
    private static final Logger log = LoggerFactory.getLogger(AttachmentFileStoreFactory.class);
    public static final String S3_ATTACHMENT_ROOT = "confluence/attachments/v4";
    public static final String CONFLUENCE_ATTACHMENTS_VER004_DARK_FEATURE_DISABLED = "confluence.disable-attachments-ver004";

    private AttachmentFileStoreFactory() {
    }

    public static AttachmentDataFileSystem getInstance(ContentDirectoryStructureAttachmentDataFileSystemV003 structureV3, ContentDirectoryStructureAttachmentDataFileSystemV004 structureV4, ApplicationConfiguration appConfig, DarkFeaturesManager darkFeaturesManager, ClusterConfigurationHelperInternal clusterConfigurationHelper, ZduManager zduManager, AtlassianBootstrapManager bootstrapManager, LicenseService licenseService) throws IOException {
        Optional<S3Config> s3Config = S3ConfigFactory.getInstance(appConfig, clusterConfigurationHelper, licenseService);
        if (s3Config.isPresent()) {
            log.info("Attachment data operations will be performed using S3 Object storage Filestore");
            FileStore.Path attachmentPath = new S3FileStore(s3Config.get()).path(new String[]{S3_ATTACHMENT_ROOT});
            System.setProperty("AttachmentStorageType", "s3.object.based.attachments.storage");
            return new ObjectStorageAttachmentDataFileSystem(attachmentPath);
        }
        if (AttachmentFileStoreFactory.isAttachmentsVer004FeatureDisabled(darkFeaturesManager)) {
            log.info("Attachment data operations will be performed using v003 Filestore");
            System.setProperty("AttachmentStorageType", "file.system.based.attachments.storage");
            return structureV3;
        }
        if (bootstrapManager.getString("attachments.dir") != null && structureV3.getRootPath().exists()) {
            log.info("Attachment data operations will be performed using v4 Adapter Filestore");
            System.setProperty("AttachmentStorageType", "file.system.based.attachments.storage");
            return ContentDirectoryStructureAttachmentDataFileSystemAdapter.create(structureV3, structureV4, zduManager);
        }
        log.info("Attachment data operations will be performed using v4 Filestore");
        System.setProperty("AttachmentStorageType", "file.system.based.attachments.storage");
        return structureV4;
    }

    private static boolean isAttachmentsVer004FeatureDisabled(DarkFeaturesManager salDarkFeatureManager) {
        return salDarkFeatureManager.getSiteDarkFeatures().isFeatureEnabled(CONFLUENCE_ATTACHMENTS_VER004_DARK_FEATURE_DISABLED);
    }
}

