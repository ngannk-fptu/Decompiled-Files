/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import java.io.Serializable;

@Deprecated
public interface LegacyPluginSettingsManager {
    public Serializable getPluginSettings(String var1);

    public void updatePluginSettings(String var1, Serializable var2);
}

