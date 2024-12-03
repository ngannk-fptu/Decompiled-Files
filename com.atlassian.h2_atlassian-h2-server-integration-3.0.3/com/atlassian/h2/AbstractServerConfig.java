/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.h2.tools.Server
 */
package com.atlassian.h2;

import io.atlassian.fugue.Pair;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import org.h2.tools.Server;

@NotThreadSafe
public abstract class AbstractServerConfig
implements Supplier<Server> {
    @Nonnull
    private Pair<Integer, Boolean> port = new Pair((Object)9092, (Object)Boolean.FALSE);
    @Nullable
    private Supplier<File> databaseDirectory;

    public AbstractServerConfig setPort(int port, boolean required) {
        this.port = new Pair((Object)port, (Object)required);
        return this;
    }

    public AbstractServerConfig setDatabaseHomeDirectory(@Nonnull Supplier<File> databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
        return this;
    }

    @Override
    @Nonnull
    public Server get() {
        List<String> options = this.getOptions();
        try {
            return Server.createTcpServer((String[])options.toArray(new String[options.size()]));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    protected List<String> getOptions() {
        ArrayList<String> options = new ArrayList<String>();
        if (((Boolean)this.port.right()).booleanValue()) {
            options.add("-tcpPort");
            options.add(((Integer)this.port.left()).toString());
        }
        if (this.databaseDirectory != null) {
            options.add("-baseDir");
            options.add(this.databaseDirectory.get().getAbsolutePath());
        }
        return options;
    }
}

