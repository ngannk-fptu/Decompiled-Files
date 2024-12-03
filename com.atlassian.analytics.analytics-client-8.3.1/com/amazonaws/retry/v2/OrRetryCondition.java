/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.RetryCondition;
import com.amazonaws.retry.v2.RetryPolicyContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrRetryCondition
implements RetryCondition {
    private List<RetryCondition> conditions = new ArrayList<RetryCondition>();

    public OrRetryCondition(RetryCondition ... conditions) {
        Collections.addAll(this.conditions, conditions);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        for (RetryCondition retryCondition : this.conditions) {
            if (!retryCondition.shouldRetry(context)) continue;
            return true;
        }
        return false;
    }
}

