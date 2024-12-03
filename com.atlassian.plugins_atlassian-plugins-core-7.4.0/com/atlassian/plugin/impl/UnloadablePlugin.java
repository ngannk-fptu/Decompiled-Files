/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.impl;

import com.atlassian.plugin.impl.StaticPlugin;

public class UnloadablePlugin
extends StaticPlugin {
    private static final String UNKNOWN_KEY_PREFIX = "Unknown-";
    private String errorText;
    private boolean uninstallable = true;
    private boolean deletable = true;
    private boolean dynamic = false;

    public UnloadablePlugin() {
        this((String)null);
    }

    public UnloadablePlugin(String text) {
        this.errorText = text;
        this.setKey(UNKNOWN_KEY_PREFIX + System.identityHashCode(this));
    }

    @Override
    public boolean isUninstallable() {
        return this.uninstallable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public boolean isDeleteable() {
        return this.deletable;
    }

    public void setUninstallable(boolean uninstallable) {
        this.uninstallable = uninstallable;
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    public String getErrorText() {
        return this.errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public boolean isDynamicallyLoaded() {
        return this.dynamic;
    }

    public void setDynamicallyLoaded(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public void close() {
    }

    @Override
    protected void uninstallInternal() {
        if (!this.uninstallable) {
            super.uninstallInternal();
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.errorText;
    }
}

