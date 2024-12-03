/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.design;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class FigmaRenderer
extends AbstractWidgetRenderer {
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 450;
    private static final String SERVICE_NAME = "Figma";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public FigmaRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        try {
            return "https://www.figma.com/embed?embed_host=confluence&url=" + URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", String.valueOf(800));
        params.putIfAbsent("height", String.valueOf(450));
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

