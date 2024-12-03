/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.confluence.impl.filestore.AbstractFileStoreFactory;
import com.atlassian.confluence.setup.BootstrapManager;
import java.nio.file.Path;
import java.nio.file.Paths;

@Deprecated
public class FileStoreFactory
extends AbstractFileStoreFactory {
    private final BootstrapManager bootstrapManager;

    public FileStoreFactory(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    protected Path getConfluenceHomePath() {
        return Paths.get(this.bootstrapManager.getConfluenceHome(), new String[0]);
    }

    @Override
    protected Path getLocalHomePath() {
        return this.bootstrapManager.getLocalHome().toPath();
    }

    @Override
    protected Path getSharedHomePath() {
        return this.bootstrapManager.getSharedHome().toPath();
    }
}

