/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.TemplateManager;
import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import com.atlassian.plugin.notifications.api.template.TemplateType;
import com.atlassian.plugin.notifications.spi.RendererComponentAccessor;
import com.atlassian.plugin.notifications.spi.TemplateLocator;
import com.atlassian.plugin.notifications.spi.TemplateParams;
import com.atlassian.plugin.notifications.spi.TemplateParamsBuilder;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class TemplateManagerImpl
implements TemplateManager {
    private static final Logger log = Logger.getLogger(TemplateManagerImpl.class);
    private static final String DIR_MESSAGE = "message/";
    private static final String DIR_SUBJECT = "subject/";
    private final TemplateRenderer templateRenderer;
    private final TemplateLocator templateLocator;
    private final RendererComponentAccessor rendererComponentAccessor;
    private final SoyTemplateRenderer soyTemplateRenderer;

    public TemplateManagerImpl(TemplateRenderer templateRenderer, TemplateLocator templateLocator, SoyTemplateRenderer soyTemplateRenderer, RendererComponentAccessor rendererComponentAccessor) {
        this.templateRenderer = templateRenderer;
        this.rendererComponentAccessor = rendererComponentAccessor;
        this.templateLocator = templateLocator;
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    @Override
    public String renderMessage(RecipientType recipientType, Map<String, Object> params, ServerConfiguration serverConfig) {
        return this.render(DIR_MESSAGE, recipientType, params, Optional.of("text/html"), serverConfig);
    }

    @Override
    public String renderSubject(RecipientType recipientType, Map<String, Object> params, ServerConfiguration serverConfig) {
        return this.render(DIR_SUBJECT, recipientType, params, Optional.of("text/plain"), serverConfig);
    }

    private String render(String templateType, RecipientType recipientType, Map<String, Object> context, Optional<String> outputMimeType, ServerConfiguration serverConfig) {
        String customTemplatePath = serverConfig.getCustomTemplatePath();
        String eventTypeKey = StringUtils.deleteWhitespace((String)StringUtils.lowerCase((String)((String)context.get("eventTypeKey"))));
        NotificationMedium medium = serverConfig.getNotificationMedium();
        TemplateParams params = TemplateParamsBuilder.create().customTemplatePath(customTemplatePath).templateType(templateType).eventTypeKey(eventTypeKey).mediumKey(medium.getKey()).recipientType(recipientType).context(context).build();
        TemplateDefinition template = this.templateLocator.getTemplate(params);
        if (template != null) {
            if (this.rendererComponentAccessor.getRenderer() != null) {
                StringWriter out = new StringWriter();
                this.rendererComponentAccessor.getRenderer().render(template, context, outputMimeType, out);
                return out.toString();
            }
            if (template.getType().equals((Object)TemplateType.VM)) {
                return this.templateRenderer.renderFragment(template.getTemplate(), context);
            }
            if (template.getType().equals((Object)TemplateType.SOY)) {
                try {
                    return this.soyTemplateRenderer.render(template.getTemplatePackage(), template.getTemplate(), context);
                }
                catch (SoyException e) {
                    log.error((Object)("Error rendering soy template '" + template.getTemplate() + "' from module '" + template.getTemplatePackage() + "'."), (Throwable)e);
                }
            } else {
                log.error((Object)("Unsupported notification template renderer type '" + (Object)((Object)template.getType()) + "'."));
            }
        }
        StringWriter out = new StringWriter();
        try {
            this.templateRenderer.render("templates/generic.vm", context, (Writer)out);
        }
        catch (IOException e) {
            log.error((Object)"Error rendering generic notification template", (Throwable)e);
            throw new RuntimeException(e);
        }
        return out.toString();
    }
}

