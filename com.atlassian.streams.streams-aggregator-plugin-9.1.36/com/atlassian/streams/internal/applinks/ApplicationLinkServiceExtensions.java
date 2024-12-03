/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.streams.internal.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import java.net.URI;

public interface ApplicationLinkServiceExtensions {
    public boolean isAuthorised(ApplicationLink var1);

    public URI getUserAdminUri(ApplicationLink var1);

    public URI getAuthCallbackUri(ApplicationLink var1);
}

