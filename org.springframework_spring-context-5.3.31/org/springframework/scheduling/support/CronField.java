/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.scheduling.support;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.ValueRange;
import java.util.function.BiFunction;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.BitsCronField;
import org.springframework.scheduling.support.CompositeCronField;
import org.springframework.scheduling.support.QuartzCronField;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

abstract class CronField {
    private static final String[] MONTHS = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private static final String[] DAYS = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private final Type type;

    protected CronField(Type type) {
        this.type = type;
    }

    public static CronField zeroNanos() {
        return BitsCronField.zeroNanos();
    }

    public static CronField parseSeconds(String value) {
        return BitsCronField.parseSeconds(value);
    }

    public static CronField parseMinutes(String value) {
        return BitsCronField.parseMinutes(value);
    }

    public static CronField parseHours(String value) {
        return BitsCronField.parseHours(value);
    }

    public static CronField parseDaysOfMonth(String value) {
        if (!QuartzCronField.isQuartzDaysOfMonthField(value)) {
            return BitsCronField.parseDaysOfMonth(value);
        }
        return CronField.parseList(value, Type.DAY_OF_MONTH, (field, type) -> {
            if (QuartzCronField.isQuartzDaysOfMonthField(field)) {
                return QuartzCronField.parseDaysOfMonth(field);
            }
            return BitsCronField.parseDaysOfMonth(field);
        });
    }

    public static CronField parseMonth(String value) {
        value = CronField.replaceOrdinals(value, MONTHS);
        return BitsCronField.parseMonth(value);
    }

    public static CronField parseDaysOfWeek(String value) {
        if (!QuartzCronField.isQuartzDaysOfWeekField(value = CronField.replaceOrdinals(value, DAYS))) {
            return BitsCronField.parseDaysOfWeek(value);
        }
        return CronField.parseList(value, Type.DAY_OF_WEEK, (field, type) -> {
            if (QuartzCronField.isQuartzDaysOfWeekField(field)) {
                return QuartzCronField.parseDaysOfWeek(field);
            }
            return BitsCronField.parseDaysOfWeek(field);
        });
    }

    private static CronField parseList(String value, Type type, BiFunction<String, Type, CronField> parseFieldFunction) {
        Assert.hasLength((String)value, (String)"Value must not be empty");
        String[] fields = StringUtils.delimitedListToStringArray((String)value, (String)",");
        CronField[] cronFields = new CronField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            cronFields[i] = parseFieldFunction.apply(fields[i], type);
        }
        return CompositeCronField.compose(cronFields, type, value);
    }

    private static String replaceOrdinals(String value, String[] list) {
        value = value.toUpperCase();
        for (int i = 0; i < list.length; ++i) {
            String replacement = Integer.toString(i + 1);
            value = StringUtils.replace((String)value, (String)list[i], (String)replacement);
        }
        return value;
    }

    @Nullable
    public abstract <T extends Temporal & Comparable<? super T>> T nextOrSame(T var1);

    protected Type type() {
        return this.type;
    }

    protected static <T extends Temporal & Comparable<? super T>> T cast(Temporal temporal) {
        return (T)temporal;
    }

    protected static enum Type {
        NANO(ChronoField.NANO_OF_SECOND, ChronoUnit.SECONDS, new ChronoField[0]),
        SECOND(ChronoField.SECOND_OF_MINUTE, ChronoUnit.MINUTES, ChronoField.NANO_OF_SECOND),
        MINUTE(ChronoField.MINUTE_OF_HOUR, ChronoUnit.HOURS, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        HOUR(ChronoField.HOUR_OF_DAY, ChronoUnit.DAYS, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        DAY_OF_MONTH(ChronoField.DAY_OF_MONTH, ChronoUnit.MONTHS, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        MONTH(ChronoField.MONTH_OF_YEAR, ChronoUnit.YEARS, ChronoField.DAY_OF_MONTH, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        DAY_OF_WEEK(ChronoField.DAY_OF_WEEK, ChronoUnit.WEEKS, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND);

        private final ChronoField field;
        private final ChronoUnit higherOrder;
        private final ChronoField[] lowerOrders;

        private Type(ChronoField field, ChronoUnit higherOrder, ChronoField ... lowerOrders) {
            this.field = field;
            this.higherOrder = higherOrder;
            this.lowerOrders = lowerOrders;
        }

        public int get(Temporal date) {
            return date.get(this.field);
        }

        public ValueRange range() {
            return this.field.range();
        }

        public int checkValidValue(int value) {
            if (this == DAY_OF_WEEK && value == 0) {
                return value;
            }
            try {
                return this.field.checkValidIntValue(value);
            }
            catch (DateTimeException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        public <T extends Temporal & Comparable<? super T>> T elapseUntil(T temporal, int goal) {
            int current = this.get(temporal);
            ValueRange range = temporal.range(this.field);
            if (current < goal) {
                if (range.isValidIntValue(goal)) {
                    return CronField.cast(temporal.with(this.field, goal));
                }
                long amount = range.getMaximum() - (long)current + 1L;
                return this.field.getBaseUnit().addTo(temporal, amount);
            }
            long amount = (long)goal + range.getMaximum() - (long)current + 1L - range.getMinimum();
            return this.field.getBaseUnit().addTo(temporal, amount);
        }

        public <T extends Temporal & Comparable<? super T>> T rollForward(T temporal) {
            T result = this.higherOrder.addTo(temporal, 1L);
            ValueRange range = result.range(this.field);
            return this.field.adjustInto(result, range.getMinimum());
        }

        public <T extends Temporal> T reset(T temporal) {
            for (ChronoField lowerOrder : this.lowerOrders) {
                if (!temporal.isSupported(lowerOrder)) continue;
                temporal = lowerOrder.adjustInto(temporal, temporal.range(lowerOrder).getMinimum());
            }
            return temporal;
        }

        public String toString() {
            return this.field.toString();
        }
    }
}

