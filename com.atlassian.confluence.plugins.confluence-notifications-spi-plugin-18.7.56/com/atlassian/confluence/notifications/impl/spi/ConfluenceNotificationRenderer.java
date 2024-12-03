/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext
 *  com.atlassian.plugin.notifications.api.template.TemplateDefinition
 *  com.atlassian.plugin.notifications.api.template.TemplateType
 *  com.atlassian.plugin.notifications.spi.NotificationRenderer
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.AnalyticsRenderContext;
import com.atlassian.confluence.notifications.impl.AnalyticsRenderContextManager;
import com.atlassian.confluence.notifications.impl.FakeHttpRequestInjector;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext;
import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import com.atlassian.plugin.notifications.api.template.TemplateType;
import com.atlassian.plugin.notifications.spi.NotificationRenderer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.base.Throwables;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceNotificationRenderer
implements NotificationRenderer {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceNotificationRenderer.class);
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final FakeHttpRequestInjector fakeHttpRequestInjector;
    private final AnalyticsRenderContextManager contextManager;

    public ConfluenceNotificationRenderer(SoyTemplateRenderer soyTemplateRenderer, FakeHttpRequestInjector fakeHttpRequestInjector, AnalyticsRenderContextManager contextManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.fakeHttpRequestInjector = fakeHttpRequestInjector;
        this.contextManager = contextManager;
    }

    public void render(TemplateDefinition template, Map<String, Object> context, Writer out) {
        this.render(template, context, Optional.empty(), out);
    }

    public void render(TemplateDefinition template, Map<String, Object> context, Optional<String> outputMimeType, Writer out) {
        this.contextManager.setContext((AnalyticsRenderContext.Context)context.get("analyticsContext"), () -> this.fakeHttpRequestInjector.withRequest(() -> {
            try {
                Object injectedData;
                if (log.isDebugEnabled()) {
                    log.debug("Template type: '{}', start: '{}'", (Object)template.getType(), (Object)StringUtils.truncate((String)template.getTemplate(), (int)10));
                }
                Map soyInjectedData = (injectedData = context.get("soyInjectedData")) == null || !(injectedData instanceof Map) ? Collections.emptyMap() : (Map)injectedData;
                if (TemplateType.SOY.equals((Object)template.getType())) {
                    this.soyTemplateRenderer.render((Appendable)out, template.getTemplatePackage(), template.getTemplate(), context, soyInjectedData);
                } else {
                    OutputMimeTypeAwareVelocityContext confluenceVelocityContext = new OutputMimeTypeAwareVelocityContext(context);
                    confluenceVelocityContext.setOutputMimeType(outputMimeType.orElse("text/html"));
                    out.write(VelocityUtils.getRenderedContent((String)template.getTemplate(), (Context)confluenceVelocityContext));
                }
            }
            catch (Throwable e) {
                Throwables.propagate((Throwable)e);
            }
            return null;
        }));
    }
}

