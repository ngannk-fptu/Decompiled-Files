/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package com.atlassian.crowd.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ExceptionUtils {
    @Nonnull
    public static String getMessageWithValidDbCharacters(@Nullable Throwable t) {
        String message = org.apache.commons.lang3.exception.ExceptionUtils.getMessage((Throwable)t);
        return message.replace("\u0000", "");
    }

    private ExceptionUtils() {
    }
}

