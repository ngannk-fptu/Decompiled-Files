/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.template;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.renderer.template.TemplateRenderingException;
import java.util.Map;

public interface TemplateRenderer {
    public void renderTo(Appendable var1, String var2, String var3, Map<String, Object> var4) throws TemplateRenderingException;

    public Streamable render(String var1, String var2, Map<String, Object> var3) throws TemplateRenderingException;

    public void renderTo(Appendable var1, String var2, String var3, Map<String, Object> var4, Map<String, Object> var5) throws TemplateRenderingException;

    public Streamable render(String var1, String var2, Map<String, Object> var3, Map<String, Object> var4) throws TemplateRenderingException;
}

