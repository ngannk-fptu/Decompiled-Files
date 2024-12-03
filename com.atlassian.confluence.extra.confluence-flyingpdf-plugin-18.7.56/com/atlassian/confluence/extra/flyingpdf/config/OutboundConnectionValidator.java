/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboundConnectionValidator {
    public static final Set<String> LOCAL_HOSTS = ImmutableSet.of((Object)"localhost", (Object)"127.0.0.1", (Object)"[::1]");
    private OutboundWhitelist outboundWhitelist;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;

    @Autowired
    public OutboundConnectionValidator(@ComponentImport OutboundWhitelist outboundWhitelist, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.outboundWhitelist = outboundWhitelist;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public ValidateResult validate(String url) {
        UserKey userKey = this.getAuthenticatedUser() == null ? null : this.getAuthenticatedUser().getKey();
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host != null) {
                if (LOCAL_HOSTS.contains(host)) {
                    if (!OutboundConnectionValidator.isUrlWithLocalHostAllowed()) {
                        return new ValidateResult(false, this.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.config.url.local.host.not.enabled", (Object[])new String[]{url}));
                    }
                } else if (!this.outboundWhitelist.isAllowed(uri, userKey)) {
                    return new ValidateResult(false, this.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.config.url.not.whitelisted", (Object[])new String[]{url}));
                }
            }
        }
        catch (IllegalArgumentException e) {
            return new ValidateResult(false, this.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.config.url.invalid", (Object[])new String[]{url}));
        }
        return new ValidateResult(true);
    }

    private ConfluenceUser getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)this.getAuthenticatedUser()));
    }

    static boolean isUrlWithLocalHostAllowed() {
        return Boolean.getBoolean("confluence.pdfexport.allow.local.hosts");
    }

    public class ValidateResult {
        private boolean valid;
        private String errorMessage;

        public ValidateResult(boolean valid) {
            this.valid = valid;
        }

        public ValidateResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return this.valid;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}

