/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.mime.DownloadPolicy
 *  com.atlassian.http.mime.DownloadPolicyProvider
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.servlet.download.AttachmentSecurityLevel;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.http.mime.DownloadPolicy;
import com.atlassian.http.mime.DownloadPolicyProvider;
import org.springframework.beans.factory.FactoryBean;

public class SettingsConfiguredDownloadPolicyProvider
implements FactoryBean {
    private SettingsManager settingsManager;

    public SettingsConfiguredDownloadPolicyProvider(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public Object getObject() throws Exception {
        return this::getAttachmentSecurityLevel;
    }

    private DownloadPolicy getAttachmentSecurityLevel() {
        if (!GeneralUtil.isSetupComplete()) {
            return DownloadPolicy.Smart;
        }
        AttachmentSecurityLevel attachmentLevel = this.settingsManager.getGlobalSettings().getAttachmentSecurityLevel();
        return attachmentLevel.getDownloadPolicyLevel();
    }

    public Class getObjectType() {
        return DownloadPolicyProvider.class;
    }

    public boolean isSingleton() {
        return false;
    }
}

