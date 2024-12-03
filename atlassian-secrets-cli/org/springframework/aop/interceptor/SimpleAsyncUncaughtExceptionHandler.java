/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class SimpleAsyncUncaughtExceptionHandler
implements AsyncUncaughtExceptionHandler {
    private static final Log logger = LogFactory.getLog(SimpleAsyncUncaughtExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object ... params) {
        if (logger.isErrorEnabled()) {
            logger.error("Unexpected error occurred invoking async method: " + method, ex);
        }
    }
}

