/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.migration;

import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import com.atlassian.applinks.internal.rest.model.migration.RestAuthenticationConfig;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class RestAuthenticationStatus
extends ApplinksRestRepresentation {
    @VisibleForTesting
    public static final String OUTGOING = "outgoing";
    @VisibleForTesting
    public static final String INCOMING = "incoming";
    @VisibleForTesting
    public static final String CAPABILITIES = "capabilities";
    private RestAuthenticationConfig outgoing;
    private RestAuthenticationConfig incoming;
    private Set<ApplinksCapabilities> capabilities;

    public RestAuthenticationStatus() {
    }

    public RestAuthenticationStatus(@Nonnull AuthenticationStatus authenticationStatus, @Nonnull RemoteApplicationCapabilities remoteCapabilities) {
        Objects.requireNonNull(authenticationStatus, "authenticationConfigs");
        Objects.requireNonNull(remoteCapabilities, "remoteCapabilities");
        this.outgoing = new RestAuthenticationConfig(authenticationStatus.outgoing());
        this.incoming = new RestAuthenticationConfig(authenticationStatus.incoming());
        this.capabilities = remoteCapabilities.getCapabilities();
    }
}

