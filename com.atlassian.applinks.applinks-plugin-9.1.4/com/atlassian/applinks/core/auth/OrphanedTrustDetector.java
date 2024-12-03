/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import java.util.List;

public interface OrphanedTrustDetector {
    public List<OrphanedTrustCertificate> findOrphanedTrustCertificates();

    public void deleteTrustCertificate(String var1, OrphanedTrustCertificate.Type var2);

    public void addOrphanedTrustToApplicationLink(String var1, OrphanedTrustCertificate.Type var2, ApplicationId var3);

    public boolean canHandleCertificateType(OrphanedTrustCertificate.Type var1);
}

