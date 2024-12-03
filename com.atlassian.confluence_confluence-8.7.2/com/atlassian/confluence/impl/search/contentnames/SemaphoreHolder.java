/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.contentnames;

import com.atlassian.confluence.setup.settings.SettingsManager;
import java.util.concurrent.Semaphore;

public class SemaphoreHolder {
    private final SettingsManager settingsManager;
    private volatile Semaphore semaphore;

    public SemaphoreHolder(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public Semaphore getSemaphore() {
        if (this.semaphore == null) {
            this.refreshSemaphore();
        }
        return this.semaphore;
    }

    public void refreshSemaphore() {
        this.semaphore = new Semaphore(this.settingsManager.getGlobalSettings().getMaxSimultaneousQuickNavRequests(), true);
    }
}

