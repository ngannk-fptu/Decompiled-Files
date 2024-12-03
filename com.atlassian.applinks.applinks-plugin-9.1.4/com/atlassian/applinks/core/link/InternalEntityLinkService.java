/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 */
package com.atlassian.applinks.core.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;

public interface InternalEntityLinkService
extends MutatingEntityLinkService {
    public void migrateEntityLinks(ApplicationLink var1, ApplicationLink var2);
}

