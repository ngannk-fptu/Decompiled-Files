/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.PopMailServer
 *  com.atlassian.mail.server.SMTPMailServer
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequest;
import java.util.List;

public interface MailUtility {
    public String getDefaultFromAddress();

    public boolean isMailServerConfigured();

    public void sendSupportRequestMail(SupportRequest var1, SupportApplicationInfo var2) throws Exception;

    public void sendMail(Email var1);

    public List<SMTPMailServer> getSmtpMailServers();

    public List<PopMailServer> getPopMailServers();

    public boolean isDefaultSmtpMailServer(MailServer var1);

    public boolean isDefaultPopMailServer(MailServer var1);

    public String getAuthenticationMethod(MailServer var1);
}

