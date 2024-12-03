/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdFeatureFlagAware
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.ipd.internal.spi.IpdFeatureFlagAware;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultIpdJobRunner
implements IpdJobRunner,
IpdFeatureFlagAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultIpdJobRunner.class);
    protected final Map<Class<?>, IpdJob> jobs = new ConcurrentHashMap();

    protected DefaultIpdJobRunner() {
    }

    public void register(@Nonnull IpdJob job) {
        this.jobs.put(job.getClass(), job);
        log.info("Job {} has been registered", (Object)job.getClass().getName());
    }

    public void runJobs() {
        if (!this.isIpdFeatureFlagEnabled()) {
            log.debug("Not executing IpdJobs because in.product.diagnostics feature flag is disabled");
            return;
        }
        for (IpdJob job : this.jobs.values()) {
            if (job.isWorkInProgressJob() && !this.isWipIpdFeatureFlagEnabled()) {
                log.debug("Ignoring job {}, in.product.diagnostics.wip feature flag is not enabled", (Object)job.getClass().getName());
                continue;
            }
            log.debug("Running job {}", (Object)job.getClass().getName());
            try {
                job.runJob();
            }
            catch (RuntimeException ex) {
                log.error(String.format("Error during executing %s job", job.getClass().getName()), (Throwable)ex);
            }
        }
    }
}

