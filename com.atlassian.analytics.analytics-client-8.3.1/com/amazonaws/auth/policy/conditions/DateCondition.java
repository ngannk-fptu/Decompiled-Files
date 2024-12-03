/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.conditions;

import com.amazonaws.auth.policy.Condition;
import com.amazonaws.util.DateUtils;
import java.util.Arrays;
import java.util.Date;

public class DateCondition
extends Condition {
    public DateCondition(DateComparisonType type, Date date) {
        this.type = type.toString();
        this.conditionKey = "aws:CurrentTime";
        this.values = Arrays.asList(DateUtils.formatISO8601Date(date));
    }

    public static enum DateComparisonType {
        DateEquals,
        DateGreaterThan,
        DateGreaterThanEquals,
        DateLessThan,
        DateLessThanEquals,
        DateNotEquals;

    }
}

