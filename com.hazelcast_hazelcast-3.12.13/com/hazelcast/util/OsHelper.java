/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.StringUtil;

public final class OsHelper {
    public static final String OS = StringUtil.lowerCaseInternal(System.getProperty("os.name"));

    private OsHelper() {
    }

    public static boolean isUnixFamily() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }
}

