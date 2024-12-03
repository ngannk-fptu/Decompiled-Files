/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.common.util;

import java.util.Objects;
import javax.annotation.Nullable;

public final class MaskingUtil {
    public static final String SANITIZATION_MASK = "*****";

    private MaskingUtil() {
    }

    public static String sanitize(@Nullable Object value) {
        return Objects.toString(value, "").isEmpty() ? "" : SANITIZATION_MASK;
    }
}

