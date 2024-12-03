/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.easyuser;

import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class EasyUserContextProvider
implements ContextProvider {
    private final SignupManager easyUserManager;
    private final GlobalSettingsManager settingsManager;
    private final MailServerManager mailServerManager;

    public EasyUserContextProvider(SignupManager easyUserManager, GlobalSettingsManager settingsManager, MailServerManager mailServerManager) {
        this.easyUserManager = easyUserManager;
        this.settingsManager = settingsManager;
        this.mailServerManager = mailServerManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        context.put("inviteLink", this.easyUserManager.getSignupURL());
        context.put("inviteEmail", this.easyUserManager.isEmailSentOnInviteSignUp());
        context.put("siteTitle", this.settingsManager.getGlobalSettings().getSiteTitle());
        context.put("isSmtpConfigured", this.mailServerManager.isDefaultSMTPMailServerDefined());
        context.put("configureEmailLink", this.getConfigureEmailLink());
        return context;
    }

    private String getConfigureEmailLink() {
        return ServletContextThreadLocal.getRequest().getContextPath() + "/admin/mail/createmailserver.action";
    }
}

