/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import java.io.File;
import java.nio.file.Path;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface ConfluenceDirectories {
    public Path getTempDirectory();

    @Internal
    public Path getLuceneIndexDirectory();

    @Deprecated
    public static File getLegacyTempDirectory() {
        return ConfluenceDirectories.getLegacyTempDirectory(BootstrapUtils.getBootstrapManager());
    }

    @Deprecated
    public static File getLegacyTempDirectory(AtlassianBootstrapManager bootstrapManager) {
        return new File(bootstrapManager.getFilePathProperty("struts.multipart.saveDir"));
    }

    @Deprecated
    @Internal
    public static File getLegacyLuceneIndexDirectory() {
        return ConfluenceDirectories.getLegacyLuceneIndexDirectory(BootstrapUtils.getBootstrapManager());
    }

    @Deprecated
    @Internal
    public static File getLegacyLuceneIndexDirectory(AtlassianBootstrapManager bootstrapManager) {
        return new File(bootstrapManager.getFilePathProperty("lucene.index.dir"));
    }
}

