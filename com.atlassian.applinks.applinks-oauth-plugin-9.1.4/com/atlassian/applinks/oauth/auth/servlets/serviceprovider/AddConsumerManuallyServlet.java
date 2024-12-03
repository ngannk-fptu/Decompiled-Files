/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$ForbiddenException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.serviceprovider.AbstractConsumerServlet;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.Key;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddConsumerManuallyServlet
extends AbstractConsumerServlet {
    private final ServiceProviderStoreService providerStoreService;
    private final WebSudoManager webSudoManager;
    private static final Logger LOG = LoggerFactory.getLogger(AddConsumerManuallyServlet.class);
    private static final String INCOMING_NON_APPLINKS_TEMPLATE = "com/atlassian/applinks/oauth/auth/incoming_nonapplinks.vm";
    private static final String CONSUMER = "consumer";
    private static final String PUBLIC_KEY = "publicKey";

    protected AddConsumerManuallyServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, RequestFactory requestFactory, ServiceProviderStoreService providerStoreService, InternalHostApplication internalHostApplication, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, requestFactory, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.providerStoreService = providerStoreService;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            Consumer consumer = this.providerStoreService.getConsumer(applicationLink);
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            builder.put("contextPath", (Object)request.getContextPath());
            builder.put("message", (Object)this.getMessage(request));
            if (consumer != null) {
                builder.put(CONSUMER, (Object)consumer);
                String publicKey = RSAKeys.toPemEncoding((Key)consumer.getPublicKey());
                builder.put(PUBLIC_KEY, (Object)publicKey);
            }
            builder.put("isSysadmin", (Object)this.isSysadmin());
            this.render(INCOMING_NON_APPLINKS_TEMPLATE, builder.build(), request, response, applicationLink);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            HashMap<String, String> fieldErrorMessages = new HashMap<String, String>();
            boolean enabled = Boolean.parseBoolean(this.checkRequiredFormParameter(request, "oauth-incoming-enabled", fieldErrorMessages, "auth.oauth.config.error.enable"));
            this.addOrRemoveConsumer(request, applicationLink, fieldErrorMessages, enabled);
            if (fieldErrorMessages.isEmpty()) {
                String message = enabled ? this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.enabled") : this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.disabled");
                response.sendRedirect("./" + applicationLink.getId() + "?" + "message" + "=" + URIUtil.utf8Encode((String)message));
            } else {
                FormFields formFields = new FormFields(request);
                RendererContextBuilder builder = this.createContextBuilder(applicationLink);
                builder.put("contextPath", (Object)request.getContextPath());
                builder.put(CONSUMER, (Object)formFields);
                builder.put(PUBLIC_KEY, (Object)formFields.getPublicKey());
                builder.put("fieldErrorMessages", fieldErrorMessages);
                builder.put("isSysadmin", (Object)this.isSysadmin());
                this.render(INCOMING_NON_APPLINKS_TEMPLATE, builder.build(), request, response, applicationLink);
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void addOrRemoveConsumer(HttpServletRequest request, ApplicationLink applicationLink, Map<String, String> fieldErrorMessages, boolean enabled) throws IOException {
        if (enabled) {
            String key = this.checkRequiredFormParameter(request, "key", fieldErrorMessages, "auth.oauth.config.serviceprovider.missing.consumer.key");
            String name = this.checkRequiredFormParameter(request, "consumerName", fieldErrorMessages, "auth.oauth.config.serviceprovider.missing.consumer.name");
            String description = request.getParameter("description");
            PublicKey publicKey = this.getPublicKey(request, fieldErrorMessages);
            URI callback = this.getCallbackUri(request, fieldErrorMessages);
            boolean twoLOAllowed = Boolean.parseBoolean(request.getParameter("two-lo-enabled"));
            if (twoLOAllowed && !this.isSysadmin()) {
                throw new AbstractApplinksServlet.ForbiddenException(this.messageFactory.newI18nMessage("applinks.error.only.sysadmin.operation", new Serializable[0]));
            }
            String executingTwoLOUser = null;
            if (twoLOAllowed && !StringUtils.isBlank((CharSequence)(executingTwoLOUser = this.checkRequiredFormParameter(request, "two-lo-execute-as", fieldErrorMessages, "auth.oauth.config.2lo.username.error"))) && this.userManager.resolve(executingTwoLOUser) == null) {
                fieldErrorMessages.put("two-lo-execute-as", this.i18nResolver.getText("auth.oauth.config.2lo.username.error"));
            }
            boolean twoLOImpersonationAllowed = Boolean.parseBoolean(request.getParameter("two-lo-impersonation-enabled"));
            if (!fieldErrorMessages.isEmpty()) {
                return;
            }
            try {
                Consumer consumer = Consumer.key((String)key).name(name).publicKey(publicKey).description(description).callback(callback).twoLOAllowed(twoLOAllowed).executingTwoLOUser(executingTwoLOUser).twoLOImpersonationAllowed(twoLOImpersonationAllowed).build();
                this.providerStoreService.addConsumer(consumer, applicationLink);
            }
            catch (Exception e) {
                LOG.error("Failed to store consumer key", (Throwable)e);
                fieldErrorMessages.put("communication", this.i18nResolver.getText("auth.oauth.config.error.consumer.add", new Serializable[]{e.getMessage()}));
            }
        } else {
            try {
                this.providerStoreService.removeConsumer(applicationLink);
            }
            catch (Exception e) {
                LOG.error("Failed to disable OAuth outgoing, when trying to remove the consumer for application link '" + applicationLink + "'", (Throwable)e);
                fieldErrorMessages.put("communication", this.i18nResolver.getText("auth.oauth.config.error.consumer.remove", new Serializable[]{e.getMessage()}));
            }
        }
    }

    @Override
    protected List<String> getRequiredWebResources() {
        return new ImmutableList.Builder().addAll(super.getRequiredWebResources()).add((Object)"com.atlassian.applinks.applinks-oauth-plugin:oauth-2lo-config").build();
    }

    public static class FormFields {
        public String key;
        public String name;
        public String description;
        public String publicKey;
        public String callback;
        public boolean twoLOAllowed;
        public String executingTwoLOUser;
        public boolean twoLOImpersonationAllowed;

        public FormFields(HttpServletRequest request) {
            this.key = request.getParameter("key");
            this.name = request.getParameter("consumerName");
            this.description = request.getParameter("description");
            this.publicKey = request.getParameter(AddConsumerManuallyServlet.PUBLIC_KEY);
            this.callback = request.getParameter("callback");
            this.twoLOAllowed = Boolean.parseBoolean(request.getParameter("two-lo-enabled"));
            this.executingTwoLOUser = request.getParameter("two-lo-execute-as");
            this.twoLOImpersonationAllowed = Boolean.parseBoolean(request.getParameter("two-lo-impersonation-enabled"));
        }

        public String getKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getPublicKey() {
            return this.publicKey;
        }

        public String getCallback() {
            return this.callback;
        }

        public boolean isTwoLOAllowed() {
            return this.twoLOAllowed;
        }

        public String getExecutingTwoLOUser() {
            return this.executingTwoLOUser;
        }

        public boolean isTwoLOImpersonationAllowed() {
            return this.twoLOImpersonationAllowed;
        }
    }
}

