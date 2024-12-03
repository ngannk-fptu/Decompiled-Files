/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.AbstractRestoreProgressMonitor;
import com.atlassian.activeobjects.spi.RestoreProgressMonitor;

public final class NullRestoreProgressMonitor
extends AbstractRestoreProgressMonitor {
    public static final RestoreProgressMonitor INSTANCE = new NullRestoreProgressMonitor();

    private NullRestoreProgressMonitor() {
    }
}

