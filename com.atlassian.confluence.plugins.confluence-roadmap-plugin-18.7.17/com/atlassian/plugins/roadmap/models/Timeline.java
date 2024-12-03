/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

import java.util.Date;

public class Timeline {
    private Date startDate;
    private Date endDate;
    private DisplayOption displayOption;

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public DisplayOption getDisplayOption() {
        return this.displayOption;
    }

    public void setDisplayOption(DisplayOption displayOption) {
        this.displayOption = displayOption;
    }

    public static enum DisplayOption {
        MONTH,
        WEEK;

    }
}

