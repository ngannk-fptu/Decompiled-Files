/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.RetryCondition;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetryOnStatusCodeCondition
implements RetryCondition {
    private final List<Integer> statusCodesToRetryOn;

    public RetryOnStatusCodeCondition(List<Integer> statusCodesToRetryOn) {
        this.statusCodesToRetryOn = new ArrayList<Integer>((Collection)ValidationUtils.assertNotNull(statusCodesToRetryOn, "statusCodesToRetryOn"));
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        if (context.httpStatusCode() != null) {
            for (Integer statusCode : this.statusCodesToRetryOn) {
                if (!statusCode.equals(context.httpStatusCode())) continue;
                return true;
            }
        }
        return false;
    }
}

