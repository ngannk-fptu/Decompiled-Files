/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.renderer.template;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.renderer.template.TemplateRendererModuleDescriptor;
import com.atlassian.confluence.renderer.template.TemplateRenderingException;
import com.atlassian.plugin.PluginAccessor;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DelegatingTemplateRenderer
implements TemplateRenderer {
    private final PluginAccessor pluginAccessor;

    public DelegatingTemplateRenderer(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public void renderTo(Appendable appendable, String templateProviderPluginKey, String templateName, Map<String, Object> data) throws TemplateRenderingException {
        this.getDelegate(templateName).renderTo(appendable, templateProviderPluginKey, templateName, data);
    }

    @Override
    public Streamable render(String templateProviderPluginKey, String templateName, Map<String, Object> data) throws TemplateRenderingException {
        return this.getDelegate(templateName).render(templateProviderPluginKey, templateName, data);
    }

    @Override
    public void renderTo(Appendable appendable, String templateProviderPluginKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws TemplateRenderingException {
        this.getDelegate(templateName).renderTo(appendable, templateProviderPluginKey, templateName, data, injectedData);
    }

    @Override
    public Streamable render(String templateProviderPluginKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws TemplateRenderingException {
        return this.getDelegate(templateName).render(templateProviderPluginKey, templateName, data, injectedData);
    }

    private TemplateRenderer getDelegate(String templateName) {
        String extension = DelegatingTemplateRenderer.extractFileExtension(templateName);
        TemplateRenderer renderer = null;
        for (TemplateRendererModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(TemplateRendererModuleDescriptor.class)) {
            if (!descriptor.getSupportedFileExtensions().contains(extension)) continue;
            renderer = descriptor.getModule();
        }
        if (renderer == null) {
            throw new TemplateRenderingException("Unable to find template renderer to handle file extension ." + extension);
        }
        return renderer;
    }

    private static String extractFileExtension(String templateName) {
        if (StringUtils.isBlank((CharSequence)templateName)) {
            throw new TemplateRenderingException("Template name was not provided.");
        }
        int i = templateName.lastIndexOf(46);
        if (i < 1 || i > templateName.length() - 2) {
            throw new TemplateRenderingException("Unable to determine file extension of template named: " + templateName);
        }
        return templateName.substring(i + 1);
    }
}

