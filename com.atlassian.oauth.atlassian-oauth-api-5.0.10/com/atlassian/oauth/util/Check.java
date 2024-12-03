/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.oauth.util;

import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class Check {
    @Deprecated
    public static <T> T notNull(T reference, Object errorMessage) {
        return Objects.requireNonNull(reference, String.valueOf(errorMessage));
    }

    public static String notBlank(String str, Object errorMessage) {
        Validate.notBlank((CharSequence)str, (String)String.valueOf(errorMessage), (Object[])new Object[0]);
        return str;
    }
}

