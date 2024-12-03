/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface IProgressMonitorWithBlocking
extends IProgressMonitor {
    public void setBlocked(IStatus var1);

    public void clearBlocked();
}

