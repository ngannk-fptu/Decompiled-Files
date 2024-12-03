/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.util;

import com.atlassian.core.util.ProgressMeter;
import com.google.common.base.Preconditions;

public class SubProgressMeter
extends ProgressMeter {
    private ProgressMeter outerMeter;
    private boolean isStartProcessing;
    private float percentageOfOuterMeter;
    private int percentageComplete;
    private int outerPercentage;

    public SubProgressMeter(ProgressMeter outerMeter, float percentageOfOuterMeter, int total) {
        Preconditions.checkNotNull((Object)outerMeter);
        this.outerMeter = outerMeter;
        this.setTotalObjects(total);
        this.percentageOfOuterMeter = percentageOfOuterMeter;
        this.isStartProcessing = false;
    }

    public void setPercentage(int count, int total) {
        if (total < 0) {
            this.setPercentage(0);
        } else if (total <= count) {
            this.setPercentage(100);
        } else {
            int calculatedPercentage = (int)(100.0f * (float)count / (float)total);
            if (count < total && calculatedPercentage == 100) {
                calculatedPercentage = 99;
            }
            this.setPercentage(calculatedPercentage);
        }
    }

    public synchronized void setStatus(String status) {
        super.setStatus(status);
        if (this.outerMeter != null) {
            this.outerMeter.setStatus(status);
        }
    }

    public synchronized void setPercentage(int percentageComplete) {
        this.percentageComplete = percentageComplete;
        if (this.outerMeter != null) {
            if (!this.isStartProcessing) {
                this.outerPercentage = this.outerMeter.getPercentageComplete();
                this.isStartProcessing = true;
            }
            this.outerMeter.setPercentage(this.outerPercentage + Math.round((float)this.percentageComplete * this.percentageOfOuterMeter));
        }
    }

    public synchronized void setCompletedSuccessfully(boolean completedSuccessfully) {
        super.setCompletedSuccessfully(completedSuccessfully);
        if (!completedSuccessfully) {
            this.outerMeter.setCompletedSuccessfully(false);
        }
    }
}

