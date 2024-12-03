/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.core.PluginsEnablementState;

public interface SafeModeAccessor {
    public boolean isSafeMode();

    public PluginsEnablementState getCurrentConfiguration();
}

