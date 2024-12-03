/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;

public abstract class ProgressMonitorWrapper
implements IProgressMonitor,
IProgressMonitorWithBlocking {
    private IProgressMonitor progressMonitor;

    protected ProgressMonitorWrapper(IProgressMonitor monitor) {
        Assert.isNotNull(monitor);
        this.progressMonitor = monitor;
    }

    public void beginTask(String name, int totalWork) {
        this.progressMonitor.beginTask(name, totalWork);
    }

    public void clearBlocked() {
        if (this.progressMonitor instanceof IProgressMonitorWithBlocking) {
            ((IProgressMonitorWithBlocking)this.progressMonitor).clearBlocked();
        }
    }

    public void done() {
        this.progressMonitor.done();
    }

    public IProgressMonitor getWrappedProgressMonitor() {
        return this.progressMonitor;
    }

    public void internalWorked(double work) {
        this.progressMonitor.internalWorked(work);
    }

    public boolean isCanceled() {
        return this.progressMonitor.isCanceled();
    }

    public void setBlocked(IStatus reason) {
        if (this.progressMonitor instanceof IProgressMonitorWithBlocking) {
            ((IProgressMonitorWithBlocking)this.progressMonitor).setBlocked(reason);
        }
    }

    public void setCanceled(boolean b) {
        this.progressMonitor.setCanceled(b);
    }

    public void setTaskName(String name) {
        this.progressMonitor.setTaskName(name);
    }

    public void subTask(String name) {
        this.progressMonitor.subTask(name);
    }

    public void worked(int work) {
        this.progressMonitor.worked(work);
    }
}

