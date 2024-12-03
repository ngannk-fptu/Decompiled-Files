/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.managers.XMLMailServerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.managers.XMLMailServerManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailConfigMigrator {
    private static final Logger log = LoggerFactory.getLogger(MailConfigMigrator.class);
    public static final String MAIL_CONFIG_FILE = "confluence-mail.cfg.xml";
    private MailServerManager mailServerManager;

    public void run() {
        this.run(new File(BootstrapUtils.getBootstrapManager().getApplicationHome() + "/config/", MAIL_CONFIG_FILE));
    }

    private void run(File mailConfigFile) {
        if (mailConfigFile == null || !mailConfigFile.exists()) {
            return;
        }
        try {
            OldXMLMailServerManager xmlMailServerManager = new OldXMLMailServerManager(mailConfigFile);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("config-file", MAIL_CONFIG_FILE);
            xmlMailServerManager.init(params);
            for (MailServer mailServer : xmlMailServerManager.getSmtpMailServers()) {
                this.mailServerManager.create(mailServer);
            }
        }
        catch (MailException e) {
            log.error("Error migrating confluence-mail.cfg.xml to database.", (Throwable)e);
        }
        mailConfigFile.delete();
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    private static class OldXMLMailServerManager
    extends XMLMailServerManager {
        private File mailConfigFile;

        public OldXMLMailServerManager(File mailConfigFile) {
            this.mailConfigFile = mailConfigFile;
        }

        protected InputStream getConfigurationInputStream(String resource) {
            try {
                return new FileInputStream(this.mailConfigFile);
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException();
            }
        }
    }
}

