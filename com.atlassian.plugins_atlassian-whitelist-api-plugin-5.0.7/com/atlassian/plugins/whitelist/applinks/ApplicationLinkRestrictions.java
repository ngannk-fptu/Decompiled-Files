/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.whitelist.applinks;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness;

@PublicApi
public interface ApplicationLinkRestrictions {
    public void setRestrictiveness(ApplicationLinkRestrictiveness var1);

    public ApplicationLinkRestrictiveness getRestrictiveness();
}

