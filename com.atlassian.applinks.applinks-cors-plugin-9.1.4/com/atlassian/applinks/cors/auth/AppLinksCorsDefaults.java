/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults
 */
package com.atlassian.applinks.cors.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.cors.auth.CorsService;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class AppLinksCorsDefaults
implements CorsDefaults {
    private final CorsService corsService;

    public AppLinksCorsDefaults(CorsService corsService) {
        this.corsService = corsService;
    }

    public boolean allowsOrigin(String uri) {
        Collection<ApplicationLink> applicationLinksByOrigin = this.corsService.getApplicationLinksByOrigin(uri);
        return applicationLinksByOrigin != null && !applicationLinksByOrigin.isEmpty();
    }

    public boolean allowsCredentials(String uri) throws IllegalArgumentException {
        Collection<ApplicationLink> links = this.corsService.getRequiredApplicationLinksByOrigin(uri);
        for (ApplicationLink link : links) {
            if (this.corsService.allowsCredentials(link)) continue;
            return false;
        }
        return true;
    }

    public Set<String> getAllowedRequestHeaders(String uri) throws IllegalArgumentException {
        return this.allowsCredentials(uri) ? Collections.singleton("Authorization") : Collections.emptySet();
    }

    public Set<String> getAllowedResponseHeaders(String uri) throws IllegalArgumentException {
        this.corsService.getRequiredApplicationLinksByOrigin(uri);
        return Collections.emptySet();
    }
}

