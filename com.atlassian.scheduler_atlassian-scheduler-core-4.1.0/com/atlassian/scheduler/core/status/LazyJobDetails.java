/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.AbstractSchedulerService;
import com.atlassian.scheduler.core.status.AbstractJobDetails;
import com.atlassian.scheduler.core.status.SimpleJobDetails;
import com.atlassian.scheduler.core.status.UnusableJobDetails;
import io.atlassian.util.concurrent.LazyReference;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LazyJobDetails
extends AbstractJobDetails {
    private final LazyReference<AbstractJobDetails> delegateRef;

    public LazyJobDetails(final AbstractSchedulerService schedulerService, final JobId jobId, final JobRunnerKey jobRunnerKey, final RunMode runMode, final Schedule schedule, final @Nullable Date nextRunTime, final @Nullable byte[] parameters) {
        super(jobId, jobRunnerKey, runMode, schedule, nextRunTime, parameters);
        Objects.requireNonNull(schedulerService, "schedulerService");
        this.delegateRef = new LazyReference<AbstractJobDetails>(){

            @Nonnull
            protected AbstractJobDetails create() throws Exception {
                Throwable cause = null;
                try {
                    JobRunner jobRunner = schedulerService.getJobRunner(jobRunnerKey);
                    if (jobRunner != null) {
                        ClassLoader classLoader = jobRunner.getClass().getClassLoader();
                        Map<String, Serializable> parametersMap = schedulerService.getParameterMapSerializer().deserializeParameters(classLoader, parameters);
                        return new SimpleJobDetails(jobId, jobRunnerKey, runMode, schedule, nextRunTime, parameters, parametersMap);
                    }
                }
                catch (Exception ex) {
                    cause = ex;
                }
                catch (LinkageError err) {
                    cause = err;
                }
                return new UnusableJobDetails(jobId, jobRunnerKey, runMode, schedule, nextRunTime, parameters, cause);
            }
        };
    }

    @Nonnull
    public Map<String, Serializable> getParameters() {
        return this.getDelegate().getParameters();
    }

    public boolean isRunnable() {
        return this.getDelegate().isRunnable();
    }

    @Override
    protected void appendToStringDetails(StringBuilder sb) {
        if (this.delegateRef.isInitialized()) {
            AbstractJobDetails delegate = this.getDelegate();
            sb.append(",delegate=").append(delegate.getClass().getSimpleName());
            delegate.appendToStringDetails(sb);
        } else {
            sb.append(",delegate=(unresolved)");
        }
    }

    private AbstractJobDetails getDelegate() {
        return (AbstractJobDetails)this.delegateRef.get();
    }
}

