/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.TypeNotInstalledException;

public interface ApplicationLinkService {
    public ApplicationLink getApplicationLink(ApplicationId var1) throws TypeNotInstalledException;

    public Iterable<ApplicationLink> getApplicationLinks();

    public Iterable<ApplicationLink> getApplicationLinks(Class<? extends ApplicationType> var1);

    public ApplicationLink getPrimaryApplicationLink(Class<? extends ApplicationType> var1);
}

