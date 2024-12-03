/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon;

import org.apache.commons.daemon.DaemonController;

public interface DaemonContext {
    public DaemonController getController();

    public String[] getArguments();
}

