/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package com.atlassian.migration.agent.service.util;

import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import java.io.InterruptedIOException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class StopConditionCheckingUtil {
    private StopConditionCheckingUtil() {
    }

    public static void throwIfStopConditionWasReached() {
        if (StopConditionCheckingUtil.isStopConditionReached()) {
            throw new UncheckedInterruptedException();
        }
    }

    public static boolean isStopConditionReached() {
        return Thread.currentThread().isInterrupted();
    }

    public static boolean isStoppingExceptionInCausalChain(Exception e) {
        if (StopConditionCheckingUtil.isStoppingException(e)) {
            return true;
        }
        return ExceptionUtils.getThrowableList((Throwable)e).stream().anyMatch(throwable -> StopConditionCheckingUtil.isStoppingException((Exception)throwable));
    }

    public static boolean isStoppingException(Exception e) {
        return e instanceof InterruptedException || e instanceof UncheckedInterruptedException || e instanceof InterruptedIOException;
    }
}

