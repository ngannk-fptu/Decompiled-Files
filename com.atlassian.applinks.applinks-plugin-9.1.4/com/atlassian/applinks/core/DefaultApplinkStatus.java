/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorCategory;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.internal.status.error.SimpleApplinkError;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public final class DefaultApplinkStatus
implements ApplinkStatus {
    private final ApplicationLink link;
    private final ApplinkError errorDetails;
    private final ApplinkOAuthStatus localAuthentication;
    private final ApplinkOAuthStatus remoteAuthentication;

    private DefaultApplinkStatus(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nullable ApplinkOAuthStatus remoteAuthentication, @Nullable ApplinkError error) {
        this.link = Objects.requireNonNull(link, "link");
        this.errorDetails = error;
        this.localAuthentication = Objects.requireNonNull(localAuthentication, "localAuthentication");
        this.remoteAuthentication = remoteAuthentication;
    }

    @Nonnull
    public static DefaultApplinkStatus working(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nonnull ApplinkOAuthStatus remoteAuthentication) {
        return new DefaultApplinkStatus(link, localAuthentication, remoteAuthentication, null);
    }

    @Nonnull
    public static DefaultApplinkStatus disabled(@Nonnull ApplicationLink link, @Nonnull ApplinkError error) {
        Objects.requireNonNull(error, "error");
        Validate.isTrue((boolean)ApplinkErrorCategory.DISABLED.equals((Object)error.getType().getCategory()), (String)"error", (Object[])new Object[0]);
        return new DefaultApplinkStatus(link, ApplinkOAuthStatus.OFF, ApplinkOAuthStatus.OFF, error);
    }

    @Nonnull
    public static DefaultApplinkStatus configError(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nonnull ApplinkOAuthStatus remoteAuthentication, @Nonnull ApplinkError error) {
        Objects.requireNonNull(error, "error");
        Validate.isTrue((boolean)ApplinkErrorCategory.CONFIG_ERROR.equals((Object)error.getType().getCategory()));
        return new DefaultApplinkStatus(link, localAuthentication, remoteAuthentication, error);
    }

    @Nonnull
    public static DefaultApplinkStatus error(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nullable ApplinkOAuthStatus remoteAuthentication, @Nonnull ApplinkError error) {
        Objects.requireNonNull(error, "error");
        return new DefaultApplinkStatus(link, localAuthentication, remoteAuthentication, error);
    }

    @Nonnull
    public static DefaultApplinkStatus error(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nonnull ApplinkError error) {
        Objects.requireNonNull(error, "error");
        return new DefaultApplinkStatus(link, localAuthentication, null, error);
    }

    @Nonnull
    public static DefaultApplinkStatus unknown(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localAuthentication, @Nonnull ApplinkError error) {
        Objects.requireNonNull(error, "error");
        Validate.isTrue((boolean)ApplinkErrorCategory.UNKNOWN.equals((Object)error.getType().getCategory()));
        return new DefaultApplinkStatus(link, localAuthentication, null, error);
    }

    @Nonnull
    public static DefaultApplinkStatus unknown(@Nonnull ApplicationLink link, @Nullable ApplinkOAuthStatus localAuthentication, @Nonnull Exception exception) {
        ApplinkOAuthStatus localStatus = localAuthentication == null ? ApplinkOAuthStatus.OFF : localAuthentication;
        return DefaultApplinkStatus.unknown(link, localStatus, DefaultApplinkStatus.createUnknownError(exception));
    }

    @Override
    @Nonnull
    public ApplicationLink getLink() {
        return this.link;
    }

    @Override
    @Nullable
    public ApplinkError getError() {
        return this.errorDetails;
    }

    @Override
    public boolean isWorking() {
        return this.errorDetails == null;
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus getLocalAuthentication() {
        return this.localAuthentication;
    }

    @Override
    @Nullable
    public ApplinkOAuthStatus getRemoteAuthentication() {
        return this.remoteAuthentication;
    }

    private static ApplinkError createUnknownError(Exception error) {
        return new SimpleApplinkError(ApplinkErrorType.UNKNOWN, ApplinkErrors.toDetails(error));
    }
}

