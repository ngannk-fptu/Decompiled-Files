/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import javax.annotation.Nullable;

public class InvalidFeatureKeyException
extends InvalidArgumentException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.feature.invalidkey";

    public InvalidFeatureKeyException(@Nullable String message) {
        super(message);
    }

    public InvalidFeatureKeyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

