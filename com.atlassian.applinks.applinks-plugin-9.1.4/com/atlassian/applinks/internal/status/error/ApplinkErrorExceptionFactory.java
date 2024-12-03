/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import com.atlassian.applinks.internal.status.error.SimpleApplinkStatusException;
import com.atlassian.applinks.internal.status.remote.ApplinkStatusAccessException;
import com.atlassian.applinks.internal.status.remote.ResponseApplinkStatusException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ApplinkErrorExceptionFactory
implements ApplinkErrorVisitor<ApplinkStatusException> {
    @Override
    @Nullable
    public ApplinkStatusException visit(@Nonnull ApplinkError error) {
        return error instanceof ApplinkStatusException ? (ApplinkStatusException)error : new SimpleApplinkStatusException(error, ApplinkErrorExceptionFactory.asCause(error));
    }

    @Override
    @Nullable
    public ApplinkStatusException visit(@Nonnull AuthorisationUriAwareApplinkError error) {
        return error instanceof ApplinkStatusAccessException ? (ApplinkStatusAccessException)error : new ApplinkStatusAccessException(error, ApplinkErrorExceptionFactory.asCause(error));
    }

    @Override
    @Nullable
    public ApplinkStatusException visit(@Nonnull ResponseApplinkError responseError) {
        return responseError instanceof ResponseApplinkStatusException ? (ResponseApplinkStatusException)responseError : new ResponseApplinkStatusException(responseError, ApplinkErrorExceptionFactory.asCause(responseError));
    }

    private static Throwable asCause(@Nonnull Object error) {
        return error instanceof Throwable ? (Throwable)error : null;
    }
}

