/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.support;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;

final class QuartzCronField
extends CronField {
    private final CronField.Type rollForwardType;
    private final TemporalAdjuster adjuster;
    private final String value;

    private QuartzCronField(CronField.Type type, TemporalAdjuster adjuster, String value) {
        this(type, type, adjuster, value);
    }

    private QuartzCronField(CronField.Type type, CronField.Type rollForwardType, TemporalAdjuster adjuster, String value) {
        super(type);
        this.adjuster = adjuster;
        this.value = value;
        this.rollForwardType = rollForwardType;
    }

    public static boolean isQuartzDaysOfMonthField(String value) {
        return value.contains("L") || value.contains("W");
    }

    public static QuartzCronField parseDaysOfMonth(String value) {
        int idx = value.lastIndexOf(76);
        if (idx != -1) {
            TemporalAdjuster adjuster;
            if (idx != 0) {
                throw new IllegalArgumentException("Unrecognized characters before 'L' in '" + value + "'");
            }
            if (value.length() == 2 && value.charAt(1) == 'W') {
                adjuster = QuartzCronField.lastWeekdayOfMonth();
            } else if (value.length() == 1) {
                adjuster = QuartzCronField.lastDayOfMonth();
            } else {
                int offset = Integer.parseInt(value.substring(idx + 1));
                if (offset >= 0) {
                    throw new IllegalArgumentException("Offset '" + offset + " should be < 0 '" + value + "'");
                }
                adjuster = QuartzCronField.lastDayWithOffset(offset);
            }
            return new QuartzCronField(CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        idx = value.lastIndexOf(87);
        if (idx != -1) {
            if (idx == 0) {
                throw new IllegalArgumentException("No day-of-month before 'W' in '" + value + "'");
            }
            if (idx != value.length() - 1) {
                throw new IllegalArgumentException("Unrecognized characters after 'W' in '" + value + "'");
            }
            int dayOfMonth = Integer.parseInt(value.substring(0, idx));
            dayOfMonth = CronField.Type.DAY_OF_MONTH.checkValidValue(dayOfMonth);
            TemporalAdjuster adjuster = QuartzCronField.weekdayNearestTo(dayOfMonth);
            return new QuartzCronField(CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        throw new IllegalArgumentException("No 'L' or 'W' found in '" + value + "'");
    }

    public static boolean isQuartzDaysOfWeekField(String value) {
        return value.contains("L") || value.contains("#");
    }

    public static QuartzCronField parseDaysOfWeek(String value) {
        int idx = value.lastIndexOf(76);
        if (idx != -1) {
            if (idx != value.length() - 1) {
                throw new IllegalArgumentException("Unrecognized characters after 'L' in '" + value + "'");
            }
            if (idx == 0) {
                throw new IllegalArgumentException("No day-of-week before 'L' in '" + value + "'");
            }
            DayOfWeek dayOfWeek = QuartzCronField.parseDayOfWeek(value.substring(0, idx));
            TemporalAdjuster adjuster = QuartzCronField.lastInMonth(dayOfWeek);
            return new QuartzCronField(CronField.Type.DAY_OF_WEEK, CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        idx = value.lastIndexOf(35);
        if (idx != -1) {
            if (idx == 0) {
                throw new IllegalArgumentException("No day-of-week before '#' in '" + value + "'");
            }
            if (idx == value.length() - 1) {
                throw new IllegalArgumentException("No ordinal after '#' in '" + value + "'");
            }
            DayOfWeek dayOfWeek = QuartzCronField.parseDayOfWeek(value.substring(0, idx));
            int ordinal = Integer.parseInt(value.substring(idx + 1));
            if (ordinal <= 0) {
                throw new IllegalArgumentException("Ordinal '" + ordinal + "' in '" + value + "' must be positive number ");
            }
            TemporalAdjuster adjuster = QuartzCronField.dayOfWeekInMonth(ordinal, dayOfWeek);
            return new QuartzCronField(CronField.Type.DAY_OF_WEEK, CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        throw new IllegalArgumentException("No 'L' or '#' found in '" + value + "'");
    }

    private static DayOfWeek parseDayOfWeek(String value) {
        int dayOfWeek = Integer.parseInt(value);
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        try {
            return DayOfWeek.of(dayOfWeek);
        }
        catch (DateTimeException ex) {
            String msg = ex.getMessage() + " '" + value + "'";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    private static TemporalAdjuster atMidnight() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.NANO_OF_DAY)) {
                return temporal.with(ChronoField.NANO_OF_DAY, 0L);
            }
            return temporal;
        };
    }

    private static TemporalAdjuster lastDayOfMonth() {
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return QuartzCronField.rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster lastWeekdayOfMonth() {
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal lastDom = adjuster.adjustInto(temporal);
            int dow = lastDom.get(ChronoField.DAY_OF_WEEK);
            Temporal result = dow == 6 ? lastDom.minus(1L, ChronoUnit.DAYS) : (dow == 7 ? lastDom.minus(2L, ChronoUnit.DAYS) : lastDom);
            return QuartzCronField.rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster lastDayWithOffset(int offset) {
        Assert.isTrue((offset < 0 ? 1 : 0) != 0, (String)"Offset should be < 0");
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal).plus(offset, ChronoUnit.DAYS);
            return QuartzCronField.rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster weekdayNearestTo(int dayOfMonth) {
        return temporal -> {
            int current = CronField.Type.DAY_OF_MONTH.get(temporal);
            DayOfWeek dayOfWeek = DayOfWeek.from(temporal);
            if (current == dayOfMonth && QuartzCronField.isWeekday(dayOfWeek) || dayOfWeek == DayOfWeek.FRIDAY && current == dayOfMonth - 1 || dayOfWeek == DayOfWeek.MONDAY && current == dayOfMonth + 1 || dayOfWeek == DayOfWeek.MONDAY && dayOfMonth == 1 && current == 3) {
                return temporal;
            }
            int count = 0;
            while (count++ < 366) {
                if (current == dayOfMonth) {
                    dayOfWeek = DayOfWeek.from(temporal);
                    if (dayOfWeek == DayOfWeek.SATURDAY) {
                        temporal = dayOfMonth != 1 ? temporal.minus(1L, ChronoUnit.DAYS) : temporal.plus(2L, ChronoUnit.DAYS);
                    } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                        temporal = temporal.plus(1L, ChronoUnit.DAYS);
                    }
                    return QuartzCronField.atMidnight().adjustInto(temporal);
                }
                temporal = CronField.Type.DAY_OF_MONTH.elapseUntil(QuartzCronField.cast(temporal), dayOfMonth);
                current = CronField.Type.DAY_OF_MONTH.get(temporal);
            }
            return null;
        };
    }

    private static boolean isWeekday(DayOfWeek dayOfWeek) {
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private static TemporalAdjuster lastInMonth(DayOfWeek dayOfWeek) {
        TemporalAdjuster adjuster = TemporalAdjusters.lastInMonth(dayOfWeek);
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return QuartzCronField.rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        TemporalAdjuster adjuster = TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek);
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return QuartzCronField.rollbackToMidnight(temporal, result);
        };
    }

    private static Temporal rollbackToMidnight(Temporal current, Temporal result) {
        if (result.get(ChronoField.DAY_OF_MONTH) == current.get(ChronoField.DAY_OF_MONTH)) {
            return current;
        }
        return QuartzCronField.atMidnight().adjustInto(result);
    }

    @Override
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        T result = this.adjust(temporal);
        if (result != null && ((Comparable<T>)result).compareTo(temporal) < 0 && (result = this.adjust(temporal = this.rollForwardType.rollForward(temporal))) != null) {
            result = this.type().reset(result);
        }
        return result;
    }

    @Nullable
    private <T extends Temporal & Comparable<? super T>> T adjust(T temporal) {
        return (T)this.adjuster.adjustInto(temporal);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuartzCronField)) {
            return false;
        }
        QuartzCronField other = (QuartzCronField)o;
        return this.type() == other.type() && this.value.equals(other.value);
    }

    public String toString() {
        return (Object)((Object)this.type()) + " '" + this.value + "'";
    }
}

