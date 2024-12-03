/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.templaterenderer.TemplateRendererFactory
 */
package com.atlassian.templaterenderer.velocity.one.six;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.TemplateRendererFactory;
import java.util.Map;

@Deprecated
public interface VelocityTemplateRendererFactory
extends TemplateRendererFactory {
    @Deprecated
    public TemplateRenderer getInstance(ClassLoader var1, Map<String, String> var2);

    public TemplateRenderer getInstance(Map<String, String> var1);
}

