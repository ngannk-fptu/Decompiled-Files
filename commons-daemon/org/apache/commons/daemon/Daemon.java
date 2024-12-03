/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public interface Daemon {
    public void init(DaemonContext var1) throws DaemonInitException, Exception;

    public void start() throws Exception;

    public void stop() throws Exception;

    public void destroy();
}

