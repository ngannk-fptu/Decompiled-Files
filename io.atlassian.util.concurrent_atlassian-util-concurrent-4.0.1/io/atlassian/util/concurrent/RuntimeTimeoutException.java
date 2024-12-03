/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.TimedOutException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RuntimeTimeoutException
extends RuntimeException {
    private static final long serialVersionUID = -5025209597479375477L;

    public RuntimeTimeoutException(TimeoutException cause) {
        super(Objects.requireNonNull(cause, "cause"));
    }

    public RuntimeTimeoutException(String message, TimeoutException cause) {
        super(message, Objects.requireNonNull(cause, "cause"));
    }

    public RuntimeTimeoutException(long time, TimeUnit unit) {
        super(new TimedOutException(time, unit));
    }

    @Override
    public TimeoutException getCause() {
        return (TimeoutException)super.getCause();
    }
}

