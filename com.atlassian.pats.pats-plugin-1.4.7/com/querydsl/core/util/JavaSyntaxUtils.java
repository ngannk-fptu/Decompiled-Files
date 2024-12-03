/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import javax.lang.model.SourceVersion;

@Deprecated
public final class JavaSyntaxUtils {
    private JavaSyntaxUtils() {
    }

    public static boolean isReserved(String str) {
        return SourceVersion.isKeyword(str);
    }
}

