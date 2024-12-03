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
import com.atlassian.scheduler.caesium.cron.rule.field.SingleValueFieldRule;
import com.google.common.base.Preconditions;

public class RangeFieldRule
extends AbstractFieldRule {
    private static final long serialVersionUID = -8064232881796546833L;
    private final int min;
    private final int max;

    private RangeFieldRule(DateTimeTemplate.Field field, int min, int max) {
        super(field);
        this.min = min;
        this.max = max;
    }

    public static FieldRule of(DateTimeTemplate.Field field, int min, int max) {
        Preconditions.checkArgument((min >= 0 ? 1 : 0) != 0, (Object)"min >= 0");
        if (min == max) {
            return new SingleValueFieldRule(field, min);
        }
        Preconditions.checkArgument((min < max ? 1 : 0) != 0, (Object)"min < max");
        return new RangeFieldRule(field, min, max);
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        int value = this.get(dateTime);
        return value >= this.min && value <= this.max;
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        this.set(dateTime, this.min);
        return true;
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        int value = this.get(dateTime);
        if (value >= this.max) {
            return false;
        }
        int nextValue = value < this.min ? this.min : value + 1;
        this.set(dateTime, nextValue);
        return true;
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        if (this.min == this.field.getMinimumValue() && this.max == this.field.getMaximumValue()) {
            sb.append('*');
        } else {
            sb.append(this.min).append('-').append(this.max);
        }
    }
}

