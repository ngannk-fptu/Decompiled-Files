/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.AbstractFieldRule;

public class SingleValueFieldRule
extends AbstractFieldRule {
    private static final long serialVersionUID = 4153982970557111861L;
    private final int acceptedValue;

    public SingleValueFieldRule(DateTimeTemplate.Field field, int acceptedValue) {
        super(field);
        this.acceptedValue = acceptedValue;
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        return this.get(dateTime) == this.acceptedValue;
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        this.set(dateTime, this.acceptedValue);
        return true;
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        if (this.get(dateTime) >= this.acceptedValue) {
            return false;
        }
        this.set(dateTime, this.acceptedValue);
        return true;
    }

    @Override
    protected void appendTo(StringBuilder sb) {
        sb.append(this.acceptedValue);
    }
}

