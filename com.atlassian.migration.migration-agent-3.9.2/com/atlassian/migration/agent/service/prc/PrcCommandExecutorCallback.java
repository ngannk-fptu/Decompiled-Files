/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.prc.model.Command
 *  javax.annotation.PreDestroy
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.migration.agent.service.prc.PrcCommandExecutor;
import com.atlassian.migration.prc.model.Command;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrcCommandExecutorCallback {
    private final ExecutorService commandExecutorService;
    private final PrcCommandExecutor prcCommandExecutor;
    private final Logger log = LoggerFactory.getLogger(PrcCommandExecutorCallback.class);
    private int corePoolSize = 2;
    private int maxWorkerThreads = 2;
    private long keepAliveInSec = 60L;

    public PrcCommandExecutorCallback(PrcCommandExecutor prcCommandExecutor) {
        this.prcCommandExecutor = prcCommandExecutor;
        this.commandExecutorService = this.newCommandExecutorThreadPool();
    }

    @PreDestroy
    public void cleanup() {
        this.commandExecutorService.shutdown();
    }

    private ExecutorService newCommandExecutorThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(this.corePoolSize, this.maxWorkerThreads, this.keepAliveInSec, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    @NotNull
    public Future<Void> execute(@NotNull Command command) {
        try {
            return this.commandExecutorService.submit(() -> {
                this.prcCommandExecutor.executeCommand(command);
                return null;
            });
        }
        catch (Exception e) {
            this.log.error("Error occurred while executing command {} with id: {} with message : {}", new Object[]{command.getName(), command.getId(), e.getMessage()});
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            future.completeExceptionally(e);
            return future;
        }
    }
}

