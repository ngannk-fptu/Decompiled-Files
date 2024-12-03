/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model.status;

import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.internal.common.rest.model.applink.RestMinimalApplicationLink;
import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkError;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkOAuthStatus;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestApplinkStatus
extends ApplinksRestRepresentation {
    public static final String LINK = "link";
    public static final String WORKING = "working";
    public static final String ERROR = "error";
    public static final String LOCAL_AUTHENTICATION = "localAuthentication";
    public static final String REMOTE_AUTHENTICATION = "remoteAuthentication";
    private RestMinimalApplicationLink link;
    private boolean working;
    private RestApplinkOAuthStatus localAuthentication;
    private RestApplinkOAuthStatus remoteAuthentication;
    private RestApplinkError error;

    public RestApplinkStatus() {
    }

    public RestApplinkStatus(@Nonnull ApplinkStatus status) {
        this(status, null);
    }

    public RestApplinkStatus(@Nonnull ApplinkStatus status, @Nullable URI authorisationCallback) {
        Objects.requireNonNull(status, "status");
        this.link = new RestMinimalApplicationLink(status.getLink());
        this.working = status.isWorking();
        this.localAuthentication = new RestApplinkOAuthStatus(status.getLocalAuthentication());
        this.remoteAuthentication = status.getRemoteAuthentication() == null ? null : new RestApplinkOAuthStatus(status.getRemoteAuthentication());
        this.error = !status.isWorking() ? status.getError().accept(new RestApplinkError.Visitor(authorisationCallback)) : null;
    }
}

