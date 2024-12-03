/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.CoreFeaturesManager;

public class DefaultCoreFeaturesManager
implements CoreFeaturesManager {
    @Override
    public boolean isOnDemand() {
        return false;
    }
}

