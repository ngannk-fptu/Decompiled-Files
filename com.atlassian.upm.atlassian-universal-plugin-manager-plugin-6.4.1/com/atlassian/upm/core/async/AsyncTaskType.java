/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.api.util.Option;

public enum AsyncTaskType {
    CANCELLABLE,
    DISABLE_ALL_INCOMPATIBLE,
    EMBEDDED_HOST_LICENSE_CHANGE,
    INSTALL,
    PLUGIN_SCAN_DIRECTORY_REFRESH,
    UNINSTALL,
    UPDATE_ALL,
    UPDATE_BUNDLED;


    public static Option<AsyncTaskType> getType(String name) {
        for (AsyncTaskType type : AsyncTaskType.values()) {
            if (!type.name().equalsIgnoreCase(name)) continue;
            return Option.some(type);
        }
        return Option.none(AsyncTaskType.class);
    }
}

