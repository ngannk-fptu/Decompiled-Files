/*
 * Decompiled with CFR 0.152.
 */
package groovy.time;

import groovy.time.BaseDuration;
import groovy.time.DatumDependentDuration;
import groovy.time.Duration;
import groovy.time.TimeDatumDependentDuration;
import java.util.Calendar;
import java.util.Date;

public class TimeDuration
extends Duration {
    public TimeDuration(int hours, int minutes, int seconds, int millis) {
        super(0, hours, minutes, seconds, millis);
    }

    public TimeDuration(int days, int hours, int minutes, int seconds, int millis) {
        super(days, hours, minutes, seconds, millis);
    }

    @Override
    public Duration plus(Duration rhs) {
        return new TimeDuration(this.getDays() + rhs.getDays(), this.getHours() + rhs.getHours(), this.getMinutes() + rhs.getMinutes(), this.getSeconds() + rhs.getSeconds(), this.getMillis() + rhs.getMillis());
    }

    @Override
    public DatumDependentDuration plus(DatumDependentDuration rhs) {
        return new TimeDatumDependentDuration(rhs.getYears(), rhs.getMonths(), this.getDays() + rhs.getDays(), this.getHours() + rhs.getHours(), this.getMinutes() + rhs.getMinutes(), this.getSeconds() + rhs.getSeconds(), this.getMillis() + rhs.getMillis());
    }

    @Override
    public Duration minus(Duration rhs) {
        return new TimeDuration(this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    @Override
    public DatumDependentDuration minus(DatumDependentDuration rhs) {
        return new TimeDatumDependentDuration(-rhs.getYears(), -rhs.getMonths(), this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    @Override
    public Date getAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(6, -this.getDays());
        cal.add(11, -this.getHours());
        cal.add(12, -this.getMinutes());
        cal.add(13, -this.getSeconds());
        cal.add(14, -this.getMillis());
        return cal.getTime();
    }

    @Override
    public BaseDuration.From getFrom() {
        return new BaseDuration.From(){

            @Override
            public Date getNow() {
                Calendar cal = Calendar.getInstance();
                cal.add(6, TimeDuration.this.getDays());
                cal.add(11, TimeDuration.this.getHours());
                cal.add(12, TimeDuration.this.getMinutes());
                cal.add(13, TimeDuration.this.getSeconds());
                cal.add(14, TimeDuration.this.getMillis());
                return cal.getTime();
            }
        };
    }
}

