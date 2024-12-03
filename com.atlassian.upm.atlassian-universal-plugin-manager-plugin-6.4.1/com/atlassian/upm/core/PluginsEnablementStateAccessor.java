/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginsEnablementState;

public interface PluginsEnablementStateAccessor {
    public boolean hasSavedConfiguration() throws PluginsEnablementStateStoreException;

    public Option<PluginsEnablementState> getSavedConfiguration() throws PluginsEnablementStateStoreException;

    public static class PluginsEnablementStateStoreException
    extends RuntimeException {
        public PluginsEnablementStateStoreException(String message, Throwable cause) {
            super(message, cause);
        }

        public PluginsEnablementStateStoreException(String message) {
            super(message);
        }
    }
}

