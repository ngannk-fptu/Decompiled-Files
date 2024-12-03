/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.ExtendedBaseRules
 *  org.apache.commons.digester.Rules
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail.server.managers;

import com.atlassian.mail.MailException;
import com.atlassian.mail.config.ConfigLoader;
import com.atlassian.mail.server.ImapMailServer;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.impl.ImapMailServerImpl;
import com.atlassian.mail.server.impl.PopMailServerImpl;
import com.atlassian.mail.server.impl.SMTPMailServerImpl;
import com.atlassian.mail.server.managers.AbstractMailServerManager;
import com.atlassian.mail.util.ClassLoaderUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.digester.Rules;
import org.apache.log4j.Logger;

public class XMLMailServerManager
extends AbstractMailServerManager {
    private static final Logger log = Logger.getLogger(XMLMailServerManager.class);
    Map<Long, MailServer> serverIds;
    private static String DEFAULT_CONFIG_FILE = "mail-servers.xml";
    String configFile;

    @Override
    public void init(Map params) {
        this.configFile = DEFAULT_CONFIG_FILE;
        this.serverIds = new HashMap<Long, MailServer>();
        if (params.containsKey("config-file")) {
            this.configFile = (String)params.get("config-file");
        }
        this.configure();
    }

    private void configure() {
        try {
            Digester digester = this.newDigester();
            digester.push((Object)this);
            digester.setRules((Rules)new ExtendedBaseRules());
            digester.addObjectCreate("mail-servers/pop-server", this.getPopMailServerClass());
            digester.addSetProperties("mail-servers/pop-server");
            digester.addBeanPropertySetter("mail-servers/pop-server/?");
            digester.addSetRoot("mail-servers/pop-server", "create");
            digester.addObjectCreate("mail-servers/imap-server", this.getImapMailServerClass());
            digester.addSetProperties("mail-servers/imap-server");
            digester.addBeanPropertySetter("mail-servers/imap-server/?");
            digester.addSetRoot("mail-servers/imap-server", "create");
            digester.addObjectCreate("mail-servers/smtp-server", this.getSMTPMailServerClass());
            digester.addSetProperties("mail-servers/smtp-server");
            digester.addBeanPropertySetter("mail-servers/smtp-server/?");
            digester.addBeanPropertySetter("mail-servers/smtp-server/jndi-location", "jndiLocation");
            digester.addSetRoot("mail-servers/smtp-server", "create");
            InputStream is = this.getConfigurationInputStream(this.configFile);
            digester.parse(is);
        }
        catch (Exception e) {
            log.fatal((Object)e, (Throwable)e);
            throw new RuntimeException("Error in mail config: " + e.getMessage(), e);
        }
    }

    protected Digester newDigester() {
        return new Digester();
    }

    protected InputStream getConfigurationInputStream(String resource) {
        return ClassLoaderUtils.getResourceAsStream(resource, ConfigLoader.class);
    }

    public String getConfigFile() {
        return this.configFile;
    }

    @Override
    @Nullable
    public MailServer getMailServer(Long id) {
        return this.serverIds.get(id);
    }

    @Override
    @Nullable
    public MailServer getMailServer(String name) throws MailException {
        if (name == null) {
            throw new MailException("name is null");
        }
        for (MailServer server : this.serverIds.values()) {
            if (!name.equals(server.getName())) continue;
            return server;
        }
        return null;
    }

    @Override
    public synchronized Long create(MailServer mailServer) throws MailException {
        Long id = new Long(this.serverIds.size() + 1);
        while (this.serverIds.containsKey(id)) {
            id = new Long(id + 1L);
        }
        mailServer.setId(id);
        this.serverIds.put(id, mailServer);
        return id;
    }

    @Override
    public void update(MailServer mailServer) throws MailException {
        this.serverIds.put(mailServer.getId(), mailServer);
    }

    @Override
    public void delete(Long mailServerId) throws MailException {
        if (mailServerId == null) {
            throw new MailException("mailServerId is null");
        }
        if (!this.serverIds.containsKey(mailServerId)) {
            throw new MailException("A mail server with the specified mailServerId does not exist");
        }
        this.serverIds.remove(mailServerId);
    }

    @Override
    public List<String> getServerNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (MailServer server : this.serverIds.values()) {
            result.add(server.getName());
        }
        return result;
    }

    @Override
    public List<SMTPMailServer> getSmtpMailServers() {
        ArrayList<SMTPMailServer> result = new ArrayList<SMTPMailServer>();
        for (MailServer server : this.serverIds.values()) {
            if (!(server instanceof SMTPMailServer)) continue;
            result.add((SMTPMailServer)server);
        }
        return result;
    }

    @Override
    public List<PopMailServer> getPopMailServers() {
        ArrayList<PopMailServer> result = new ArrayList<PopMailServer>();
        for (MailServer server : this.serverIds.values()) {
            if (!(server instanceof PopMailServer)) continue;
            result.add((PopMailServer)server);
        }
        return result;
    }

    @Override
    public List<ImapMailServer> getImapMailServers() {
        ArrayList<ImapMailServer> result = new ArrayList<ImapMailServer>();
        for (MailServer server : this.serverIds.values()) {
            if (!(server instanceof ImapMailServer)) continue;
            result.add((ImapMailServer)server);
        }
        return result;
    }

    @Override
    @Nullable
    public SMTPMailServer getDefaultSMTPMailServer() {
        List<SMTPMailServer> smtpServers = this.getSmtpMailServers();
        if (smtpServers.size() > 0) {
            return smtpServers.get(0);
        }
        return null;
    }

    @Override
    @Nullable
    public PopMailServer getDefaultPopMailServer() {
        List<PopMailServer> popServers = this.getPopMailServers();
        if (popServers.size() > 0) {
            return popServers.get(0);
        }
        return null;
    }

    protected Class getSMTPMailServerClass() {
        return SMTPMailServerImpl.class;
    }

    protected Class getPopMailServerClass() {
        return PopMailServerImpl.class;
    }

    protected Class getImapMailServerClass() {
        return ImapMailServerImpl.class;
    }
}

