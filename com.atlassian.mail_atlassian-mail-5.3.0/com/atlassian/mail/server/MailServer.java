/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Session
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail.server;

import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import java.io.PrintStream;
import java.util.Properties;
import javax.mail.Session;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

public interface MailServer {
    public Long getId();

    public String getName();

    public String getDescription();

    public String getType();

    public String getPort();

    public MailProtocol getMailProtocol();

    public String getHostname();

    public String getUsername();

    public String getPassword();

    public long getTimeout();

    public String getSocksHost();

    public String getSocksPort();

    public boolean getDebug();

    public Session getSession() throws NamingException, MailException;

    public void setName(String var1);

    public void setDescription(String var1);

    public void setHostname(String var1);

    public void setUsername(String var1);

    public void setPassword(String var1);

    public void setId(Long var1);

    public void setPort(String var1);

    public void setMailProtocol(MailProtocol var1);

    public void setTimeout(long var1);

    public void setSocksHost(String var1);

    public void setSocksPort(String var1);

    public void setDebug(boolean var1);

    public void setDebugStream(PrintStream var1);

    public void setProperties(Properties var1);

    public Properties getProperties();

    public void setLogger(Logger var1);

    default public boolean isTlsHostnameCheckRequired() {
        return false;
    }

    default public void setTlsHostnameCheckRequired(boolean tlsHostnameCheckRequired) {
        throw new UnsupportedOperationException();
    }
}

