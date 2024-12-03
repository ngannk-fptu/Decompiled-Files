/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplication;

@Deprecated
public interface TrustedApplicationsManager {
    public TrustedApplication getTrustedApplication(String var1);

    public CurrentApplication getCurrentApplication();
}

