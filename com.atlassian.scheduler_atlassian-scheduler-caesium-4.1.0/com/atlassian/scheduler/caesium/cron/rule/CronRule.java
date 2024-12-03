/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule;

import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import java.io.Serializable;

public interface CronRule
extends Serializable {
    public boolean matches(DateTimeTemplate var1);

    public boolean first(DateTimeTemplate var1);

    public boolean next(DateTimeTemplate var1);
}

