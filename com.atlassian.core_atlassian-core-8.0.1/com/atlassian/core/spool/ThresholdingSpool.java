/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.Spool;

public interface ThresholdingSpool
extends Spool {
    public void setThresholdBytes(int var1);

    public int getThresholdBytes();
}

