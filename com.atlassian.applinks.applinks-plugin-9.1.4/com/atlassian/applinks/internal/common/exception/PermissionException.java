/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.NoAccessException;
import javax.annotation.Nullable;

public class PermissionException
extends NoAccessException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.access.nopermission";

    public PermissionException(@Nullable String message) {
        super(message);
    }

    public PermissionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

