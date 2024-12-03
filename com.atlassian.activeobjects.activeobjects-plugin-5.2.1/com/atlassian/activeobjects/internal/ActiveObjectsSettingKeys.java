/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.Prefix;

public final class ActiveObjectsSettingKeys {
    public static final String MODEL_VERSION = "#";
    public static final String DATA_SOURCE_TYPE = "DST";

    public String getDataSourceTypeKey(Prefix prefix) {
        return prefix.prepend(DATA_SOURCE_TYPE);
    }

    public String getModelVersionKey(Prefix prefix) {
        return prefix.prepend(MODEL_VERSION);
    }
}

