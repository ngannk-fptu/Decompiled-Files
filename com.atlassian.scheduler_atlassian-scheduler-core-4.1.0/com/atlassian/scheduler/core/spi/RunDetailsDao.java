/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  javax.annotation.CheckForNull
 */
package com.atlassian.scheduler.core.spi;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunDetails;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

public interface RunDetailsDao {
    @CheckForNull
    public RunDetails getLastRunForJob(JobId var1);

    @CheckForNull
    public RunDetails getLastSuccessfulRunForJob(JobId var1);

    default public Map<JobId, RunDetails> getLastRunForJobs(List<JobId> jobIds) {
        HashMap<JobId, RunDetails> runDetailsByJobId = new HashMap<JobId, RunDetails>();
        for (JobId jobId : jobIds) {
            RunDetails runDetails = this.getLastRunForJob(jobId);
            if (runDetails == null) continue;
            runDetailsByJobId.put(jobId, runDetails);
        }
        return runDetailsByJobId;
    }

    public void addRunDetails(JobId var1, RunDetails var2);
}

