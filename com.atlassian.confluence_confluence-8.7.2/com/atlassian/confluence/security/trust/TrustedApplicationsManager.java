/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.CurrentApplication
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import java.util.Collection;

public interface TrustedApplicationsManager {
    public void saveTrustedApplication(ConfluenceTrustedApplication var1);

    public void deleteTrustedApplication(ConfluenceTrustedApplication var1);

    public ConfluenceTrustedApplication getTrustedApplicationByAlias(String var1);

    public CurrentApplication getCurrentApplication();

    public Collection<ConfluenceTrustedApplication> getAllTrustedApplications();

    public ConfluenceTrustedApplication getTrustedApplication(long var1);

    public ConfluenceTrustedApplication getTrustedApplicationByName(String var1);
}

