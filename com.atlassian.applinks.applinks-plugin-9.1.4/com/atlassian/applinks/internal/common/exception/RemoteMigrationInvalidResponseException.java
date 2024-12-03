/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.ConsumerInformationUnavailableException;
import javax.annotation.Nullable;

public class RemoteMigrationInvalidResponseException
extends ConsumerInformationUnavailableException {
    public static final String DEFAULT_MESSAGE = "applinks.service.error.remote.migration.response.invalid";

    public RemoteMigrationInvalidResponseException(@Nullable String message) {
        super(message);
    }
}

