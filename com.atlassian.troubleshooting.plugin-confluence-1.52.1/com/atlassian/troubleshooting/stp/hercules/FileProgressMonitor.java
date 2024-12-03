/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.hercules;

public interface FileProgressMonitor {
    public void setTotalSize(long var1);

    public void setProgress(long var1);

    public boolean isCancelled();
}

