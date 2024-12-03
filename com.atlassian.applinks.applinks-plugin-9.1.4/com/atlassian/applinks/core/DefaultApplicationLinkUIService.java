/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkUIService
 *  com.atlassian.applinks.api.ApplicationLinkUIService$MessageBuilder
 *  com.atlassian.applinks.api.ApplicationLinkUIService$MessageFormat
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkUIService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplicationLinkUIService
implements ApplicationLinkUIService {
    private static final String REQUEST_BANNER_TEMPLATE = "templates/fragments/auth_request_banner.vm";
    private static final String REQUEST_INLINE_TEMPLATE = "templates/fragments/auth_request_inline.vm";
    private final I18nResolver i18nResolver;
    private final TemplateRenderer templateRenderer;

    @Autowired
    public DefaultApplicationLinkUIService(I18nResolver i18nResolver, TemplateRenderer templateRenderer) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
    }

    public ApplicationLinkUIService.MessageBuilder authorisationRequest(ApplicationLink appLink) {
        return new AuthRequestMessageBuilder(Objects.requireNonNull(appLink, "appLink"));
    }

    private class AuthRequestMessageBuilder
    implements ApplicationLinkUIService.MessageBuilder {
        private final ApplicationLink appLink;
        private ApplicationLinkUIService.MessageFormat format = ApplicationLinkUIService.MessageFormat.BANNER;
        private String contentHtml = "";

        AuthRequestMessageBuilder(ApplicationLink appLink) {
            this.appLink = appLink;
        }

        public ApplicationLinkUIService.MessageBuilder format(ApplicationLinkUIService.MessageFormat format) {
            this.format = Objects.requireNonNull(format, "format");
            return this;
        }

        public ApplicationLinkUIService.MessageBuilder contentHtml(String contentHtml) {
            this.contentHtml = Objects.requireNonNull(contentHtml, "contentHtml");
            return this;
        }

        public String getHtml() {
            String messageHtml;
            String template;
            String applinkId = this.appLink.getId().toString();
            String appName = this.appLink.getName();
            String appUri = this.appLink.getDisplayUrl().toString();
            String authUri = this.appLink.createAuthenticatedRequestFactory().getAuthorisationURI().toString();
            switch (this.format) {
                case INLINE: {
                    template = DefaultApplicationLinkUIService.REQUEST_INLINE_TEMPLATE;
                    messageHtml = DefaultApplicationLinkUIService.this.i18nResolver.getText("applinks.util.auth.request.inline", new Serializable[]{StringEscapeUtils.escapeHtml4((String)authUri)});
                    break;
                }
                default: {
                    template = DefaultApplicationLinkUIService.REQUEST_BANNER_TEMPLATE;
                    messageHtml = DefaultApplicationLinkUIService.this.i18nResolver.getText("applinks.util.auth.request", new Serializable[]{StringEscapeUtils.escapeHtml4((String)authUri), StringEscapeUtils.escapeHtml4((String)appUri), StringEscapeUtils.escapeHtml4((String)appName)});
                }
            }
            StringWriter buf = new StringWriter();
            ImmutableMap.Builder contextBuilder = ImmutableMap.builder().put((Object)"applinkId", (Object)applinkId).put((Object)"appName", (Object)appName).put((Object)"appUri", (Object)appUri).put((Object)"authUri", (Object)authUri).put((Object)"messageHtml", (Object)messageHtml).put((Object)"contentHtml", (Object)this.contentHtml);
            try {
                DefaultApplicationLinkUIService.this.templateRenderer.render(template, (Map)contextBuilder.build(), (Writer)buf);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return buf.toString();
        }
    }
}

