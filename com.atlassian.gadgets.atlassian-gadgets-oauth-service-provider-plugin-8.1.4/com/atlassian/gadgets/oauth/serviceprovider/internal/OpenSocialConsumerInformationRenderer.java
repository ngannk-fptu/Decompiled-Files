/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContextFactory
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.oauth.serviceprovider.internal;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContextFactory;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException;
import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class OpenSocialConsumerInformationRenderer
implements ConsumerInformationRenderer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TemplateRenderer renderer;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final GadgetRequestContextFactory gadgetRequestContextFactory;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;

    @Autowired
    public OpenSocialConsumerInformationRenderer(@ComponentImport TemplateRenderer renderer, @ComponentImport GadgetSpecFactory gadgetSpecFactory, @ComponentImport GadgetRequestContextFactory gadgetRequestContextFactory, @ComponentImport UserManager userManager, @ComponentImport I18nResolver i18nResolver) {
        this.renderer = (TemplateRenderer)Preconditions.checkNotNull((Object)renderer, (Object)"renderer");
        this.gadgetSpecFactory = (GadgetSpecFactory)Preconditions.checkNotNull((Object)gadgetSpecFactory, (Object)"gadgetSpecFactory");
        this.gadgetRequestContextFactory = (GadgetRequestContextFactory)Preconditions.checkNotNull((Object)gadgetRequestContextFactory, (Object)"gadgetRequestContextFactory");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
    }

    public boolean canRender(ServiceProviderToken token, HttpServletRequest request) {
        return token.hasProperty("xoauth_app_url");
    }

    public void render(ServiceProviderToken token, HttpServletRequest request, Writer writer) throws IOException {
        UserProfile profile;
        HashMap<String, Object> context = new HashMap<String, Object>();
        try {
            GadgetSpec spec = this.getGadgetSpec(token, request);
            context.put("gadgetSpec", spec);
        }
        catch (ConsumerInformationRenderException e) {
            String gadgetUri = token.getProperty("xoauth_app_url");
            this.logger.warn("Error parsing gadget from '" + gadgetUri + "'.", (Throwable)e);
            context.put("gadgetRetrievalError", true);
            context.put("gadgetUri", gadgetUri);
        }
        context.put("consumer", token.getConsumer());
        String username = this.userManager.getRemoteUsername();
        String userFullName = StringUtils.isNotBlank((CharSequence)username) ? ((profile = this.userManager.getUserProfile(username)) != null && StringUtils.isNotBlank((CharSequence)profile.getFullName()) ? profile.getFullName() : username) : this.i18nResolver.getText("com.atlassian.gadgets.oauth.serviceprovider.authorize.user.not.found");
        context.put("userFullName", userFullName);
        try {
            this.renderer.render("opensocial-consumer-info.vm", context, writer);
        }
        catch (RenderingException e) {
            throw new ConsumerInformationRenderException("Could not render consumer information", (Throwable)e);
        }
    }

    private GadgetSpec getGadgetSpec(ServiceProviderToken token, HttpServletRequest request) {
        GadgetSpec spec;
        try {
            spec = this.gadgetSpecFactory.getGadgetSpec(URI.create(token.getProperty("xoauth_app_url")), this.gadgetRequestContextFactory.get(request));
        }
        catch (GadgetParsingException e) {
            throw new ConsumerInformationRenderException("Parsing of the OpenSocial gadget failed", (Throwable)e);
        }
        return spec;
    }
}

