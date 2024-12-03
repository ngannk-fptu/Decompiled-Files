/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.google.common.io.CharStreams
 */
package com.atlassian.plugin.web.renderer;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Map;

public class StaticWebPanelRenderer
implements WebPanelRenderer {
    public static final StaticWebPanelRenderer RENDERER = new StaticWebPanelRenderer();
    public static final String RESOURCE_TYPE = "static";

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE;
    }

    @Override
    public void render(String templateName, Plugin plugin, Map<String, Object> context, Writer writer) throws RendererException, IOException {
        try (InputStreamReader in = new InputStreamReader(this.loadTemplate(plugin, templateName));){
            CharStreams.copy((Readable)in, (Appendable)writer);
        }
    }

    @Override
    public String renderFragment(String fragment, Plugin plugin, Map<String, Object> context) throws RendererException {
        return fragment;
    }

    @Override
    public void renderFragment(Writer writer, String fragment, Plugin plugin, Map<String, Object> context) throws RendererException, IOException {
        writer.write(fragment);
    }

    private InputStream loadTemplate(Plugin plugin, String templateName) throws IOException {
        InputStream in = plugin.getClassLoader().getResourceAsStream(templateName);
        if (in == null && (in = this.getClass().getResourceAsStream(templateName)) == null) {
            throw new RendererException(String.format("Static web panel template %s not found.", templateName));
        }
        return in;
    }
}

