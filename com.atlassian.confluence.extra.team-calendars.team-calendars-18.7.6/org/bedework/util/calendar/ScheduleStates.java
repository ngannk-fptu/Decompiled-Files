/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.io.Serializable;

public interface ScheduleStates
extends Serializable {
    public static final int scheduleUnprocessed = -1;
    public static final int scheduleOk = 0;
    public static final int scheduleNoAccess = 1;
    public static final int scheduleDeferred = 2;
    public static final int scheduleIgnored = 3;
    public static final int scheduleError = 4;
}

