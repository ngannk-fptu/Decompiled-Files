/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.util.concurrent.ThreadFactories
 */
package com.atlassian.crowd.directory.cache;

import com.atlassian.crowd.directory.rest.mapper.DeltaQueryResult;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.GroupWithMembershipChanges;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.util.concurrent.ThreadFactories;
import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BackgroundQueriesProcessor
implements Closeable {
    private final ExecutorService queryExecutor;
    private final Future<DeltaQueryResult<UserWithAttributes>> usersFuture;
    private final Future<DeltaQueryResult<GroupWithMembershipChanges>> groupsFuture;

    public BackgroundQueriesProcessor(String threadPoolName, Callable<DeltaQueryResult<UserWithAttributes>> usersSupplier, Callable<DeltaQueryResult<GroupWithMembershipChanges>> groupsSupplier) {
        this.queryExecutor = Executors.newFixedThreadPool(2, ThreadFactories.namedThreadFactory((String)threadPoolName));
        this.usersFuture = this.queryExecutor.submit(usersSupplier);
        this.groupsFuture = this.queryExecutor.submit(groupsSupplier);
    }

    public DeltaQueryResult<UserWithAttributes> getUsers() throws OperationFailedException {
        return BackgroundQueriesProcessor.get(this.usersFuture);
    }

    public DeltaQueryResult<GroupWithMembershipChanges> getGroups() throws OperationFailedException {
        return BackgroundQueriesProcessor.get(this.groupsFuture);
    }

    private static <T> T get(Future<T> future) throws OperationFailedException {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationFailedException("background query interrupted", (Throwable)e);
        }
        catch (ExecutionException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    @Override
    public void close() {
        this.queryExecutor.shutdown();
    }
}

