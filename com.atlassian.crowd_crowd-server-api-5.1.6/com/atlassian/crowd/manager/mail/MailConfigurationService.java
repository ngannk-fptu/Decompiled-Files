/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.manager.mail.MailConfiguration;

public interface MailConfigurationService {
    public MailConfiguration getMailConfiguration();

    public void saveConfiguration(MailConfiguration var1);

    public boolean isConfigured();
}

