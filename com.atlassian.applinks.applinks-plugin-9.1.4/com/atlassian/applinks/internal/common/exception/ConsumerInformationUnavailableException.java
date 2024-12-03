/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.EntityUpdateException;
import javax.annotation.Nullable;

public class ConsumerInformationUnavailableException
extends EntityUpdateException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.entity.oauth.consumernotavailable";

    public ConsumerInformationUnavailableException(@Nullable String message) {
        super(message);
    }

    public ConsumerInformationUnavailableException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

