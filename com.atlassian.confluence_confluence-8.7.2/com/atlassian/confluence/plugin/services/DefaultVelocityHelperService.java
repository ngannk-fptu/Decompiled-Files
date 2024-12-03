/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.plugin.services;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Map;
import org.apache.velocity.context.Context;

@Deprecated(forRemoval=true)
public class DefaultVelocityHelperService
implements VelocityHelperService {
    @Override
    public String getRenderedTemplate(String templateName, Map<String, Object> context) {
        return VelocityUtils.getRenderedTemplate(templateName, context);
    }

    @Override
    public String getRenderedTemplateWithoutSwallowingErrors(String templateName, Map<String, Object> context) throws Exception {
        return VelocityUtils.getRenderedTemplateWithoutSwallowingErrors(templateName, context);
    }

    @Override
    public String getRenderedContent(String templateContent, Map<String, Object> context) {
        return VelocityUtils.getRenderedContent(templateContent, context);
    }

    @Override
    public String getRenderedContent(String templateContent, Context context) {
        return VelocityUtils.getRenderedContent(templateContent, context);
    }

    @Override
    public String getRenderedContentWithoutSwallowingErrors(String templateContent, Context context) throws Exception {
        return this.getRenderedContent(templateContent, context);
    }

    @Override
    public Map<String, Object> createDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

