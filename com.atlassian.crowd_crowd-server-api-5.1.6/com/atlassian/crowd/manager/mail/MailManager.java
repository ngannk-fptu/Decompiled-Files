/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.mail.SMTPServer
 *  com.atlassian.crowd.validator.ValidationError
 *  javax.activation.DataSource
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.manager.mail.EmailMessage;
import com.atlassian.crowd.manager.mail.MailConfiguration;
import com.atlassian.crowd.manager.mail.MailConfigurationService;
import com.atlassian.crowd.manager.mail.MailSendException;
import com.atlassian.crowd.manager.mail.SendTestMailResult;
import com.atlassian.crowd.util.mail.SMTPServer;
import com.atlassian.crowd.validator.ValidationError;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.internet.InternetAddress;

public interface MailManager
extends MailConfigurationService {
    @Deprecated
    public void sendHtmlEmail(InternetAddress var1, String var2, String var3, String var4) throws MailSendException;

    @Deprecated
    public void sendPlainTextEmail(InternetAddress var1, String var2, String var3) throws MailSendException;

    @Deprecated
    public void sendPlainTextEmail(InternetAddress var1, String var2, String var3, Map<String, String> var4, Map<String, DataSource> var5) throws MailSendException;

    public SendTestMailResult sendTestMail(SMTPServer var1, InternetAddress var2) throws MailSendException;

    public Collection<? extends EmailMessage> sendEmails(Collection<? extends EmailMessage> var1) throws MailSendException;

    public void sendEmail(EmailMessage var1) throws MailSendException;

    public List<ValidationError> validateConfiguration(MailConfiguration var1);

    @Override
    @Deprecated
    public void saveConfiguration(MailConfiguration var1);

    @Override
    @Deprecated
    public MailConfiguration getMailConfiguration();

    @Override
    @Deprecated
    public boolean isConfigured();
}

