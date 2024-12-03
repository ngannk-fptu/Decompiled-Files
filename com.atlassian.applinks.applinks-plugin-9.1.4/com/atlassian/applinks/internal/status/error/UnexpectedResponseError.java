/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.AbstractResponseApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.sal.api.net.Response;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UnexpectedResponseError
extends AbstractResponseApplinkError {
    public UnexpectedResponseError(@Nonnull Response response) {
        super(response);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return ApplinkErrorType.UNEXPECTED_RESPONSE;
    }

    @Override
    @Nullable
    public String getDetails() {
        return null;
    }
}

