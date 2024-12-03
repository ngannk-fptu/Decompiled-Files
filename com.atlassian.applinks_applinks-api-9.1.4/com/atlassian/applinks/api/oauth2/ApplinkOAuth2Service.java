/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.oauth2;

import com.atlassian.applinks.api.ApplicationLink;

public interface ApplinkOAuth2Service {
    public Iterable<ApplicationLink> getApplicationLinksForOAuth2Clients();

    public Iterable<ApplicationLink> getApplicationLinksForOAuth2Provider();
}

