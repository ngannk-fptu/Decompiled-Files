/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.core.util.ProgressMeter;

public interface ProgressMeterService {
    public void incrementProgressMeterCount(ProgressMeter var1);

    public void setStatusMessage(String var1, ProgressMeter var2);

    public void setTimeTaken(long var1, ProgressMeter var3);

    public void setAttachmentErrorMessage(ProgressMeter var1);
}

