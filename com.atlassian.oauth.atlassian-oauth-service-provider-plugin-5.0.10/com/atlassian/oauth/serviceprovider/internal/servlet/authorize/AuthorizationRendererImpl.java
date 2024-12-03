/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderException;
import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRenderer;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public final class AuthorizationRendererImpl
implements AuthorizationRenderer {
    private final TemplateRenderer renderer;
    private final Iterable<ConsumerInformationRenderer> consumerInfoRenderers;
    private final ConsumerInformationRenderer basicConsumerInformationRenderer;
    private final ApplicationProperties applicationProperties;
    private final XsrfTokenAccessor xsrfTokenAccessor;
    private final XsrfTokenValidator xsrfTokenValidator;

    public AuthorizationRendererImpl(ApplicationProperties applicationProperties, TemplateRenderer renderer, Iterable<ConsumerInformationRenderer> consumerInfoRenderers, @Qualifier(value="basicConsumerInformationRenderer") ConsumerInformationRenderer basicConsumerInformationRenderer, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.consumerInfoRenderers = Objects.requireNonNull(consumerInfoRenderers, "consumerInfoRenderers");
        this.basicConsumerInformationRenderer = Objects.requireNonNull(basicConsumerInformationRenderer, "basicConsumerInformationRenderer");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.xsrfTokenAccessor = Objects.requireNonNull(xsrfTokenAccessor, "xsrfTokenAccessor");
        this.xsrfTokenValidator = Objects.requireNonNull(xsrfTokenValidator, "xsrfTokenValidator");
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        ConsumerInformationRenderer consumerInfoRenderer = this.findConsumerInfoRenderer(request, token);
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("applicationProperties", this.applicationProperties);
        context.put("token", token.getToken());
        context.put("csrfToken", this.xsrfTokenAccessor.getXsrfToken(request, response, true));
        context.put("csrfTokenParamName", this.xsrfTokenValidator.getXsrfParameterName());
        context.put("consumer", token.getConsumer());
        context.put("consumerRenderer", new AuthorizationConsumerRenderer(consumerInfoRenderer, token, request, response.getWriter()));
        context.put("accessTokensAdminUri", this.applicationProperties.getBaseUrl() + "/plugins/servlet/oauth/users/access-tokens");
        String callback = request.getParameter("oauth_callback");
        if (callback != null) {
            context.put("callback", callback);
        }
        try {
            this.renderer.render("templates/auth/authorize.vm", Collections.unmodifiableMap(context), (Writer)response.getWriter());
        }
        catch (RenderingException e) {
            throw new ConsumerInformationRenderException("Could not render consumer information", (Throwable)e);
        }
    }

    private ConsumerInformationRenderer findConsumerInfoRenderer(HttpServletRequest request, ServiceProviderToken token) {
        return StreamSupport.stream(this.consumerInfoRenderers.spliterator(), false).filter(ciRenderer -> ciRenderer.canRender(token, request)).findFirst().map(dynamicRenderer -> new DynamicSafeConsumerInformationRenderer((ConsumerInformationRenderer)dynamicRenderer, this.basicConsumerInformationRenderer)).orElse(this.basicConsumerInformationRenderer);
    }

    private static final class DynamicSafeConsumerInformationRenderer
    implements ConsumerInformationRenderer {
        private final ConsumerInformationRenderer dynamicRenderer;
        private final ConsumerInformationRenderer fallbackRenderer;

        public DynamicSafeConsumerInformationRenderer(ConsumerInformationRenderer dynamicRenderer, ConsumerInformationRenderer fallbackRenderer) {
            this.dynamicRenderer = dynamicRenderer;
            this.fallbackRenderer = fallbackRenderer;
        }

        public boolean canRender(ServiceProviderToken token, HttpServletRequest request) {
            return true;
        }

        public void render(ServiceProviderToken token, HttpServletRequest request, Writer writer) throws IOException {
            try {
                this.dynamicRenderer.render(token, request, writer);
            }
            catch (RuntimeException e) {
                if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) {
                    this.fallbackRenderer.render(token, request, writer);
                }
                throw e;
            }
        }
    }

    private static final class AuthorizationConsumerRenderer
    implements Renderable {
        private final ConsumerInformationRenderer renderer;
        private final ServiceProviderToken token;
        private final HttpServletRequest request;
        private final PrintWriter writer;

        public AuthorizationConsumerRenderer(ConsumerInformationRenderer renderer, ServiceProviderToken token, HttpServletRequest request, PrintWriter writer) {
            this.renderer = renderer;
            this.token = token;
            this.request = request;
            this.writer = writer;
        }

        @Override
        public final void render() throws IOException {
            this.renderer.render(this.token, this.request, (Writer)this.writer);
        }
    }

    public static interface Renderable {
        public void render() throws IOException;
    }
}

