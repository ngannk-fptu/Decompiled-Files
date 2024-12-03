/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemAccess
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.confluence.impl.filestore.HomePathPlaceholderResolver;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.dc.filestore.api.compat.FilesystemAccess;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

final class ConfluenceFileStoreDirectories
implements ConfluenceDirectories {
    private final HomePathPlaceholderResolver homePathPlaceholderResolver;
    private final BootstrapManager bootstrapManager;

    public ConfluenceFileStoreDirectories(HomePathPlaceholderResolver homePathPlaceholderResolver, BootstrapManager bootstrapManager) {
        this.homePathPlaceholderResolver = Objects.requireNonNull(homePathPlaceholderResolver);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
    }

    @Override
    public Path getTempDirectory() {
        return this.resolveBootstrapPathProperty("struts.multipart.saveDir");
    }

    @Override
    public Path getLuceneIndexDirectory() {
        return this.resolveBootstrapPathProperty("lucene.index.dir");
    }

    private Path resolveBootstrapPathProperty(String bootstrapPropertyName) {
        String pathStr = this.bootstrapManager.getString(bootstrapPropertyName);
        if (pathStr == null) {
            throw new IllegalStateException(bootstrapPropertyName + " property has not been defined");
        }
        return this.homePathPlaceholderResolver.resolveFileStorePlaceHolders(pathStr).map(FilesystemAccess::asJavaPath).orElseGet(() -> Paths.get(pathStr, new String[0]));
    }
}

