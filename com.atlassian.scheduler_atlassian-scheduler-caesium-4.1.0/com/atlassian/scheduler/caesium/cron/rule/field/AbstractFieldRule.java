/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import java.util.Objects;

abstract class AbstractFieldRule
implements FieldRule {
    private static final long serialVersionUID = 8456955222871836404L;
    protected final DateTimeTemplate.Field field;

    AbstractFieldRule(DateTimeTemplate.Field field) {
        this.field = Objects.requireNonNull(field, "dateTimeFieldType");
    }

    @Override
    public int get(DateTimeTemplate dateTime) {
        return this.field.get(dateTime);
    }

    @Override
    public void set(DateTimeTemplate dateTime, int value) {
        this.field.set(dateTime, value);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64).append((Object)this.field).append(": ");
        this.appendTo(sb);
        return sb.toString();
    }

    protected abstract void appendTo(StringBuilder var1);
}

