/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import javax.annotation.Nullable;

public class SystemFeatureException
extends InvalidArgumentException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.feature.system";

    public SystemFeatureException(@Nullable String message) {
        super(message);
    }

    public SystemFeatureException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

