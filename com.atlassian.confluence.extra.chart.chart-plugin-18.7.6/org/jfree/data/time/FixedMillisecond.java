/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.jfree.data.time.RegularTimePeriod;

public class FixedMillisecond
extends RegularTimePeriod
implements Serializable {
    private static final long serialVersionUID = 7867521484545646931L;
    private long time;

    public FixedMillisecond() {
        this(new Date());
    }

    public FixedMillisecond(long millisecond) {
        this(new Date(millisecond));
    }

    public FixedMillisecond(Date time) {
        this.time = time.getTime();
    }

    public Date getTime() {
        return new Date(this.time);
    }

    public void peg(Calendar calendar) {
    }

    public RegularTimePeriod previous() {
        FixedMillisecond result = null;
        long t = this.time;
        if (t != Long.MIN_VALUE) {
            result = new FixedMillisecond(t - 1L);
        }
        return result;
    }

    public RegularTimePeriod next() {
        FixedMillisecond result = null;
        long t = this.time;
        if (t != Long.MAX_VALUE) {
            result = new FixedMillisecond(t + 1L);
        }
        return result;
    }

    public boolean equals(Object object) {
        if (object instanceof FixedMillisecond) {
            FixedMillisecond m = (FixedMillisecond)object;
            return this.time == m.getFirstMillisecond();
        }
        return false;
    }

    public int hashCode() {
        return (int)this.time;
    }

    public int compareTo(Object o1) {
        int result;
        if (o1 instanceof FixedMillisecond) {
            FixedMillisecond t1 = (FixedMillisecond)o1;
            long difference = this.time - t1.time;
            result = difference > 0L ? 1 : (difference < 0L ? -1 : 0);
        } else {
            result = o1 instanceof RegularTimePeriod ? 0 : 1;
        }
        return result;
    }

    public long getFirstMillisecond() {
        return this.time;
    }

    public long getFirstMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getLastMillisecond() {
        return this.time;
    }

    public long getLastMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getMiddleMillisecond() {
        return this.time;
    }

    public long getMiddleMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getSerialIndex() {
        return this.time;
    }
}

