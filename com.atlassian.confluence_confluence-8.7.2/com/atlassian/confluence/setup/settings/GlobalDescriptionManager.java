/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.GlobalDescription;

public interface GlobalDescriptionManager {
    public GlobalDescription getGlobalDescription();

    public void updateGlobalDescription(GlobalDescription var1);
}

