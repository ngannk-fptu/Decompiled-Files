/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.cron.CaesiumCronExpressionValidator
 *  com.atlassian.scheduler.cron.CronExpressionValidator
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.scheduler.caesium.cron.CaesiumCronExpressionValidator;
import com.atlassian.scheduler.cron.CronExpressionValidator;
import com.google.common.base.Preconditions;

public class CrowdCronExpressionValidator {
    private static final CronExpressionValidator cronExpressionValidator = new CaesiumCronExpressionValidator();

    private CrowdCronExpressionValidator() {
    }

    public static boolean isValid(String cronExpression) {
        Preconditions.checkArgument((cronExpression != null ? 1 : 0) != 0, (Object)"cron expression must not be null");
        return cronExpressionValidator.isValid(cronExpression);
    }
}

