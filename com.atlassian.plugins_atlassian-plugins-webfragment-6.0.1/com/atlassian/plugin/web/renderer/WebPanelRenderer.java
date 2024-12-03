/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.web.renderer;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface WebPanelRenderer {
    public String getResourceType();

    public void render(String var1, Plugin var2, Map<String, Object> var3, Writer var4) throws RendererException, IOException;

    public String renderFragment(String var1, Plugin var2, Map<String, Object> var3) throws RendererException;

    public void renderFragment(Writer var1, String var2, Plugin var3, Map<String, Object> var4) throws RendererException, IOException;
}

