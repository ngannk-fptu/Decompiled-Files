/*
 * Decompiled with CFR 0.152.
 */
package groovy.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;

public abstract class BaseDuration
implements Comparable<BaseDuration> {
    protected final int years;
    protected final int months;
    protected final int days;
    protected final int hours;
    protected final int minutes;
    protected final int seconds;
    protected final int millis;

    protected BaseDuration(int years, int months, int days, int hours, int minutes, int seconds, int millis) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }

    protected BaseDuration(int days, int hours, int minutes, int seconds, int millis) {
        this(0, 0, days, hours, minutes, seconds, millis);
    }

    public int getYears() {
        return this.years;
    }

    public int getMonths() {
        return this.months;
    }

    public int getDays() {
        return this.days;
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public int getMillis() {
        return this.millis;
    }

    public Date plus(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(1, this.years);
        cal.add(2, this.months);
        cal.add(6, this.days);
        cal.add(11, this.hours);
        cal.add(12, this.minutes);
        cal.add(13, this.seconds);
        cal.add(14, this.millis);
        return cal.getTime();
    }

    public String toString() {
        ArrayList<String> buffer = new ArrayList<String>();
        if (this.years != 0) {
            buffer.add(this.years + " years");
        }
        if (this.months != 0) {
            buffer.add(this.months + " months");
        }
        if (this.days != 0) {
            buffer.add(this.days + " days");
        }
        if (this.hours != 0) {
            buffer.add(this.hours + " hours");
        }
        if (this.minutes != 0) {
            buffer.add(this.minutes + " minutes");
        }
        if (this.seconds != 0 || this.millis != 0) {
            int norm_millis = this.millis % 1000;
            int norm_seconds = this.seconds + DefaultGroovyMethods.intdiv((Number)(this.millis - norm_millis), (Number)1000).intValue();
            String millisToPad = "" + Math.abs(norm_millis);
            buffer.add((norm_seconds == 0 ? (norm_millis < 0 ? "-0" : "0") : Integer.valueOf(norm_seconds)) + "." + StringGroovyMethods.padLeft((CharSequence)millisToPad, (Number)3, (CharSequence)"0") + " seconds");
        }
        if (!buffer.isEmpty()) {
            return DefaultGroovyMethods.join(buffer.iterator(), ", ");
        }
        return "0";
    }

    public abstract long toMilliseconds();

    public abstract Date getAgo();

    public abstract From getFrom();

    @Override
    public int compareTo(BaseDuration otherDuration) {
        return Long.signum(this.toMilliseconds() - otherDuration.toMilliseconds());
    }

    public static abstract class From {
        public abstract Date getNow();

        public Date getToday() {
            return this.getNow();
        }
    }
}

