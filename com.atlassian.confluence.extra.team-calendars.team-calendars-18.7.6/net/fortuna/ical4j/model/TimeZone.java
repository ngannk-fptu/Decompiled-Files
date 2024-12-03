/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

public class TimeZone
extends java.util.TimeZone {
    private static final long serialVersionUID = -5620979316746547234L;
    private final VTimeZone vTimeZone;
    private final int rawOffset;

    public TimeZone(VTimeZone vTimeZone) {
        this.vTimeZone = vTimeZone;
        TzId tzId = (TzId)vTimeZone.getProperty("TZID");
        this.setID(tzId.getValue());
        this.rawOffset = TimeZone.getRawOffset(vTimeZone);
    }

    @Override
    public final int getOffset(int era, int year, int month, int dayOfMonth, int dayOfWeek, int milliseconds) {
        int ms = milliseconds;
        int hour = ms / 3600000;
        int minute = (ms -= hour * 3600000) / 60000;
        int second = (ms -= minute * 60000) / 1000;
        ms -= second * 1000;
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(0, era);
        cal.set(7, dayOfWeek);
        cal.set(year, month, dayOfMonth, hour, minute, second);
        cal.set(14, ms);
        Observance observance = this.vTimeZone.getApplicableObservance(new DateTime(cal.getTime()));
        if (observance != null) {
            TzOffsetTo offset = (TzOffsetTo)observance.getProperty("TZOFFSETTO");
            return (int)((long)offset.getOffset().getTotalSeconds() * 1000L);
        }
        return 0;
    }

    @Override
    public int getOffset(long date) {
        Observance observance = this.vTimeZone.getApplicableObservance(new DateTime(date));
        if (observance != null) {
            TzOffsetTo offset = (TzOffsetTo)observance.getProperty("TZOFFSETTO");
            if ((long)offset.getOffset().getTotalSeconds() * 1000L < (long)this.getRawOffset()) {
                return this.getRawOffset();
            }
            return (int)((long)offset.getOffset().getTotalSeconds() * 1000L);
        }
        return 0;
    }

    @Override
    public final int getRawOffset() {
        return this.rawOffset;
    }

    @Override
    public final boolean inDaylightTime(java.util.Date date) {
        Observance observance = this.vTimeZone.getApplicableObservance(new DateTime(date));
        return observance instanceof Daylight;
    }

    @Override
    public final void setRawOffset(int offsetMillis) {
        throw new UnsupportedOperationException("Updates to the VTIMEZONE object must be performed directly");
    }

    @Override
    public final boolean useDaylightTime() {
        ComponentList daylights = this.vTimeZone.getObservances().getComponents("DAYLIGHT");
        return !daylights.isEmpty();
    }

    public final VTimeZone getVTimeZone() {
        return this.vTimeZone;
    }

    private static int getRawOffset(VTimeZone vt) {
        TzOffsetTo offsetTo;
        ComponentList seasonalTimes = vt.getObservances().getComponents("STANDARD");
        if (seasonalTimes.isEmpty() && (seasonalTimes = vt.getObservances().getComponents("DAYLIGHT")).isEmpty()) {
            return 0;
        }
        Observance latestSeasonalTime = null;
        if (seasonalTimes.size() > 1) {
            DateTime now = new DateTime();
            Date latestOnset = null;
            for (Observance seasonalTime : seasonalTimes) {
                Date onset = seasonalTime.getLatestOnset(now);
                if (onset == null || latestOnset != null && !onset.after(latestOnset)) continue;
                latestOnset = onset;
                latestSeasonalTime = seasonalTime;
            }
        } else {
            latestSeasonalTime = (Observance)seasonalTimes.get(0);
        }
        if (latestSeasonalTime instanceof Daylight) {
            TzOffsetFrom offsetFrom = (TzOffsetFrom)latestSeasonalTime.getProperty("TZOFFSETFROM");
            if (offsetFrom != null) {
                return (int)((long)offsetFrom.getOffset().getTotalSeconds() * 1000L);
            }
        } else if (latestSeasonalTime instanceof Standard && (offsetTo = (TzOffsetTo)latestSeasonalTime.getProperty("TZOFFSETTO")) != null) {
            return (int)((long)offsetTo.getOffset().getTotalSeconds() * 1000L);
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TimeZone timeZone = (TimeZone)o;
        return this.rawOffset == timeZone.rawOffset && !(this.vTimeZone == null ? timeZone.vTimeZone != null : !this.vTimeZone.equals(timeZone.vTimeZone));
    }

    public int hashCode() {
        int result = this.vTimeZone != null ? this.vTimeZone.hashCode() : 0;
        result = 31 * result + this.rawOffset;
        return result;
    }
}

