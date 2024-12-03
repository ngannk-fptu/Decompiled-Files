/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  javax.annotation.Nullable
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.spi;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.spi.application.TypeId;
import java.net.URI;
import java.util.Set;
import javax.annotation.Nullable;
import org.osgi.framework.Version;

public interface Manifest {
    public ApplicationId getId();

    public String getName();

    public TypeId getTypeId();

    @Nullable
    public String getVersion();

    public Long getBuildNumber();

    public URI getUrl();

    @Deprecated
    @Nullable
    public URI getIconUrl();

    @Nullable
    public URI getIconUri();

    @Nullable
    public Version getAppLinksVersion();

    public Set<Class<? extends AuthenticationProvider>> getInboundAuthenticationTypes();

    public Set<Class<? extends AuthenticationProvider>> getOutboundAuthenticationTypes();

    public Boolean hasPublicSignup();
}

