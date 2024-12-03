/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ErrorHandler
 */
package org.springframework.scheduling.support;

import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

public class DelegatingErrorHandlingRunnable
implements Runnable {
    private final Runnable delegate;
    private final ErrorHandler errorHandler;

    public DelegatingErrorHandlingRunnable(Runnable delegate, ErrorHandler errorHandler) {
        Assert.notNull((Object)delegate, (String)"Delegate must not be null");
        Assert.notNull((Object)errorHandler, (String)"ErrorHandler must not be null");
        this.delegate = delegate;
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        try {
            this.delegate.run();
        }
        catch (UndeclaredThrowableException ex) {
            this.errorHandler.handleError(ex.getUndeclaredThrowable());
        }
        catch (Throwable ex) {
            this.errorHandler.handleError(ex);
        }
    }

    public String toString() {
        return "DelegatingErrorHandlingRunnable for " + this.delegate;
    }
}

