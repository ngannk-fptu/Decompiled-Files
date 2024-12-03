/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimedOutException
extends TimeoutException {
    private static final long serialVersionUID = 2639693125779305458L;

    public TimedOutException(long time, TimeUnit unit) {
        super("Timed out after: " + time + " " + (Object)((Object)unit));
    }
}

