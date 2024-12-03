/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.impl.ConfigImplUtil;
import java.util.List;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static String quoteString(String s) {
        return ConfigImplUtil.renderJsonString(s);
    }

    public static String joinPath(String ... elements) {
        return ConfigImplUtil.joinPath(elements);
    }

    public static String joinPath(List<String> elements) {
        return ConfigImplUtil.joinPath(elements);
    }

    public static List<String> splitPath(String path) {
        return ConfigImplUtil.splitPath(path);
    }
}

