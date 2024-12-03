/*
 * Decompiled with CFR 0.152.
 */
package groovy.time;

import groovy.time.BaseDuration;
import groovy.time.DatumDependentDuration;
import groovy.time.TimeDatumDependentDuration;
import groovy.time.TimeDuration;
import java.util.Calendar;
import java.util.Date;

public class Duration
extends BaseDuration {
    public Duration(int days, int hours, int minutes, int seconds, int millis) {
        super(days, hours, minutes, seconds, millis);
    }

    public Duration plus(Duration rhs) {
        return new Duration(this.getDays() + rhs.getDays(), this.getHours() + rhs.getHours(), this.getMinutes() + rhs.getMinutes(), this.getSeconds() + rhs.getSeconds(), this.getMillis() + rhs.getMillis());
    }

    public Duration plus(TimeDuration rhs) {
        return rhs.plus(this);
    }

    public DatumDependentDuration plus(DatumDependentDuration rhs) {
        return rhs.plus(this);
    }

    public Duration minus(Duration rhs) {
        return new Duration(this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    public TimeDuration minus(TimeDuration rhs) {
        return new TimeDuration(this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    public DatumDependentDuration minus(DatumDependentDuration rhs) {
        return new DatumDependentDuration(-rhs.getYears(), -rhs.getMonths(), this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    public TimeDatumDependentDuration minus(TimeDatumDependentDuration rhs) {
        return new TimeDatumDependentDuration(-rhs.getYears(), -rhs.getMonths(), this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    @Override
    public long toMilliseconds() {
        return ((((long)(this.getDays() * 24) + (long)this.getHours()) * 60L + (long)this.getMinutes()) * 60L + (long)this.getSeconds()) * 1000L + (long)this.getMillis();
    }

    @Override
    public Date getAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(6, -this.getDays());
        cal.add(11, -this.getHours());
        cal.add(12, -this.getMinutes());
        cal.add(13, -this.getSeconds());
        cal.add(14, -this.getMillis());
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return new Date(cal.getTimeInMillis());
    }

    @Override
    public BaseDuration.From getFrom() {
        return new BaseDuration.From(){

            @Override
            public Date getNow() {
                Calendar cal = Calendar.getInstance();
                cal.add(6, Duration.this.getDays());
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                return new Date(cal.getTimeInMillis());
            }
        };
    }
}

