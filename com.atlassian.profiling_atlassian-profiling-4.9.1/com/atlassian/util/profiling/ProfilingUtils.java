/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.profiling;

@Deprecated
public class ProfilingUtils {
    public static String getJustClassName(Class clazz) {
        return ProfilingUtils.getJustClassName(clazz.getName());
    }

    public static String getJustClassName(String name) {
        if (name.indexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        return name;
    }
}

