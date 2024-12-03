/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.h2;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.LazyReference;
import java.io.File;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseHomeDirectory
extends LazyReference<File> {
    private final AtlassianBootstrapManager bootstrapManager;
    private final String databaseDirectoryName;

    public DatabaseHomeDirectory(@Nonnull AtlassianBootstrapManager bootstrapManager, @Nonnull String databaseDirectoryName) {
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.databaseDirectoryName = Objects.requireNonNull(databaseDirectoryName);
    }

    @Nonnull
    protected File create() {
        File databaseDirectory = new File(new File(this.bootstrapManager.getApplicationHome()), this.databaseDirectoryName);
        if (!databaseDirectory.exists()) {
            Preconditions.checkState((boolean)databaseDirectory.mkdirs(), (String)"failed to create directory %s", (Object)databaseDirectory);
        }
        Preconditions.checkState((boolean)databaseDirectory.isDirectory(), (String)"%s is not a directory", (Object)databaseDirectory);
        return databaseDirectory;
    }
}

