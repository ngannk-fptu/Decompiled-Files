/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.ApplicationId;
import java.net.URI;

public interface HostApplication {
    public ApplicationId getId();

    public URI getBaseUrl();
}

