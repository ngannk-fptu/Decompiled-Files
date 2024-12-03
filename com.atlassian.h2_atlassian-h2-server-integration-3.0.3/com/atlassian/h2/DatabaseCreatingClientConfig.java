/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.h2;

import com.atlassian.h2.ServerView;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseCreatingClientConfig
implements Function<ServerView, String> {
    private final Supplier<File> databaseDirectory;
    private final String databaseName;
    private final boolean useMVCC;

    @Deprecated
    public DatabaseCreatingClientConfig(@Nonnull Supplier<File> databaseDirectory, @Nonnull String databaseName) {
        this(databaseDirectory, databaseName, true);
    }

    public DatabaseCreatingClientConfig(@Nonnull Supplier<File> databaseDirectory, @Nonnull String databaseName, boolean useMVCC) {
        this.databaseDirectory = Objects.requireNonNull(databaseDirectory);
        this.databaseName = Objects.requireNonNull(databaseName);
        this.useMVCC = useMVCC;
    }

    @Override
    @Nonnull
    public String apply(@Nonnull ServerView server) {
        Objects.requireNonNull(server);
        Preconditions.checkState((boolean)server.isRunning(), (String)"expected %s to be running", (Object)server);
        File databaseFile = new File(this.databaseDirectory.get(), this.databaseName);
        return server.getUri() + "/" + databaseFile.getAbsolutePath() + (this.useMVCC ? ";MVCC=TRUE" : "");
    }
}

