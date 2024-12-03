/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 */
package com.atlassian.activeobjects.external;

import com.atlassian.activeobjects.spi.DatabaseType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.java.ao.RawEntity;

public interface ActiveObjectsModuleMetaData {
    public void awaitInitialization() throws ExecutionException, InterruptedException;

    public void awaitInitialization(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException;

    public boolean isInitialized();

    public DatabaseType getDatabaseType();

    public boolean isDataSourcePresent();

    public boolean isTablePresent(Class<? extends RawEntity<?>> var1);
}

