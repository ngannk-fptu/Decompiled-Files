/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.hsqldb;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Preconditions;
import java.io.File;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseHomeDirectory
extends LazyReference<File> {
    private final AtlassianBootstrapManager bootstrapManager;
    private final String databaseDirectoryName;

    public DatabaseHomeDirectory(AtlassianBootstrapManager bootstrapManager, String databaseDirectoryName) {
        this.bootstrapManager = bootstrapManager;
        this.databaseDirectoryName = databaseDirectoryName;
    }

    protected File create() throws Exception {
        File databaseDirectory = new File(new File(this.bootstrapManager.getApplicationHome()), this.databaseDirectoryName);
        if (!databaseDirectory.exists()) {
            Preconditions.checkState((boolean)databaseDirectory.mkdirs(), (String)"failed to create directory %s", (Object[])new Object[]{databaseDirectory});
        }
        Preconditions.checkState((boolean)databaseDirectory.isDirectory(), (String)"%s is not a directory", (Object[])new Object[]{databaseDirectory});
        return databaseDirectory;
    }
}

