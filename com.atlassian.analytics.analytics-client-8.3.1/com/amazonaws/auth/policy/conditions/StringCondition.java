/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.conditions;

import com.amazonaws.auth.policy.Condition;
import java.util.Arrays;

public class StringCondition
extends Condition {
    public StringCondition(StringComparisonType type, String key, String value) {
        this.type = type.toString();
        this.conditionKey = key;
        this.values = Arrays.asList(value);
    }

    public static enum StringComparisonType {
        StringEquals,
        StringEqualsIgnoreCase,
        StringLike,
        StringNotEquals,
        StringNotEqualsIgnoreCase,
        StringNotLike;

    }
}

