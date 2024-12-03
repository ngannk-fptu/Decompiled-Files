/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TemporalAmountAdapter
implements Serializable {
    private final TemporalAmount duration;

    public TemporalAmountAdapter(TemporalAmount duration) {
        this.duration = duration;
    }

    public TemporalAmount getDuration() {
        return this.duration;
    }

    public String toString() {
        return this.toString(LocalDateTime.now());
    }

    public String toString(Temporal seed) {
        String retVal = Duration.ZERO.equals(this.duration) || Period.ZERO.equals(this.duration) ? this.duration.toString() : (this.duration instanceof Period ? this.periodToString(((Period)this.duration).normalized(), seed) : this.durationToString((Duration)this.duration));
        return retVal;
    }

    private String periodToString(Period period, Temporal seed) {
        String retVal;
        Temporal adjustedSeed = seed.plus(period);
        if (period.getYears() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else if (period.getMonths() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else if (period.getDays() % 7 == 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else {
            retVal = period.toString();
        }
        if (period.isNegative() && !retVal.startsWith("-")) {
            return "-" + retVal;
        }
        return retVal;
    }

    private String durationToString(Duration duration) {
        String retVal = null;
        Duration absDuration = duration.abs();
        int days = 0;
        if (absDuration.getSeconds() != 0L) {
            days = (int)absDuration.getSeconds() / 86400;
        }
        if (days != 0) {
            Duration durationMinusDays = absDuration.minusDays(days);
            if (durationMinusDays.getSeconds() != 0L) {
                int hours = (int)durationMinusDays.getSeconds() / 3600;
                int minutes = (int)durationMinusDays.minusHours(hours).getSeconds() / 60;
                int seconds = (int)durationMinusDays.minusHours(hours).minusMinutes(minutes).getSeconds();
                if (hours > 0) {
                    retVal = seconds > 0 ? String.format("P%dDT%dH%dM%dS", days, hours, minutes, seconds) : (minutes > 0 ? String.format("P%dDT%dH%dM", days, hours, minutes) : String.format("P%dDT%dH", days, hours));
                } else if (minutes > 0) {
                    retVal = seconds > 0 ? String.format("P%dDT%dM%dS", days, minutes, seconds) : String.format("P%dDT%dM", days, minutes);
                } else if (seconds > 0) {
                    retVal = String.format("P%dDT%dS", days, seconds);
                }
            } else {
                retVal = String.format("P%dD", days);
            }
        } else {
            retVal = absDuration.toString();
        }
        if (duration.isNegative()) {
            return "-" + retVal;
        }
        return retVal;
    }

    public static TemporalAmountAdapter parse(String value) {
        TemporalAmount retVal = null;
        retVal = "P".equals(value) && CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed") ? Period.ZERO : (value.matches("([+-])?P.*(W|D)") ? Period.parse(value) : Duration.parse(value));
        return new TemporalAmountAdapter(retVal);
    }

    public static TemporalAmountAdapter fromDateRange(Date start, Date end) {
        long durationMillis = end.getTime() - start.getTime();
        TemporalAmount duration = durationMillis % 86400000L == 0L ? Period.ofDays((int)(durationMillis / 86400000L)) : Duration.ofMillis(durationMillis);
        return new TemporalAmountAdapter(duration);
    }

    public static TemporalAmountAdapter from(Dur dur) {
        TemporalAmount duration;
        if (dur.getWeeks() > 0) {
            Period p = Period.ofWeeks(dur.getWeeks());
            if (dur.isNegative()) {
                p = p.negated();
            }
            duration = p;
        } else {
            Duration d = Duration.ofDays(dur.getDays()).plusHours(dur.getHours()).plusMinutes(dur.getMinutes()).plusSeconds(dur.getSeconds());
            if (dur.isNegative()) {
                d = d.negated();
            }
            duration = d;
        }
        return new TemporalAmountAdapter(duration);
    }

    public final Date getTime(Date start) {
        return Date.from(Instant.from(this.duration.addTo(start.toInstant())));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TemporalAmountAdapter that = (TemporalAmountAdapter)o;
        return new EqualsBuilder().append((Object)this.duration, (Object)that.duration).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.duration).toHashCode();
    }
}

