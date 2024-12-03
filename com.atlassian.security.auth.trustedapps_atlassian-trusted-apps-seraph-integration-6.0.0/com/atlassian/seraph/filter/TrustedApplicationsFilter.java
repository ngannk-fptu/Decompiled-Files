/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.security.auth.trustedapps.UserResolver
 */
package com.atlassian.seraph.filter;

import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.security.auth.trustedapps.seraph.filter.SeraphTrustedApplicationsFilter;

@Deprecated
public class TrustedApplicationsFilter
extends SeraphTrustedApplicationsFilter {
    public TrustedApplicationsFilter(TrustedApplicationsManager appManager, UserResolver resolver) {
        super(appManager, resolver);
    }
}

