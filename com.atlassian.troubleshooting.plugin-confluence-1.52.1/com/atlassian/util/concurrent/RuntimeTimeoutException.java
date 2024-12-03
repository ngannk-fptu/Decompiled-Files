/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.TimedOutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RuntimeTimeoutException
extends RuntimeException {
    private static final long serialVersionUID = -5025209597479375477L;

    public RuntimeTimeoutException(TimeoutException cause) {
        super(Assertions.notNull("cause", cause));
    }

    public RuntimeTimeoutException(String message, TimeoutException cause) {
        super(message, Assertions.notNull("cause", cause));
    }

    public RuntimeTimeoutException(long time, TimeUnit unit) {
        super(new TimedOutException(time, unit));
    }

    public TimeoutException getCause() {
        return (TimeoutException)super.getCause();
    }
}

