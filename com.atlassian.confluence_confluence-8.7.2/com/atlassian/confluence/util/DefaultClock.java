/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.Clock
 */
package com.atlassian.confluence.util;

import com.atlassian.core.util.Clock;
import java.util.Date;

public final class DefaultClock
implements Clock {
    public Date getCurrentDate() {
        return new Date();
    }
}

