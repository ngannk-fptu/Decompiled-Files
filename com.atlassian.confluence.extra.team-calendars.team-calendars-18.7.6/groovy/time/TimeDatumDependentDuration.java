/*
 * Decompiled with CFR 0.152.
 */
package groovy.time;

import groovy.time.BaseDuration;
import groovy.time.DatumDependentDuration;
import groovy.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class TimeDatumDependentDuration
extends DatumDependentDuration {
    public TimeDatumDependentDuration(int years, int months, int days, int hours, int minutes, int seconds, int millis) {
        super(years, months, days, hours, minutes, seconds, millis);
    }

    @Override
    public DatumDependentDuration plus(Duration rhs) {
        return new TimeDatumDependentDuration(this.getYears(), this.getMonths(), this.getDays() + rhs.getDays(), this.getHours() + rhs.getHours(), this.getMinutes() + rhs.getMinutes(), this.getSeconds() + rhs.getSeconds(), this.getMillis() + rhs.getMillis());
    }

    @Override
    public DatumDependentDuration plus(DatumDependentDuration rhs) {
        return new TimeDatumDependentDuration(this.getYears() + rhs.getYears(), this.getMonths() + rhs.getMonths(), this.getDays() + rhs.getDays(), this.getHours() + rhs.getHours(), this.getMinutes() + rhs.getMinutes(), this.getSeconds() + rhs.getSeconds(), this.getMillis() + rhs.getMillis());
    }

    @Override
    public DatumDependentDuration minus(Duration rhs) {
        return new TimeDatumDependentDuration(this.getYears(), this.getMonths(), this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    @Override
    public DatumDependentDuration minus(DatumDependentDuration rhs) {
        return new TimeDatumDependentDuration(this.getYears() - rhs.getYears(), this.getMonths() - rhs.getMonths(), this.getDays() - rhs.getDays(), this.getHours() - rhs.getHours(), this.getMinutes() - rhs.getMinutes(), this.getSeconds() - rhs.getSeconds(), this.getMillis() - rhs.getMillis());
    }

    @Override
    public BaseDuration.From getFrom() {
        return new BaseDuration.From(){

            @Override
            public Date getNow() {
                Calendar cal = Calendar.getInstance();
                cal.add(1, TimeDatumDependentDuration.this.getYears());
                cal.add(2, TimeDatumDependentDuration.this.getMonths());
                cal.add(6, TimeDatumDependentDuration.this.getDays());
                cal.add(11, TimeDatumDependentDuration.this.getHours());
                cal.add(12, TimeDatumDependentDuration.this.getMinutes());
                cal.add(13, TimeDatumDependentDuration.this.getSeconds());
                cal.add(14, TimeDatumDependentDuration.this.getMillis());
                return cal.getTime();
            }
        };
    }
}

