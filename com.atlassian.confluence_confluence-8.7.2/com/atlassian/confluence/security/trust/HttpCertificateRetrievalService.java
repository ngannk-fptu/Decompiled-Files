/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.Application
 *  com.atlassian.security.auth.trustedapps.ApplicationRetriever$RetrievalException
 *  com.atlassian.security.auth.trustedapps.BouncyCastleEncryptionProvider
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.CertificateRetrievalException;
import com.atlassian.confluence.security.trust.CertificateRetrievalService;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.BouncyCastleEncryptionProvider;

public class HttpCertificateRetrievalService
implements CertificateRetrievalService {
    @Override
    public AliasedKey retrieveApplicationCertificate(String location) throws CertificateRetrievalException {
        AliasedKey key = new AliasedKey();
        BouncyCastleEncryptionProvider encryptionProvider = new BouncyCastleEncryptionProvider();
        Application app = null;
        try {
            app = encryptionProvider.getApplicationCertificate(location);
            key.setAlias(app.getID());
            key.setKey(app.getPublicKey());
            return key;
        }
        catch (ApplicationRetriever.RetrievalException e) {
            throw new CertificateRetrievalException("Error retrieving from location: " + location, e);
        }
    }
}

