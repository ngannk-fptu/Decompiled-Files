/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.core.Clock;
import java.util.Date;

public class SystemClock
implements Clock {
    private static final Clock INSTANCE = new SystemClock();

    @Override
    public Date now() {
        return new Date();
    }

    public static Clock getInstance() {
        return INSTANCE;
    }
}

