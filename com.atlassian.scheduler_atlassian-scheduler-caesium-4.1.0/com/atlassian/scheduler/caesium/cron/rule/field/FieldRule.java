/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import com.atlassian.scheduler.caesium.cron.rule.CronRule;
import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;

public interface FieldRule
extends CronRule {
    public int get(DateTimeTemplate var1);

    public void set(DateTimeTemplate var1, int var2);
}

