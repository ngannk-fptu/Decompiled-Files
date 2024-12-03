/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.extras.ExportProgress
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.impl.hibernate.extras.ExportProgress;
import com.atlassian.core.util.ProgressMeter;

public class DecoratedProgressMeter
implements ExportProgress {
    ProgressMeter progressMeter;

    public DecoratedProgressMeter(ProgressMeter progressMeter) {
        this.progressMeter = progressMeter;
    }

    public void setStatus(String status) {
        this.progressMeter.setStatus(status);
    }

    public int increment() {
        int newCount = this.progressMeter.getCurrentCount() + 1;
        this.progressMeter.setCurrentCount(newCount);
        return newCount;
    }

    public void setTotal(int total) {
        this.progressMeter.setTotalObjects(total);
    }

    public void incrementTotal() {
        this.progressMeter.setTotalObjects(this.progressMeter.getTotal() + 1);
    }
}

