/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.AbstractFieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import com.atlassian.scheduler.caesium.cron.rule.field.RangeFieldRule;
import com.google.common.base.Preconditions;
import java.util.BitSet;
import java.util.Objects;

public class BitSetFieldRule
extends AbstractFieldRule {
    private static final long serialVersionUID = 4225435919726324942L;
    private final BitSet values;

    private BitSetFieldRule(DateTimeTemplate.Field field, BitSet values) {
        super(field);
        this.values = (BitSet)values.clone();
    }

    public static FieldRule of(DateTimeTemplate.Field field, BitSet values) {
        Objects.requireNonNull(values, "values");
        int firstSetBit = values.nextSetBit(0);
        Preconditions.checkArgument((firstSetBit != -1 ? 1 : 0) != 0, (Object)"values cannot be empty");
        Preconditions.checkArgument((firstSetBit >= field.getMinimumValue() ? 1 : 0) != 0, (Object)"values cannot contain bits less than the field minimum");
        Preconditions.checkArgument((values.nextSetBit(field.getMaximumValue() + 1) == -1 ? 1 : 0) != 0, (Object)"values cannot contain bits more than the field maximum");
        int nextClearBit = values.nextClearBit(firstSetBit + 1);
        int separatedBit = values.nextSetBit(nextClearBit + 1);
        if (separatedBit == -1) {
            return RangeFieldRule.of(field, firstSetBit, nextClearBit - 1);
        }
        return new BitSetFieldRule(field, values);
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        return this.values.get(this.get(dateTime));
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        int value = this.values.nextSetBit(this.get(dateTime) + 1);
        if (value == -1) {
            return false;
        }
        this.set(dateTime, value);
        return true;
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        this.set(dateTime, this.values.nextSetBit(this.field.getMinimumValue()));
        return true;
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        int bit = this.values.nextSetBit(this.field.getMinimumValue());
        bit = this.appendRangeTo(sb, bit);
        bit = this.values.nextSetBit(bit);
        while (bit != -1) {
            sb.append(',');
            bit = this.appendRangeTo(sb, bit);
            bit = this.values.nextSetBit(bit);
        }
    }

    private int appendRangeTo(StringBuilder sb, int startBit) {
        sb.append(startBit);
        int nextBit = this.values.nextClearBit(startBit + 1);
        int lastBit = nextBit - 1;
        if (lastBit > startBit) {
            sb.append('-').append(lastBit);
        }
        return nextBit;
    }
}

