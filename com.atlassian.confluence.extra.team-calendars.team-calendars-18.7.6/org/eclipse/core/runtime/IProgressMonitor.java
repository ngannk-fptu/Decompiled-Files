/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

public interface IProgressMonitor {
    public static final int UNKNOWN = -1;

    public void beginTask(String var1, int var2);

    public void done();

    public void internalWorked(double var1);

    public boolean isCanceled();

    public void setCanceled(boolean var1);

    public void setTaskName(String var1);

    public void subTask(String var1);

    public void worked(int var1);
}

