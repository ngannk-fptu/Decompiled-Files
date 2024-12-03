/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.directory;

public interface FailedSynchronisationManager {
    public void finalizeSynchronisationStatuses();

    public int rescheduleStalledSynchronisations();
}

