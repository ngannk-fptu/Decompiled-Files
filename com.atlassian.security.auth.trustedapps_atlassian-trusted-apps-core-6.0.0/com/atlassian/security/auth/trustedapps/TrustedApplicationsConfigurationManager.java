/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplication;

@Deprecated
public interface TrustedApplicationsConfigurationManager {
    public Application getApplicationCertificate(String var1) throws ApplicationRetriever.RetrievalException;

    public TrustedApplication addTrustedApplication(Application var1, RequestConditions var2);

    public boolean deleteApplication(String var1);

    public Iterable<TrustedApplication> getTrustedApplications();
}

