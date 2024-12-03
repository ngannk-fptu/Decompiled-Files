/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.manager.SafeModeManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.confluence.security.websudo.WebSudoMessage;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.message.DefaultMessage;
import com.atlassian.confluence.util.message.MessageManager;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.manager.SafeModeManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessagesDecoratorFilter
extends AbstractHttpFilter {
    private static final String ALREADY_FILTERED = MessagesDecoratorFilter.class.getName() + "__already_filtered__";
    private static final Logger log = LoggerFactory.getLogger(MessagesDecoratorFilter.class);
    private final Supplier<WebSudoManager> webSudoManager = new LazyComponentReference("webSudoManager");
    private final Supplier<MessageManager> messageManager = new LazyComponentReference("requestMessageManager");
    private final Supplier<DocumentationBean> documentationBean = new LazyComponentReference("documentationBean");
    private final Supplier<SafeModeManager> safeModeManager = new LazyComponentReference("safeModeManager");
    private final Supplier<I18NBeanFactory> i18NBeanFactory = new LazyComponentReference("userI18NBeanFactory");
    private final Supplier<SettingsManager> settingsManager = new LazyComponentReference("settingsManager");
    private final Supplier<LicenseService> licenseService = new LazyComponentReference("licenseService");

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (Boolean.TRUE.equals(request.getAttribute(ALREADY_FILTERED))) {
            log.debug("MessagesDecoratorFilter did already run. Skipping");
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        request.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
        log.debug("Execute the MessagesDecoratorFilter.");
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        if (!ContainerManager.isContainerSetup()) {
            return;
        }
        this.addMessages(new MessageContext(request, this.getWebSudoManager(), this.getI18NBean(), this.getDocumentationBean(), this.getSafeModeManager(), this.getSettingsManager(), this.getLicenseService()), request);
    }

    private void addMessages(MessageContext context, HttpServletRequest request) {
        if (null == context) {
            return;
        }
        context.withMessageManager(this.getMessageManager());
    }

    private WebSudoManager getWebSudoManager() {
        return (WebSudoManager)this.webSudoManager.get();
    }

    private MessageManager getMessageManager() {
        return (MessageManager)this.messageManager.get();
    }

    public I18NBean getI18NBean() {
        return ((I18NBeanFactory)this.i18NBeanFactory.get()).getI18NBean();
    }

    private SafeModeManager getSafeModeManager() {
        return (SafeModeManager)this.safeModeManager.get();
    }

    public DocumentationBean getDocumentationBean() {
        return (DocumentationBean)this.documentationBean.get();
    }

    public SettingsManager getSettingsManager() {
        return (SettingsManager)this.settingsManager.get();
    }

    public LicenseService getLicenseService() {
        return (LicenseService)this.licenseService.get();
    }

    @VisibleForTesting
    static class MessageContext {
        private final HttpServletRequest request;
        private final WebSudoManager webSudoManager;
        private final I18NBean i18NBean;
        private final DocumentationBean documentationBean;
        private final SafeModeManager safeModeManager;
        private final SettingsManager settingsManager;
        private final LicenseService licenseService;

        MessageContext(HttpServletRequest request, WebSudoManager webSudoManager, I18NBean i18NBean, DocumentationBean documentationBean, SafeModeManager safeModeManager, SettingsManager settingsManager, LicenseService licenseService) {
            this.webSudoManager = webSudoManager;
            this.documentationBean = documentationBean;
            this.i18NBean = i18NBean;
            this.request = request;
            this.safeModeManager = safeModeManager;
            this.settingsManager = settingsManager;
            this.licenseService = licenseService;
        }

        void withMessageManager(MessageManager messageManager) {
            Settings globalSettings;
            if (this.webSudoManager.hasValidSession(this.request.getSession(false))) {
                boolean isWebSudoRequest = this.webSudoManager.isWebSudoRequest(this.request);
                StringBuilder sb = new StringBuilder(this.request.getContextPath());
                if (!isWebSudoRequest) {
                    sb.append("/ajax");
                }
                sb.append("/dropauthentication.action");
                log.debug("Add the WebSudo message to the decorator. Is the resource a WebSudo protected resource: {}", (Object)isWebSudoRequest);
                messageManager.addMessage(new WebSudoMessage(this.i18NBean.getText("websudo.header", new String[]{isWebSudoRequest ? "drop-websudo-request" : "drop-non-websudo", sb.toString(), this.documentationBean.getLink("help.websudo")})));
            }
            if (this.safeModeManager.isInSafeMode()) {
                messageManager.addMessage(new DefaultMessage(this.i18NBean.getText("safemode.activated"), "noteMessage", false));
            }
            if ((globalSettings = this.settingsManager.getGlobalSettings()).isMaintenanceBannerMessageOn() && this.licenseService.isLicensedForDataCenterOrExempt()) {
                String bannerMessage = (String)StringUtils.defaultIfBlank((CharSequence)globalSettings.getMaintenanceBannerMessage(), (CharSequence)this.i18NBean.getText("read.only.mode.default.banner.message"));
                messageManager.addMessage(new DefaultMessage("warning-banner", bannerMessage, "", false));
            }
        }
    }
}

