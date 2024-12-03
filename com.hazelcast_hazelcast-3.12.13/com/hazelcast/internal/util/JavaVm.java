/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import java.util.Properties;

public enum JavaVm {
    UNKNOWN,
    HOTSPOT,
    OPENJ9;

    public static final JavaVm CURRENT_VM;

    private static JavaVm detectCurrentVM() {
        Properties properties = System.getProperties();
        return JavaVm.parse(properties);
    }

    static JavaVm parse(Properties properties) {
        String vmName = properties.getProperty("java.vm.name");
        if (vmName == null) {
            return UNKNOWN;
        }
        if (vmName.contains("HotSpot")) {
            return HOTSPOT;
        }
        if (vmName.contains("OpenJ9")) {
            return OPENJ9;
        }
        String prop = properties.getProperty("sun.management.compiler");
        if (prop == null) {
            return UNKNOWN;
        }
        if (prop.contains("HotSpot")) {
            return HOTSPOT;
        }
        return UNKNOWN;
    }

    static {
        CURRENT_VM = JavaVm.detectCurrentVM();
    }
}

