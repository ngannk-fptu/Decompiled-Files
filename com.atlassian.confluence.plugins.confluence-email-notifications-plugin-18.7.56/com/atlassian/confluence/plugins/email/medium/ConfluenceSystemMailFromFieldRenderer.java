/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.jmx.JmxSMTPMailServer
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.jmx.JmxSMTPMailServer;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.email.medium.SystemMailFromFieldRenderer;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceSystemMailFromFieldRenderer
implements SystemMailFromFieldRenderer {
    public static final String ANONYMOUS_NAME_I18N_KEY = "anonymous.name";
    private final MailServerManager mailServerManager;
    private final I18NBeanFactory i18nbeanFactory;
    private final LocaleManager localeManager;

    public ConfluenceSystemMailFromFieldRenderer(MailServerManager mailServerManager, I18NBeanFactory i18nbeanFactory, LocaleManager localeManager) {
        this.mailServerManager = mailServerManager;
        this.i18nbeanFactory = i18nbeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public String renderFromField(UserProfile originator, User recipient) {
        String from = "${fullname} (Confluence)";
        SMTPMailServer mailServer = this.mailServerManager.getDefaultSMTPMailServer();
        if (mailServer instanceof JmxSMTPMailServer) {
            JmxSMTPMailServer server = (JmxSMTPMailServer)mailServer;
            from = server.getFromName();
        }
        String name = originator != null ? originator.getFullName() : this.getTextUsingLocaleOfRecipient(recipient, ANONYMOUS_NAME_I18N_KEY);
        String emailAddress = originator != null ? originator.getEmail() : "";
        String hostname = originator != null && StringUtils.isNotBlank((CharSequence)emailAddress) ? emailAddress.substring(emailAddress.indexOf("@") + 1) : "";
        from = StringUtils.replace((String)StringUtils.defaultString((String)from), (String)"${fullname}", (String)name);
        from = StringUtils.replace((String)from, (String)"${email}", (String)emailAddress);
        from = StringUtils.replace((String)from, (String)"${email.hostname}", (String)hostname);
        return from;
    }

    private String getTextUsingLocaleOfRecipient(User recipient, String key) {
        return this.i18nbeanFactory.getI18NBean(this.localeManager.getLocale(recipient)).getText(key);
    }
}

