/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRendererFactory
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.streams.spi.renderer;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRendererFactory;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class CachingTemplateRenderer
implements TemplateRenderer {
    private final Supplier<TemplateRenderer> renderer;

    public CachingTemplateRenderer(final VelocityTemplateRendererFactory factory) {
        this.renderer = Suppliers.memoize((Supplier)new Supplier<TemplateRenderer>(){

            public TemplateRenderer get() {
                return factory.getInstance((Map)ImmutableMap.of((Object)"classpath.resource.loader.cache", (Object)Boolean.toString(!Boolean.getBoolean("atlassian.dev.mode"))));
            }
        });
    }

    public void render(String templateName, Map<String, Object> context, Writer writer) throws RenderingException, IOException {
        ((TemplateRenderer)this.renderer.get()).render(templateName, context, writer);
    }

    public void render(String templateName, Writer writer) throws RenderingException, IOException {
        ((TemplateRenderer)this.renderer.get()).render(templateName, writer);
    }

    public String renderFragment(String fragment, Map<String, Object> context) throws RenderingException {
        return ((TemplateRenderer)this.renderer.get()).renderFragment(fragment, context);
    }

    public boolean resolve(String templateName) {
        return ((TemplateRenderer)this.renderer.get()).resolve(templateName);
    }
}

