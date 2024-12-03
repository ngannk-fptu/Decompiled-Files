/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.video;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class VimeoRenderer
extends AbstractWidgetRenderer {
    private static final String PATTERN = "/(\\d+)&?";
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final int DEFAULT_WIDTH = 960;
    private static final int DEFAULT_HEIGHT = 540;
    private final VelocityRenderService velocityRenderService;
    private static final String SERVICE_NAME = "Vimeo";

    @Autowired
    public VimeoRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", String.valueOf(960));
        params.putIfAbsent("height", String.valueOf(540));
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @VisibleForTesting
    public String getEmbedUrl(String url) {
        Pattern p = Pattern.compile(PATTERN);
        Matcher m = p.matcher(url);
        String videoId = "";
        if (m.find()) {
            videoId = m.group(1);
        }
        return "//player.vimeo.com/video/" + videoId + "?color=ffffff&byline=0&portrait=0&badge=0";
    }
}

