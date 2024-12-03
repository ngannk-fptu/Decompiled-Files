/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationManager
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mail;

import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.AbstractAtlassianMailService;
import com.atlassian.upm.mail.UpmEmail;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BambooMailService
extends AbstractAtlassianMailService {
    private static final Logger logger = LoggerFactory.getLogger(BambooMailService.class);
    private final AdministrationConfigurationManager administrationConfigurationManager;

    public BambooMailService(AdministrationConfigurationManager administrationConfigurationManager) {
        this.administrationConfigurationManager = Objects.requireNonNull(administrationConfigurationManager, "administrationConfigurationManager");
    }

    @Override
    public void sendMail(UpmEmail upmEmail) {
        for (SMTPMailServer mailServer : this.getSmtpMailServer()) {
            try {
                mailServer.send(this.toEmail(upmEmail));
            }
            catch (MailException e) {
                logger.error("Failed to send email: " + e.getMessage());
                if (!logger.isDebugEnabled()) continue;
                logger.debug("Failed to send email", (Throwable)e);
            }
        }
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        return UpmEmail.Format.HTML;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.option(this.administrationConfigurationManager.getAdministrationConfiguration().getInstanceName());
    }

    private Option<SMTPMailServer> getSmtpMailServer() {
        return Option.some(MailFactory.getServerManager().getDefaultSMTPMailServer());
    }
}

