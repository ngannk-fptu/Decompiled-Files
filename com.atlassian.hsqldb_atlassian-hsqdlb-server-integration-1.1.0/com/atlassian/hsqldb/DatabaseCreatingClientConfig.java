/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.hsqldb;

import com.atlassian.hsqldb.ServerState;
import com.atlassian.hsqldb.ServerView;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.File;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseCreatingClientConfig
implements Function<ServerView, String> {
    private final Supplier<File> databaseDirectory;
    private final String databaseName;

    public DatabaseCreatingClientConfig(Supplier<File> databaseDirectory, String databaseName) {
        this.databaseDirectory = databaseDirectory;
        this.databaseName = databaseName;
    }

    public String apply(@Nullable ServerView server) {
        Preconditions.checkState((ServerState.ONLINE == server.getState() ? 1 : 0) != 0, (String)"expected %s to be %s", (Object[])new Object[]{server, ServerState.ONLINE});
        File databaseFile = new File((File)this.databaseDirectory.get(), this.databaseName);
        return server.getUri() + "/" + this.databaseName + ";file:" + databaseFile.getAbsolutePath() + ";" + "hsqldb.tx" + "=" + "MVCC";
    }
}

