/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.status;

import com.atlassian.confluence.index.status.ReIndexJob;
import java.util.Optional;

public interface ReIndexJobPersister {
    public Optional<ReIndexJob> get();

    public void saveOrUpdate(ReIndexJob var1);

    default public boolean saveNewUniquely(ReIndexJob reIndexJob) {
        Optional<ReIndexJob> currentJob = this.get();
        if (currentJob.isEmpty() || currentJob.get().getStage().isFinal()) {
            this.saveOrUpdate(reIndexJob);
            return true;
        }
        return false;
    }

    public void clear();
}

