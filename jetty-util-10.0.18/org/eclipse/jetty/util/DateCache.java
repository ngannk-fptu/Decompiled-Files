/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateCache {
    public static final String DEFAULT_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private final String _formatString;
    private final DateTimeFormatter _tzFormat1;
    private final DateTimeFormatter _tzFormat2;
    private final ZoneId _zoneId;
    private volatile TickHolder _tickHolder;

    public DateCache() {
        this(DEFAULT_FORMAT);
    }

    public DateCache(String format) {
        this(format, null, TimeZone.getDefault());
    }

    public DateCache(String format, Locale l) {
        this(format, l, TimeZone.getDefault());
    }

    public DateCache(String format, Locale l, String tz) {
        this(format, l, TimeZone.getTimeZone(tz));
    }

    public DateCache(String format, Locale l, TimeZone tz) {
        this(format, l, tz, true);
    }

    public DateCache(String format, Locale l, TimeZone tz, boolean subSecondPrecision) {
        boolean subSecond;
        this._formatString = format = format.replaceFirst("S+", "SSS");
        this._zoneId = tz.toZoneId();
        String format1 = format;
        String format2 = null;
        if (subSecondPrecision) {
            int msIndex = format.indexOf("SSS");
            boolean bl = subSecond = msIndex >= 0;
            if (subSecond) {
                format1 = format.substring(0, msIndex);
                format2 = format.substring(msIndex + 3);
            }
        } else {
            subSecond = false;
            format1 = format.replace("SSS", "000");
        }
        this._tzFormat1 = this.createFormatter(format1, l, this._zoneId);
        this._tzFormat2 = subSecond ? this.createFormatter(format2, l, this._zoneId) : null;
    }

    private DateTimeFormatter createFormatter(String format, Locale locale, ZoneId zoneId) {
        if (locale == null) {
            return DateTimeFormatter.ofPattern(format).withZone(zoneId);
        }
        return DateTimeFormatter.ofPattern(format, locale).withZone(zoneId);
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(this._zoneId);
    }

    public String format(Date inDate) {
        return this.format(inDate.getTime());
    }

    public String format(long inDate) {
        return this.formatTick(inDate).format(inDate);
    }

    protected String doFormat(long inDate, DateTimeFormatter formatter) {
        if (formatter == null) {
            return null;
        }
        return formatter.format(Instant.ofEpochMilli(inDate));
    }

    @Deprecated
    public String formatNow(long now) {
        return this.format(now);
    }

    @Deprecated
    public String now() {
        return this.formatNow(System.currentTimeMillis());
    }

    @Deprecated
    public Tick tick() {
        return this.formatTick(System.currentTimeMillis());
    }

    protected Tick formatTick(long inDate) {
        long seconds = inDate / 1000L;
        TickHolder holder = this._tickHolder;
        if (holder != null) {
            if (holder.tick1 != null && holder.tick1.getSeconds() == seconds) {
                return holder.tick1;
            }
            if (holder.tick2 != null && holder.tick2.getSeconds() == seconds) {
                return holder.tick2;
            }
        }
        String prefix = this.doFormat(inDate, this._tzFormat1);
        String suffix = this.doFormat(inDate, this._tzFormat2);
        Tick tick = new Tick(seconds, prefix, suffix);
        this._tickHolder = new TickHolder(tick, holder == null ? null : holder.tick1);
        return tick;
    }

    public String getFormatString() {
        return this._formatString;
    }

    public static class Tick {
        private final long _seconds;
        private final String _prefix;
        private final String _suffix;

        public Tick(long seconds, String prefix, String suffix) {
            this._seconds = seconds;
            this._prefix = prefix;
            this._suffix = suffix;
        }

        public long getSeconds() {
            return this._seconds;
        }

        public String format(long inDate) {
            if (this._suffix == null) {
                return this._prefix;
            }
            long ms = inDate % 1000L;
            StringBuilder sb = new StringBuilder();
            sb.append(this._prefix);
            if (ms < 10L) {
                sb.append("00").append(ms);
            } else if (ms < 100L) {
                sb.append('0').append(ms);
            } else {
                sb.append(ms);
            }
            sb.append(this._suffix);
            return sb.toString();
        }
    }

    private static class TickHolder {
        final Tick tick1;
        final Tick tick2;

        public TickHolder(Tick t1, Tick t2) {
            this.tick1 = t1;
            this.tick2 = t2;
        }
    }
}

