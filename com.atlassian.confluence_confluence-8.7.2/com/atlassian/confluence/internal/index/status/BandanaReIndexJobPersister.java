/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.internal.index.status;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.internal.index.status.ReIndexJobPersister;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.Objects;
import java.util.Optional;

public class BandanaReIndexJobPersister
implements ReIndexJobPersister {
    public static final String REINDEX_STATUS_KEY = "reindex.status";
    private final BandanaManager bandanaManager;

    public BandanaReIndexJobPersister(BandanaManager bandanaManager) {
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
    }

    @Override
    public Optional<ReIndexJob> get() {
        Object status = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, REINDEX_STATUS_KEY);
        return status instanceof ReIndexJob ? Optional.of((ReIndexJob)status) : Optional.empty();
    }

    @Override
    public void saveOrUpdate(ReIndexJob reIndexJob) {
        Objects.requireNonNull(reIndexJob);
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, REINDEX_STATUS_KEY, (Object)reIndexJob);
    }

    @Override
    public void clear() {
        this.bandanaManager.removeValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, REINDEX_STATUS_KEY);
    }
}

