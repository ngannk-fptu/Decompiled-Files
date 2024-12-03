/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 */
package com.atlassian.applinks.spi.application;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.application.IdentifiableType;
import java.net.URI;

public interface StaticUrlApplicationType
extends ApplicationType,
IdentifiableType {
    public URI getStaticUrl();
}

