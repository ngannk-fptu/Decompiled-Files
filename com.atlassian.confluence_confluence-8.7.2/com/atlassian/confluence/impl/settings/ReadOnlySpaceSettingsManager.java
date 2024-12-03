/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;

public class ReadOnlySpaceSettingsManager
implements SpaceSettingsManager {
    private final SpaceSettingsManager delegate;

    public ReadOnlySpaceSettingsManager(SpaceSettingsManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public SpaceSettings getSpaceSettings(String spaceKey) {
        return this.delegate.getSpaceSettings(spaceKey);
    }

    @Override
    public void updateSpaceSettings(SpaceSettings spaceSettings) {
        throw new UnsupportedOperationException();
    }
}

