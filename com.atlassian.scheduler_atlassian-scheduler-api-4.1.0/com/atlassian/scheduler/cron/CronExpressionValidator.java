/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.scheduler.cron;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.cron.CronSyntaxException;

@PublicApi
public interface CronExpressionValidator {
    public boolean isValid(String var1);

    public void validate(String var1) throws CronSyntaxException;
}

