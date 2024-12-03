/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import org.springframework.lang.Nullable;

public final class SpringVersion {
    private SpringVersion() {
    }

    @Nullable
    public static String getVersion() {
        Package pkg = SpringVersion.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : null;
    }
}

