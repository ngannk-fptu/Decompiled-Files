/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.extra.jira.executor;

import com.atlassian.confluence.extra.jira.StreamableJiraIssuesMacro;
import com.atlassian.confluence.extra.jira.executor.JiraExecutorFactory;
import com.atlassian.confluence.extra.jira.executor.MacroExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.PreDestroy;

public class StreamableMacroExecutor
implements MacroExecutorService {
    private final ExecutorService delegatingService;

    public StreamableMacroExecutor(JiraExecutorFactory factory) {
        this.delegatingService = factory.newLimitedThreadPool(StreamableJiraIssuesMacro.THREAD_POOL_SIZE, "Jira macros executor");
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.delegatingService.submit(task);
    }

    @PreDestroy
    public void destroy() {
        this.delegatingService.shutdown();
    }
}

