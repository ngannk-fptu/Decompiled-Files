/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.UIDGenerator;
import com.atlassian.security.auth.trustedapps.URLApplicationRetriever;

public abstract class BaseEncryptionProvider
implements EncryptionProvider {
    @Override
    public Application getApplicationCertificate(String baseUrl) throws ApplicationRetriever.RetrievalException {
        return new URLApplicationRetriever(baseUrl, this).getApplication();
    }

    @Override
    public String generateUID() {
        return UIDGenerator.generateUID();
    }
}

