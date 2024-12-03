/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.auth.OrphanedTrustDetector;
import java.util.ArrayList;
import java.util.List;

public class DelegatingOrphanedTrustDetector
implements OrphanedTrustDetector {
    private List<InternalOrphanedTrustDetector> orphanedTrustDetectors = new ArrayList<InternalOrphanedTrustDetector>();

    public void setOrphanedTrustDetectors(List<InternalOrphanedTrustDetector> orphanedTrustDetectors) {
        this.orphanedTrustDetectors = orphanedTrustDetectors;
    }

    @Override
    public List<OrphanedTrustCertificate> findOrphanedTrustCertificates() {
        ArrayList<OrphanedTrustCertificate> certificates = new ArrayList<OrphanedTrustCertificate>();
        for (OrphanedTrustDetector orphanedTrustDetector : this.orphanedTrustDetectors) {
            certificates.addAll(orphanedTrustDetector.findOrphanedTrustCertificates());
        }
        return certificates;
    }

    @Override
    public void deleteTrustCertificate(String id, OrphanedTrustCertificate.Type type) {
        for (OrphanedTrustDetector orphanedTrustDetector : this.orphanedTrustDetectors) {
            if (!orphanedTrustDetector.canHandleCertificateType(type)) continue;
            orphanedTrustDetector.deleteTrustCertificate(id, type);
        }
    }

    @Override
    public boolean canHandleCertificateType(OrphanedTrustCertificate.Type type) {
        for (OrphanedTrustDetector orphanedTrustDetector : this.orphanedTrustDetectors) {
            if (!orphanedTrustDetector.canHandleCertificateType(type)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void addOrphanedTrustToApplicationLink(String id, OrphanedTrustCertificate.Type type, ApplicationId applicationId) {
        for (OrphanedTrustDetector orphanedTrustDetector : this.orphanedTrustDetectors) {
            if (!orphanedTrustDetector.canHandleCertificateType(type)) continue;
            orphanedTrustDetector.addOrphanedTrustToApplicationLink(id, type, applicationId);
        }
    }
}

