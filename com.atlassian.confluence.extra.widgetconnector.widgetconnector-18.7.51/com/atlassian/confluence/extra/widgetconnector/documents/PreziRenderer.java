/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

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
public class PreziRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("https://prezi.com/(?<type>(p|v|i|view))/(?<id>[^/]+)/?");
    public static final Pattern PATTERN_OLD_LINK = Pattern.compile("https://prezi.com/(?<id>[^/]+)/?");
    public static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "640";
    private static final String DEFAULT_HEIGHT = "360";
    private static final String SERVICE_NAME = "Prezi";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public PreziRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        if (url.contains("embed")) {
            return url;
        }
        Matcher matcher = PATTERN.matcher(url);
        if (!matcher.find()) {
            Matcher oldUrlMatcher = PATTERN_OLD_LINK.matcher(url);
            if (!oldUrlMatcher.find()) {
                return null;
            }
            return String.format("https://prezi.com/embed/%s", oldUrlMatcher.group("id"));
        }
        String contentType = matcher.group("type");
        if (contentType.equals("v")) {
            return String.format("https://prezi.com/v/embed/%s", matcher.group("id"));
        }
        return String.format("https://prezi.com/%s/%s/embed", contentType, matcher.group("id"));
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", DEFAULT_WIDTH);
        params.putIfAbsent("height", DEFAULT_HEIGHT);
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

