/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.video;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class MicrosoftStreamRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("web.microsoftstream.com/video/([a-zA-Z0-9-]+)");
    public static final Pattern PATTERN_EMBED = Pattern.compile("web.microsoftstream.com/embed/video/([a-zA-Z0-9-]+)");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 360;
    private static final String SERVICE_NAME = "MicrosoftStream";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public MicrosoftStreamRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        Matcher embedUrlMatcher = PATTERN_EMBED.matcher(url);
        if (embedUrlMatcher.find()) {
            return url;
        }
        return url.replaceFirst("/video/", "/embed/video/") + "?autoplay=false&amp;showinfo=true";
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", String.valueOf(640));
        params.putIfAbsent("height", String.valueOf(360));
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

