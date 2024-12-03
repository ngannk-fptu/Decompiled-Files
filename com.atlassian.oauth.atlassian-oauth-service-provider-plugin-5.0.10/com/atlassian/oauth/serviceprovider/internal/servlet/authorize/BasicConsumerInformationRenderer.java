/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException;
import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class BasicConsumerInformationRenderer
implements ConsumerInformationRenderer {
    private static final String TEMPLATE = "templates/auth/basic-consumer-info.vm";
    private final ApplicationProperties applicationProperties;
    private final TemplateRenderer renderer;
    private final UserManager userManager;

    public BasicConsumerInformationRenderer(ApplicationProperties applicationProperties, TemplateRenderer renderer, UserManager userManager) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "renderer");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    public boolean canRender(ServiceProviderToken token, HttpServletRequest request) {
        return true;
    }

    public void render(ServiceProviderToken token, HttpServletRequest request, Writer writer) throws IOException {
        UserProfile profile;
        URI appUri = URI.create(this.applicationProperties.getBaseUrl());
        String username = this.userManager.getRemoteUsername();
        String userFullName = StringUtils.isNotBlank((CharSequence)username) ? ((profile = this.userManager.getUserProfile(username)) != null && StringUtils.isNotBlank((CharSequence)profile.getFullName()) ? profile.getFullName() : username) : "User unknown";
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("consumer", token.getConsumer());
        context.put("applicationDomain", appUri.getHost());
        context.put("userFullName", userFullName);
        try {
            this.renderer.render(TEMPLATE, context, writer);
        }
        catch (RenderingException e) {
            throw new ConsumerInformationRenderException("Could not render consumer information", (Throwable)e);
        }
    }
}

