/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.UpdateManagerEvent;

public interface UpdateManagerListener {
    public void managerStarted(UpdateManagerEvent var1);

    public void managerSuspended(UpdateManagerEvent var1);

    public void managerResumed(UpdateManagerEvent var1);

    public void managerStopped(UpdateManagerEvent var1);

    public void updateStarted(UpdateManagerEvent var1);

    public void updateCompleted(UpdateManagerEvent var1);

    public void updateFailed(UpdateManagerEvent var1);
}

