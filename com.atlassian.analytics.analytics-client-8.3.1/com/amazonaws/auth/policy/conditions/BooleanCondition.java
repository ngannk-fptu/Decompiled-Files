/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.conditions;

import com.amazonaws.auth.policy.Condition;
import java.util.Arrays;

public class BooleanCondition
extends Condition {
    public BooleanCondition(String key, boolean value) {
        this.type = "Bool";
        this.conditionKey = key;
        this.values = Arrays.asList(Boolean.toString(value));
    }
}

