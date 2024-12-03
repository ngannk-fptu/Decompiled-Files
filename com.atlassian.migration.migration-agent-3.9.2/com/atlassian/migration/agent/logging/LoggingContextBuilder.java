/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.MapUtils
 */
package com.atlassian.migration.agent.logging;

import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.MapUtils;

@ParametersAreNonnullByDefault
public final class LoggingContextBuilder {
    private Map<String, Object> ctx = new HashMap<String, Object>();

    public static LoggingContextBuilder logCtx() {
        return new LoggingContextBuilder();
    }

    private LoggingContextBuilder() {
    }

    public LoggingContextBuilder withStep(Step step) {
        this.putToCtxIfNotNull(ContextKey.STEP_ID, step.getId());
        this.putToCtxIfNotNull(ContextKey.STEP_TYPE, step.getType());
        this.putToCtxIfNotNull(ContextKey.STEP_CONFIG, step.getConfig());
        return this;
    }

    public LoggingContextBuilder withTask(Task task) {
        this.putToCtxIfNotNull(ContextKey.TASK_NAME, task.getName());
        this.putToCtxIfNotNull(ContextKey.TASK_ID, task.getId());
        return this;
    }

    public LoggingContextBuilder withPlan(Plan plan) {
        this.putToCtxIfNotNull(ContextKey.PLAN_ID, plan.getId());
        this.putToCtxIfNotNull(ContextKey.PLAN_NAME, plan.getName());
        return this;
    }

    public LoggingContextBuilder withCloudSite(CloudSite cloudSite) {
        this.putToCtxIfNotNull(ContextKey.CLOUD_ID, cloudSite.getCloudId());
        this.putToCtxIfNotNull(ContextKey.CLOUD_URL, cloudSite.getCloudUrl());
        return this;
    }

    public LoggingContextBuilder withAttachment(Attachment attachment) {
        this.putToCtxIfNotNull(ContextKey.ATTACHMENT_ID, attachment.getId());
        return this;
    }

    public LoggingContextBuilder withCheckExecutionId(String checkExecutionId) {
        this.putToCtxIfNotNull(ContextKey.CHECK_EXECUTION_ID, checkExecutionId);
        return this;
    }

    public LoggingContextBuilder withAttribute(String key, String value) {
        this.ctx.put(key, value);
        return this;
    }

    LoggingContextBuilder withContext(Map<String, String> context) {
        if (MapUtils.isNotEmpty(context)) {
            this.ctx.putAll(context);
        }
        return this;
    }

    public void execute(Runnable runnable) {
        LoggingContext.executeWithContext(this.ctx, (Runnable)runnable);
    }

    public <T> T execute(Supplier<T> supplier) {
        try {
            LoggingContextBuilder.putToLoggingContext(this.ctx);
            T t = supplier.get();
            return t;
        }
        finally {
            LoggingContextBuilder.removeFromLoggingContext(this.ctx);
        }
    }

    <T> T executeCallable(Callable<T> callable) throws Exception {
        try {
            LoggingContextBuilder.putToLoggingContext(this.ctx);
            T t = callable.call();
            return t;
        }
        finally {
            LoggingContextBuilder.removeFromLoggingContext(this.ctx);
        }
    }

    private void putToCtxIfNotNull(ContextKey key, @Nullable Object value) {
        if (value != null) {
            this.ctx.put(key.getValue(), value);
        }
    }

    private static void putToLoggingContext(Map<String, Object> ctx) {
        if (MapUtils.isEmpty(ctx)) {
            return;
        }
        ctx.forEach(LoggingContext::put);
    }

    private static void removeFromLoggingContext(Map<String, Object> ctx) {
        if (MapUtils.isEmpty(ctx)) {
            return;
        }
        ctx.forEach((key, value) -> LoggingContext.remove((String[])new String[]{key}));
    }

    static enum ContextKey {
        STEP_ID("stepId"),
        STEP_TYPE("stepType"),
        STEP_CONFIG("stepConfig"),
        TASK_ID("taskId"),
        TASK_NAME("taskName"),
        PLAN_ID("planId"),
        ATTACHMENT_ID("attachmentId"),
        PLAN_NAME("planName"),
        CLOUD_ID("cloudId"),
        CLOUD_URL("cloudUrl"),
        CHECK_EXECUTION_ID("checkExecutionId");

        private final String value;

        private ContextKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}

