/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.capabilities;

import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import com.atlassian.applinks.internal.rest.model.capabilities.RestApplicationVersion;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkError;
import javax.annotation.Nonnull;

public class RestRemoteApplicationCapabilities
extends BaseRestEntity
implements ReadOnlyRestRepresentation<RemoteApplicationCapabilities> {
    public static final String APPLICATION_VERSION = "applicationVersion";
    public static final String APPLINKS_VERSION = "applinksVersion";
    public static final String CAPABILITIES = "capabilities";
    public static final String ERROR = "error";

    public RestRemoteApplicationCapabilities() {
    }

    public RestRemoteApplicationCapabilities(@Nonnull RemoteApplicationCapabilities capabilities) {
        this.putAs(APPLICATION_VERSION, capabilities.getApplicationVersion(), RestApplicationVersion.class);
        this.putAs(APPLINKS_VERSION, capabilities.getApplinksVersion(), RestApplicationVersion.class);
        this.put(CAPABILITIES, (Object)capabilities.getCapabilities());
        if (capabilities.hasError()) {
            this.put(ERROR, (Object)capabilities.getError().accept(new RestApplinkError.Visitor()));
        }
    }
}

