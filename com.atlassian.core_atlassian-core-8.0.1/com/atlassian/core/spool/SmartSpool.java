/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.BufferedFileSpool;
import com.atlassian.core.spool.DeferredSpool;
import com.atlassian.core.spool.Spool;
import com.atlassian.core.spool.ThresholdingSpool;
import java.io.IOException;
import java.io.InputStream;

public class SmartSpool
implements ThresholdingSpool {
    private Spool overThresholdSpool = new BufferedFileSpool();
    private ThresholdingSpool thresholdingSpool = new DeferredSpool();

    @Override
    public void setThresholdBytes(int bytes) {
        this.thresholdingSpool.setThresholdBytes(bytes);
    }

    @Override
    public int getThresholdBytes() {
        return this.thresholdingSpool.getThresholdBytes();
    }

    @Override
    public InputStream spool(InputStream is) throws IOException {
        if (is.available() > this.getThresholdBytes()) {
            return this.overThresholdSpool.spool(is);
        }
        return this.thresholdingSpool.spool(is);
    }

    public void setOverThresholdSpool(Spool overThresholdSpool) {
        this.overThresholdSpool = overThresholdSpool;
    }

    public void setThresholdingSpool(ThresholdingSpool thresholdingSpool) {
        this.thresholdingSpool = thresholdingSpool;
    }
}

