/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.applinks.cors.auth;

import com.atlassian.applinks.api.ApplicationLink;
import java.net.URI;
import java.util.Collection;

public interface CorsService {
    public boolean allowsCredentials(ApplicationLink var1);

    public void disableCredentials(ApplicationLink var1);

    public void enableCredentials(ApplicationLink var1);

    public Collection<ApplicationLink> getApplicationLinksByOrigin(String var1);

    public Collection<ApplicationLink> getApplicationLinksByUri(URI var1);

    public Collection<ApplicationLink> getRequiredApplicationLinksByOrigin(String var1) throws IllegalArgumentException;
}

