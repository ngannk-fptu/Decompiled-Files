/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jfree.data.time.TimePeriod;
import org.jfree.date.MonthConstants;

public abstract class RegularTimePeriod
implements TimePeriod,
Comparable,
MonthConstants {
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();
    public static final Calendar WORKING_CALENDAR = Calendar.getInstance(DEFAULT_TIME_ZONE);
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$TimeZone;
    static /* synthetic */ Class class$org$jfree$data$time$Year;
    static /* synthetic */ Class class$org$jfree$data$time$Quarter;
    static /* synthetic */ Class class$org$jfree$data$time$Month;
    static /* synthetic */ Class class$org$jfree$data$time$Day;
    static /* synthetic */ Class class$org$jfree$data$time$Hour;
    static /* synthetic */ Class class$org$jfree$data$time$Minute;
    static /* synthetic */ Class class$org$jfree$data$time$Second;
    static /* synthetic */ Class class$org$jfree$data$time$Millisecond;

    public static RegularTimePeriod createInstance(Class c, Date millisecond, TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            Constructor constructor = c.getDeclaredConstructor(class$java$util$Date == null ? (class$java$util$Date = RegularTimePeriod.class$("java.util.Date")) : class$java$util$Date, class$java$util$TimeZone == null ? (class$java$util$TimeZone = RegularTimePeriod.class$("java.util.TimeZone")) : class$java$util$TimeZone);
            result = (RegularTimePeriod)constructor.newInstance(millisecond, zone);
        }
        catch (Exception e) {
            // empty catch block
        }
        return result;
    }

    public static Class downsize(Class c) {
        if (c.equals(class$org$jfree$data$time$Year == null ? (class$org$jfree$data$time$Year = RegularTimePeriod.class$("org.jfree.data.time.Year")) : class$org$jfree$data$time$Year)) {
            return class$org$jfree$data$time$Quarter == null ? (class$org$jfree$data$time$Quarter = RegularTimePeriod.class$("org.jfree.data.time.Quarter")) : class$org$jfree$data$time$Quarter;
        }
        if (c.equals(class$org$jfree$data$time$Quarter == null ? (class$org$jfree$data$time$Quarter = RegularTimePeriod.class$("org.jfree.data.time.Quarter")) : class$org$jfree$data$time$Quarter)) {
            return class$org$jfree$data$time$Month == null ? (class$org$jfree$data$time$Month = RegularTimePeriod.class$("org.jfree.data.time.Month")) : class$org$jfree$data$time$Month;
        }
        if (c.equals(class$org$jfree$data$time$Month == null ? (class$org$jfree$data$time$Month = RegularTimePeriod.class$("org.jfree.data.time.Month")) : class$org$jfree$data$time$Month)) {
            return class$org$jfree$data$time$Day == null ? (class$org$jfree$data$time$Day = RegularTimePeriod.class$("org.jfree.data.time.Day")) : class$org$jfree$data$time$Day;
        }
        if (c.equals(class$org$jfree$data$time$Day == null ? (class$org$jfree$data$time$Day = RegularTimePeriod.class$("org.jfree.data.time.Day")) : class$org$jfree$data$time$Day)) {
            return class$org$jfree$data$time$Hour == null ? (class$org$jfree$data$time$Hour = RegularTimePeriod.class$("org.jfree.data.time.Hour")) : class$org$jfree$data$time$Hour;
        }
        if (c.equals(class$org$jfree$data$time$Hour == null ? (class$org$jfree$data$time$Hour = RegularTimePeriod.class$("org.jfree.data.time.Hour")) : class$org$jfree$data$time$Hour)) {
            return class$org$jfree$data$time$Minute == null ? (class$org$jfree$data$time$Minute = RegularTimePeriod.class$("org.jfree.data.time.Minute")) : class$org$jfree$data$time$Minute;
        }
        if (c.equals(class$org$jfree$data$time$Minute == null ? (class$org$jfree$data$time$Minute = RegularTimePeriod.class$("org.jfree.data.time.Minute")) : class$org$jfree$data$time$Minute)) {
            return class$org$jfree$data$time$Second == null ? (class$org$jfree$data$time$Second = RegularTimePeriod.class$("org.jfree.data.time.Second")) : class$org$jfree$data$time$Second;
        }
        if (c.equals(class$org$jfree$data$time$Second == null ? (class$org$jfree$data$time$Second = RegularTimePeriod.class$("org.jfree.data.time.Second")) : class$org$jfree$data$time$Second)) {
            return class$org$jfree$data$time$Millisecond == null ? (class$org$jfree$data$time$Millisecond = RegularTimePeriod.class$("org.jfree.data.time.Millisecond")) : class$org$jfree$data$time$Millisecond;
        }
        return class$org$jfree$data$time$Millisecond == null ? (class$org$jfree$data$time$Millisecond = RegularTimePeriod.class$("org.jfree.data.time.Millisecond")) : class$org$jfree$data$time$Millisecond;
    }

    public abstract RegularTimePeriod previous();

    public abstract RegularTimePeriod next();

    public abstract long getSerialIndex();

    public abstract void peg(Calendar var1);

    public Date getStart() {
        return new Date(this.getFirstMillisecond());
    }

    public Date getEnd() {
        return new Date(this.getLastMillisecond());
    }

    public abstract long getFirstMillisecond();

    public long getFirstMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        return this.getFirstMillisecond(calendar);
    }

    public abstract long getFirstMillisecond(Calendar var1);

    public abstract long getLastMillisecond();

    public long getLastMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        return this.getLastMillisecond(calendar);
    }

    public abstract long getLastMillisecond(Calendar var1);

    public long getMiddleMillisecond() {
        long m1 = this.getFirstMillisecond();
        long m2 = this.getLastMillisecond();
        return m1 + (m2 - m1) / 2L;
    }

    public long getMiddleMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        long m1 = this.getFirstMillisecond(calendar);
        long m2 = this.getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2L;
    }

    public long getMiddleMillisecond(Calendar calendar) {
        long m1 = this.getFirstMillisecond(calendar);
        long m2 = this.getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2L;
    }

    public String toString() {
        return String.valueOf(this.getStart());
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

