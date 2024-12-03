/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronExpressionValidator
 *  com.atlassian.scheduler.cron.CronSyntaxException
 */
package com.atlassian.scheduler.caesium.cron;

import com.atlassian.scheduler.caesium.cron.parser.CronExpressionParser;
import com.atlassian.scheduler.cron.CronExpressionValidator;
import com.atlassian.scheduler.cron.CronSyntaxException;

public class CaesiumCronExpressionValidator
implements CronExpressionValidator {
    public boolean isValid(String cronExpression) {
        return CronExpressionParser.isValid(cronExpression);
    }

    public void validate(String cronExpression) throws CronSyntaxException {
        CronExpressionParser.parse(cronExpression);
    }
}

