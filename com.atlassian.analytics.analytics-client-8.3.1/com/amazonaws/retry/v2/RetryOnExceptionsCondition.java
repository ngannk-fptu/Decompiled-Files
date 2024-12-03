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

public class RetryOnExceptionsCondition
implements RetryCondition {
    private final List<Class<? extends Exception>> exceptionsToRetryOn;

    public RetryOnExceptionsCondition(List<Class<? extends Exception>> exceptionsToRetryOn) {
        this.exceptionsToRetryOn = new ArrayList<Class<? extends Exception>>((Collection)ValidationUtils.assertNotNull(exceptionsToRetryOn, "exceptionsToRetryOn"));
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        if (context.exception() != null) {
            for (Class<? extends Exception> exceptionClass : this.exceptionsToRetryOn) {
                if (this.exceptionMatches(context, exceptionClass)) {
                    return true;
                }
                if (!this.wrappedCauseMatches(context, exceptionClass)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean exceptionMatches(RetryPolicyContext context, Class<? extends Exception> exceptionClass) {
        return context.exception().getClass().equals(exceptionClass);
    }

    private boolean wrappedCauseMatches(RetryPolicyContext context, Class<? extends Exception> exceptionClass) {
        if (context.exception().getCause() == null) {
            return false;
        }
        return context.exception().getCause().getClass().equals(exceptionClass);
    }
}

