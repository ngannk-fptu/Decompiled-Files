/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentFileStoreFactory;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV003;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV004;
import com.atlassian.confluence.impl.pages.attachments.filesystem.IpdAttachmentDataFileSystem;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import java.io.IOException;
import java.time.Clock;

public class IpdAttachmentFileStoreFactory {
    public static AttachmentDataFileSystem getInstance(ContentDirectoryStructureAttachmentDataFileSystemV003 structureV3, ContentDirectoryStructureAttachmentDataFileSystemV004 structureV4, ApplicationConfiguration appConfig, DarkFeaturesManager darkFeaturesManager, ClusterConfigurationHelperInternal clusterConfigurationHelper, ZduManager zduManager, AtlassianBootstrapManager bootstrapManager, LicenseService licenseService, IpdMainRegistry ipdMainRegistry, Clock clock) throws IOException {
        return new IpdAttachmentDataFileSystem(AttachmentFileStoreFactory.getInstance(structureV3, structureV4, appConfig, darkFeaturesManager, clusterConfigurationHelper, zduManager, bootstrapManager, licenseService), ipdMainRegistry, clock);
    }
}

