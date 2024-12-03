/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.PluginsEnablementStateAccessor;

public interface PluginsEnablementStateStore
extends PluginsEnablementStateAccessor {
    public void saveConfiguration(PluginsEnablementState var1) throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException;

    public Option<PluginsEnablementState> removeSavedConfiguration() throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException;
}

