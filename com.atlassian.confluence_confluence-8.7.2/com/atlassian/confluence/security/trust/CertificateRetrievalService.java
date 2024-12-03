/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.CertificateRetrievalException;

public interface CertificateRetrievalService {
    public AliasedKey retrieveApplicationCertificate(String var1) throws CertificateRetrievalException;
}

