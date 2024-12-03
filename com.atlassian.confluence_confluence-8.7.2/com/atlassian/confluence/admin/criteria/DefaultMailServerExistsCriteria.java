/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.criteria.MailServerExistsCriteria;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;

public class DefaultMailServerExistsCriteria
implements AdminConfigurationCriteria,
MailServerExistsCriteria {
    private final SettingsManager settingsManager;
    private final MailServerManager mailServerManager;

    public DefaultMailServerExistsCriteria(SettingsManager settingsManager, MailServerManager mailServerManager) {
        this.settingsManager = settingsManager;
        this.mailServerManager = mailServerManager;
    }

    @Override
    public boolean isMet() {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }

    @Override
    public boolean getIgnored() {
        return this.settingsManager.getGlobalSettings().isEmailAdminMessageOff();
    }

    @Override
    public void setIgnored(boolean ignored) {
        Settings settings = this.settingsManager.getGlobalSettings();
        settings.setEmailAdminMessageOff(ignored);
        this.settingsManager.updateGlobalSettings(settings);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        return this.isMet() ? this.getHumanFriendlyMailServerName() : "None";
    }

    private String getHumanFriendlyMailServerName() {
        StringBuilder result = new StringBuilder();
        SMTPMailServer mailServer = this.mailServerManager.getDefaultSMTPMailServer();
        result.append(mailServer.getName());
        result.append("\n");
        if (mailServer.isSessionServer()) {
            result.append(mailServer.getJndiLocation());
        } else {
            result.append("(");
            result.append(mailServer.getHostname());
            result.append(":");
            result.append(mailServer.getPort());
            result.append(")");
        }
        return result.toString();
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }
}

