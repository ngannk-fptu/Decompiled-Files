/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.pages.ancestors.AncestorsRepairer;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.concurrent.ExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AncestorsRepairJob
implements JobRunner {
    private final AncestorsRepairer ancestorsRepairer;

    public AncestorsRepairJob(@NonNull AncestorsRepairer ancestorsRepairer) {
        this.ancestorsRepairer = ancestorsRepairer;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            this.ancestorsRepairer.repairAncestors();
        }
        catch (InterruptedException e) {
            return JobRunnerResponse.aborted((String)("Ancestors repair was interrupted: " + e.getMessage()));
        }
        catch (ExecutionException e) {
            return JobRunnerResponse.failed((String)("Ancestors repair failed: " + e.getMessage()));
        }
        return JobRunnerResponse.success((String)"Ancestors were repaired successfully");
    }
}

