/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.CurrentApplication
 *  com.atlassian.security.auth.trustedapps.EncryptionProvider
 *  com.atlassian.security.auth.trustedapps.TrustedApplication
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security.trust.seraph;

import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import com.atlassian.confluence.security.trust.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import org.springframework.transaction.annotation.Transactional;

public class SeraphTrustedApplicationIntegration
implements com.atlassian.security.auth.trustedapps.TrustedApplicationsManager {
    private TrustedApplicationsManager trustedApplicationsManager;
    private EncryptionProvider encryptionProvider;

    @Transactional(readOnly=true)
    public TrustedApplication getTrustedApplication(String alias) {
        CurrentApplication currentApplication = this.getCurrentApplication();
        if (currentApplication != null && currentApplication.getID().equals(alias) && currentApplication instanceof TrustedApplication) {
            return (TrustedApplication)currentApplication;
        }
        ConfluenceTrustedApplication trustedApplication = this.trustedApplicationsManager.getTrustedApplicationByAlias(alias);
        if (null == trustedApplication) {
            return null;
        }
        return trustedApplication.toDefaultTrustedApplication(this.encryptionProvider);
    }

    @Transactional(readOnly=true)
    public CurrentApplication getCurrentApplication() {
        return this.trustedApplicationsManager.getCurrentApplication();
    }

    public void setTrustedApplicationsManager(TrustedApplicationsManager trustedApplicationsManager) {
        this.trustedApplicationsManager = trustedApplicationsManager;
    }

    public void setEncryptionProvider(EncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }
}

