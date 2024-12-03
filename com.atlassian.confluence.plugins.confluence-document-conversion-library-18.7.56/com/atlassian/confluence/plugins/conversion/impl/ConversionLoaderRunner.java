/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.confluence.plugins.conversion.impl.ConversionManagerInternal;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@Component
public class ConversionLoaderRunner
implements JobRunner {
    private ConversionManagerInternal conversionManager;

    @Autowired
    public ConversionLoaderRunner(ConversionManagerInternal conversionManager) {
        this.conversionManager = conversionManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.conversionManager.init();
        return JobRunnerResponse.success();
    }
}

