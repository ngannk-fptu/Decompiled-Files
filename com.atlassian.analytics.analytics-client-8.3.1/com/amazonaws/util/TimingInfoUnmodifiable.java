/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.util.TimingInfo;

@ThreadSafe
final class TimingInfoUnmodifiable
extends TimingInfo {
    TimingInfoUnmodifiable(Long startEpochTimeMilli, long startTimeNano, Long endTimeNano) {
        super(startEpochTimeMilli, startTimeNano, endTimeNano);
    }

    @Override
    public void setEndTime(long _) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEndTimeNano(long _) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimingInfo endTiming() {
        throw new UnsupportedOperationException();
    }
}

