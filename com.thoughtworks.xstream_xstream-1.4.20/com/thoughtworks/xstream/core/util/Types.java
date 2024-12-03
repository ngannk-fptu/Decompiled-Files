/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Types {
    private static final Pattern lambdaPattern = Pattern.compile(".*\\$\\$Lambda\\$[0-9]+/.*");

    public static final boolean isLambdaType(Class<?> type) {
        return type != null && type.isSynthetic() && lambdaPattern.matcher(type.getSimpleName()).matches();
    }
}

