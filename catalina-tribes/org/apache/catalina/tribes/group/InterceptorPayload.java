/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import org.apache.catalina.tribes.ErrorHandler;

public class InterceptorPayload {
    private ErrorHandler errorHandler;

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}

