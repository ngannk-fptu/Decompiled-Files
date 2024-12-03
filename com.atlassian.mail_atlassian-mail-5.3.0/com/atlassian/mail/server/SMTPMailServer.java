/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.server;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import java.io.PrintStream;

public interface SMTPMailServer
extends MailServer {
    @Deprecated
    public static final String DEFAULT_SMTP_PORT = "25";

    public String getDefaultFrom();

    public void setDefaultFrom(String var1);

    public String getPrefix();

    public void setPrefix(String var1);

    public boolean isSessionServer();

    public void setSessionServer(boolean var1);

    public String getJndiLocation();

    public void setJndiLocation(String var1);

    public boolean isRemovePrecedence();

    public void setRemovePrecedence(boolean var1);

    public void send(Email var1) throws MailException;

    public void sendWithMessageId(Email var1, String var2) throws MailException;

    public void quietSend(Email var1) throws MailException;

    @Override
    public void setDebug(boolean var1);

    @Override
    public boolean getDebug();

    public PrintStream getDebugStream();

    public boolean isTlsRequired();

    public void setTlsRequired(boolean var1);

    default public boolean isTransportCachingEnabled() {
        return false;
    }
}

