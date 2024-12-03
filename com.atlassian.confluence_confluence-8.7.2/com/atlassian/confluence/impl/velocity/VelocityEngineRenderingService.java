/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.DirectiveVelocityContext
 *  com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.app.event.EventCartridge
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.velocity;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.velocity.context.DirectiveVelocityContext;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class VelocityEngineRenderingService
implements VelocityHelperService {
    private static final Logger log = LoggerFactory.getLogger(VelocityEngineRenderingService.class);
    private final EventCartridgeProcessor eventCartridgeProcessor;
    private final GlobalSettingsManager settingsManager;
    private final VelocityEngine velocityEngine;

    public VelocityEngineRenderingService(EventCartridgeProcessor eventCartridgeProcessor, GlobalSettingsManager settingsManager, VelocityEngine velocityEngine) {
        this.eventCartridgeProcessor = Objects.requireNonNull(eventCartridgeProcessor);
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.velocityEngine = Objects.requireNonNull(velocityEngine);
    }

    @Override
    public String getRenderedTemplateWithoutSwallowingErrors(String templateName, Map<String, Object> contextMap) throws Exception {
        DirectiveVelocityContext context = new DirectiveVelocityContext(contextMap);
        this.attachEventCartridge((Context)context);
        StringWriter buffer = new StringWriter(512);
        this.getTemplate(templateName).merge((Context)context, (Writer)buffer);
        return buffer.toString();
    }

    @Override
    public String getRenderedContent(String templateContent, Context context) {
        try {
            return this.getRenderedContentWithoutSwallowingErrors(templateContent, context);
        }
        catch (Exception ex) {
            log.error("Failed to render template content", (Throwable)ex);
            return "";
        }
    }

    @Override
    public String getRenderedContentWithoutSwallowingErrors(String templateContent, Context context) throws Exception {
        this.attachEventCartridge(context);
        StringWriter buffer = new StringWriter(templateContent.length());
        this.velocityEngine.evaluate(context, (Writer)buffer, "getRenderedContent", templateContent);
        return buffer.toString();
    }

    @Override
    public String getRenderedTemplate(String templateName, Map<String, Object> context) {
        try {
            return this.getRenderedTemplateWithoutSwallowingErrors(templateName, context);
        }
        catch (Exception ex) {
            log.error("Failed to render template [{}]", (Object)templateName, (Object)ex);
            return "";
        }
    }

    private void attachEventCartridge(Context context) {
        EventCartridge cartridge = new EventCartridge();
        this.eventCartridgeProcessor.processCartridge(cartridge);
        cartridge.attachToContext(context);
    }

    private Template getTemplate(String templateName) throws Exception {
        return this.velocityEngine.getTemplate(templateName, this.settingsManager.getGlobalSettings().getDefaultEncoding());
    }

    @Override
    public Map<String, Object> createDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

