/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.confluence.plugins.synchrony.api;

import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import io.atlassian.util.concurrent.Promise;
import java.util.Map;

public interface SynchronyProcessManager {
    public boolean isSynchronyClusterManuallyManaged();

    public boolean isSynchronyOff();

    public void setSynchronyOff(boolean var1);

    public Promise<Boolean> startup();

    public boolean stop();

    public String getSynchronyProperty(SynchronyEnv var1);

    public Promise<Boolean> restart();

    public Map<String, String> getConfiguration();

    default public boolean isSynchronyStartingUp() {
        throw new RuntimeException("This implementation of SynchronyProcessManager does not implementisSynchronyStartingUp().");
    }

    public static enum ExternalProcessState {
        BeforeStart,
        Started,
        Terminating,
        Terminated;

    }
}

