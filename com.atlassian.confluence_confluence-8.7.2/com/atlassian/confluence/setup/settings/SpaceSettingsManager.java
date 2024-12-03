/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.SpaceSettings;

public interface SpaceSettingsManager {
    public SpaceSettings getSpaceSettings(String var1);

    public void updateSpaceSettings(SpaceSettings var1);
}

