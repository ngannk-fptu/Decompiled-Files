/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.applinks;

public enum ApplicationLinkRestrictiveness {
    ALLOW_AUTHENTICATED,
    ALLOW_ANONYMOUS,
    DENY;


    public boolean createApplinkRules() {
        return this != DENY;
    }

    public boolean allowAnonymous() {
        return this == ALLOW_ANONYMOUS;
    }
}

