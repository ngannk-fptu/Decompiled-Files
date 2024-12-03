/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.NoSuchEntityException;
import javax.annotation.Nullable;

public class NoSuchApplinkException
extends NoSuchEntityException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.nosuchentity.applink";

    public NoSuchApplinkException(@Nullable String message) {
        super(message);
    }

    public NoSuchApplinkException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

