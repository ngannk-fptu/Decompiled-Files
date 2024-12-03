/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.util.StringUtil;

public enum SelectorMode {
    SELECT,
    SELECT_NOW,
    SELECT_WITH_FIX;

    public static final String SELECT_STRING = "select";
    public static final String SELECT_WITH_FIX_STRING = "selectwithfix";
    public static final String SELECT_NOW_STRING = "selectnow";

    public static SelectorMode getConfiguredValue() {
        return SelectorMode.fromString(SelectorMode.getConfiguredString());
    }

    public static String getConfiguredString() {
        return System.getProperty("hazelcast.io.selectorMode", SELECT_STRING).trim().toLowerCase(StringUtil.LOCALE_INTERNAL);
    }

    public static SelectorMode fromString(String value) {
        if (value.equals(SELECT_STRING)) {
            return SELECT;
        }
        if (value.equals(SELECT_WITH_FIX_STRING)) {
            return SELECT_WITH_FIX;
        }
        if (value.equals(SELECT_NOW_STRING) || value.startsWith("selectnow,")) {
            return SELECT_NOW;
        }
        throw new IllegalArgumentException(String.format("Unrecognized selectorMode [%s]", value));
    }
}

