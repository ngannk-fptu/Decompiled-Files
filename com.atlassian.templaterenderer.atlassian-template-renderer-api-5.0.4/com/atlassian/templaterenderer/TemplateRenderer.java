/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.templaterenderer;

import com.atlassian.templaterenderer.RenderingException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateRenderer {
    public void render(String var1, Writer var2) throws RenderingException, IOException;

    public void render(String var1, Map<String, Object> var2, Writer var3) throws RenderingException, IOException;

    public String renderFragment(String var1, Map<String, Object> var2) throws RenderingException;

    public boolean resolve(String var1);
}

