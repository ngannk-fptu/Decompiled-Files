/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.util.TimeValue;

public class Deadline {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final long INTERNAL_MAX_VALUE = Long.MAX_VALUE;
    private static final long INTERNAL_MIN_VALUE = 0L;
    public static Deadline MAX_VALUE = new Deadline(Long.MAX_VALUE);
    public static Deadline MIN_VALUE = new Deadline(0L);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").toFormatter();
    private volatile boolean frozen;
    private volatile long lastCheck;
    private final long value;

    public static Deadline calculate(long timeMillis, TimeValue timeValue) {
        if (TimeValue.isPositive(timeValue)) {
            long deadline = timeMillis + timeValue.toMilliseconds();
            return deadline < 0L ? MAX_VALUE : Deadline.fromUnixMilliseconds(deadline);
        }
        return MAX_VALUE;
    }

    public static Deadline calculate(TimeValue timeValue) {
        return Deadline.calculate(System.currentTimeMillis(), timeValue);
    }

    public static Deadline fromUnixMilliseconds(long value) {
        if (value == Long.MAX_VALUE) {
            return MAX_VALUE;
        }
        if (value == 0L) {
            return MIN_VALUE;
        }
        return new Deadline(value);
    }

    public static Deadline parse(String source) throws ParseException {
        if (source == null) {
            return null;
        }
        Instant instant = Instant.from(DATE_TIME_FORMATTER.parse(source));
        return Deadline.fromUnixMilliseconds(instant.toEpochMilli());
    }

    private Deadline(long deadlineMillis) {
        this.value = deadlineMillis;
        this.setLastCheck();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Deadline other = (Deadline)obj;
        return this.value == other.value;
    }

    public int hashCode() {
        return Long.hashCode(this.value);
    }

    public String format(TimeUnit overdueTimeUnit) {
        if (this.value == Deadline.MAX_VALUE.value) {
            return "No deadline (infinite)";
        }
        return String.format("Deadline: %s, %s overdue", this.formatTarget(), TimeValue.of(this.remaining(), overdueTimeUnit));
    }

    public String formatTarget() {
        if (this.value == Deadline.MAX_VALUE.value) {
            return "(infinite)";
        }
        return DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(this.value).atOffset(ZoneOffset.UTC));
    }

    public Deadline freeze() {
        this.frozen = true;
        return this;
    }

    long getLastCheck() {
        return this.lastCheck;
    }

    public long getValue() {
        return this.value;
    }

    public boolean isBefore(long millis) {
        return this.value < millis;
    }

    public boolean isExpired() {
        this.setLastCheck();
        return this.value < this.lastCheck;
    }

    public boolean isMax() {
        return this.value == Long.MAX_VALUE;
    }

    public boolean isMin() {
        return this.value == 0L;
    }

    public boolean isNotExpired() {
        this.setLastCheck();
        return this.value >= this.lastCheck;
    }

    public Deadline min(Deadline other) {
        return this.value <= other.value ? this : other;
    }

    public long remaining() {
        this.setLastCheck();
        return this.value - this.lastCheck;
    }

    public TimeValue remainingTimeValue() {
        return TimeValue.of(this.remaining(), TimeUnit.MILLISECONDS);
    }

    private void setLastCheck() {
        if (!this.frozen) {
            this.lastCheck = System.currentTimeMillis();
        }
    }

    public String toString() {
        return this.formatTarget();
    }
}

