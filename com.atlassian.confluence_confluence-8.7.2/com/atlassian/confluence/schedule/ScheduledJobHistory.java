/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.schedule;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ScheduledJobHistory
implements Serializable {
    private Date startDate;
    private Date endDate;

    private ScheduledJobHistory() {
    }

    public ScheduledJobHistory(Date startDate, Date endDate) {
        this.startDate = new Date(startDate.getTime());
        this.endDate = new Date(endDate.getTime());
    }

    public Date getEndDate() {
        return new Date(this.endDate.getTime());
    }

    public Date getStartDate() {
        return new Date(this.startDate.getTime());
    }

    public long getDuration() {
        return this.endDate.getTime() - this.startDate.getTime();
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder((Object)this);
        builder.append("startDate", (Object)this.startDate);
        builder.append("endDate", (Object)this.endDate);
        return builder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ScheduledJobHistory) {
            ScheduledJobHistory that = (ScheduledJobHistory)o;
            return ObjectUtils.equals((Object)this.startDate, (Object)that.startDate) && ObjectUtils.equals((Object)this.endDate, (Object)that.endDate);
        }
        return false;
    }

    public int hashCode() {
        int result = ObjectUtils.hashCode((Object)this.startDate);
        result = 31 * result + ObjectUtils.hashCode((Object)this.endDate);
        return result;
    }

    public static class NaturalComparator
    implements Comparator<ScheduledJobHistory> {
        @Override
        public int compare(ScheduledJobHistory o1, ScheduledJobHistory o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            int c = o1.startDate.compareTo(o2.startDate);
            if (c == 0) {
                c = o1.endDate.compareTo(o2.endDate);
            }
            return c;
        }
    }
}

