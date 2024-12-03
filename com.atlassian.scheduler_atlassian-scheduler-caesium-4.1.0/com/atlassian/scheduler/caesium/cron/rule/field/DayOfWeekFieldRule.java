/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.joda.time.LocalDate
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.AbstractFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.DayOfWeekConstantConverter;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Objects;
import org.joda.time.LocalDate;

public class DayOfWeekFieldRule
extends AbstractFieldRule {
    private static final long serialVersionUID = -5922219253093923249L;
    private static final DayOfWeekFieldRule SATURDAY = new DayOfWeekFieldRule();
    private final BitSet isoDaysOfWeek;

    public static DayOfWeekFieldRule saturday() {
        return SATURDAY;
    }

    private DayOfWeekFieldRule() {
        super(DateTimeTemplate.Field.DAY);
        BitSet saturday = new BitSet(8);
        saturday.set(6);
        this.isoDaysOfWeek = saturday;
    }

    private DayOfWeekFieldRule(BitSet values) {
        super(DateTimeTemplate.Field.DAY);
        Objects.requireNonNull(values, "values");
        Preconditions.checkArgument((!values.isEmpty() ? 1 : 0) != 0, (Object)"values cannot be empty");
        Preconditions.checkArgument((!values.get(0) ? 1 : 0) != 0, (Object)"values cannot contain 0");
        Preconditions.checkArgument((values.nextSetBit(8) == -1 ? 1 : 0) != 0, (Object)"values cannot contain anything > 7");
        this.isoDaysOfWeek = DayOfWeekConstantConverter.cronToIso(values);
    }

    public static FieldRule of(BitSet values) {
        return new DayOfWeekFieldRule(values);
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        LocalDate firstOfMonth = dateTime.toFirstOfMonth();
        int lastDayOfMonth = firstOfMonth.dayOfMonth().getMaximumValue();
        if (dateTime.getDay() > lastDayOfMonth) {
            return false;
        }
        int isoDayOfWeek = DayOfWeekFieldRule.getIsoDayOfWeek(firstOfMonth, dateTime.getDay());
        return this.isoDaysOfWeek.get(isoDayOfWeek);
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        int isoDayOfWeek = dateTime.toFirstOfMonth().getDayOfWeek();
        if (this.isoDaysOfWeek.get(isoDayOfWeek)) {
            dateTime.setDay(1);
        } else {
            dateTime.setDay(1 + this.interval(isoDayOfWeek));
        }
        return true;
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        int lastDayOfMonth;
        LocalDate firstOfMonth = dateTime.toFirstOfMonth();
        int isoDayOfWeek = DayOfWeekFieldRule.getIsoDayOfWeek(firstOfMonth, dateTime.getDay());
        int day = dateTime.getDay() + this.interval(isoDayOfWeek);
        if (day > (lastDayOfMonth = firstOfMonth.dayOfMonth().getMaximumValue())) {
            return false;
        }
        dateTime.setDay(day);
        return true;
    }

    private int interval(int isoDayOfWeek) {
        int next = this.isoDaysOfWeek.nextSetBit(isoDayOfWeek + 1);
        if (next != -1) {
            return next - isoDayOfWeek;
        }
        next = this.isoDaysOfWeek.nextSetBit(1);
        return next - isoDayOfWeek + 7;
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        int bit = this.isoDaysOfWeek.nextSetBit(1);
        if (bit == 1 && this.isoDaysOfWeek.nextClearBit(2) == 8) {
            sb.append("*(dow)");
            return;
        }
        while (bit != -1) {
            sb.append(DayOfWeekConstantConverter.isoToName(bit)).append(',');
            bit = this.isoDaysOfWeek.nextSetBit(bit + 1);
        }
        sb.setLength(sb.length() - 1);
    }

    protected Object writeReplace() {
        return this == SATURDAY ? SaturdaySentinel.INSTANCE : this;
    }

    private static int getIsoDayOfWeek(LocalDate firstOfMonth, int dayOfMonth) {
        int dayOfWeek = (firstOfMonth.getDayOfWeek() + dayOfMonth - 1) % 7;
        return dayOfWeek != 0 ? dayOfWeek : 7;
    }

    static final class SaturdaySentinel
    implements Serializable {
        private static final long serialVersionUID = 1925593671579829200L;
        static final SaturdaySentinel INSTANCE = new SaturdaySentinel();

        SaturdaySentinel() {
        }

        protected Object readResolve() {
            return DayOfWeekFieldRule.saturday();
        }
    }
}

