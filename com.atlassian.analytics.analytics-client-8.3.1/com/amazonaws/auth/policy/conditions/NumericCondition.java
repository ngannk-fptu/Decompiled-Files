/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.conditions;

import com.amazonaws.auth.policy.Condition;
import java.util.Arrays;

public class NumericCondition
extends Condition {
    public NumericCondition(NumericComparisonType type, String key, String value) {
        this.type = type.toString();
        this.conditionKey = key;
        this.values = Arrays.asList(value);
    }

    public static enum NumericComparisonType {
        NumericEquals,
        NumericGreaterThan,
        NumericGreaterThanEquals,
        NumericLessThan,
        NumericLessThanEquals,
        NumericNotEquals;

    }
}

