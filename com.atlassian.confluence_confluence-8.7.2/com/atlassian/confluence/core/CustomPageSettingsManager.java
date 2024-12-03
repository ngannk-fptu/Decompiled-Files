/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.CustomPageSettings;

public interface CustomPageSettingsManager {
    public CustomPageSettings retrieveSettings(String var1);

    public CustomPageSettings retrieveSettings();

    public void saveSettings(String var1, CustomPageSettings var2);

    public void saveSettings(CustomPageSettings var1);
}

