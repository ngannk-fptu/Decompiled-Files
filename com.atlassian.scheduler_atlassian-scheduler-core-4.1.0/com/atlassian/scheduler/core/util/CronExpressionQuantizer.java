/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.util;

import java.util.Random;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class CronExpressionQuantizer {
    private static final Pattern REGEX = Pattern.compile("^[0-5]?[0-9] ");
    private static final Random RANDOM = new Random();

    public static String quantizeSecondsField(String cronExpression) {
        return CronExpressionQuantizer.quantizeSecondsField(cronExpression, Randomize.AS_NEEDED);
    }

    public static String quantizeSecondsField(@Nullable String cronExpression, @Nullable Randomize randomize) {
        if (!CronExpressionQuantizer.shouldEdit(cronExpression, randomize)) {
            return cronExpression;
        }
        int pos = cronExpression.indexOf(32);
        if (pos <= 0) {
            return cronExpression;
        }
        StringBuilder sb = new StringBuilder(cronExpression.length());
        sb.append(randomize != Randomize.NEVER ? RANDOM.nextInt(60) : 0);
        return sb.append(cronExpression, pos, cronExpression.length()).toString();
    }

    private static boolean shouldEdit(@Nullable String cronExpression, @Nullable Randomize randomize) {
        if (cronExpression == null) {
            return false;
        }
        if (randomize == Randomize.ALWAYS) {
            return true;
        }
        return !REGEX.matcher(cronExpression).find();
    }

    public static enum Randomize {
        NEVER,
        AS_NEEDED,
        ALWAYS;

    }
}

