/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule;

import com.atlassian.scheduler.caesium.cron.rule.CompositeRule;
import com.atlassian.scheduler.caesium.cron.rule.CronRule;
import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.caesium.cron.rule.field.FieldRule;
import java.util.Objects;

public class CronExpression
implements CronRule {
    private static final long serialVersionUID = -5039113142559402448L;
    private final String cronExpression;
    private final CronRule delegate;

    public CronExpression(String cronExpression, FieldRule year, FieldRule month, FieldRule day, FieldRule hour, FieldRule minute, FieldRule second) {
        this.cronExpression = Objects.requireNonNull(cronExpression, "cronExpression");
        CronRule rule = second;
        rule = CompositeRule.compose(minute, rule);
        rule = CompositeRule.compose(hour, rule);
        rule = CompositeRule.compose(day, rule);
        rule = CompositeRule.compose(month, rule);
        this.delegate = CompositeRule.compose(year, rule);
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        return this.delegate.matches(dateTime);
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        return this.delegate.next(dateTime);
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        return this.delegate.first(dateTime);
    }

    public String toString() {
        return "CronExpression[" + this.cronExpression + ":\n\t" + this.delegate + "\n]";
    }
}

