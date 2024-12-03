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
public class WufooRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("http://([^.]+)\\.wufoo\\.com/([^/]+)/([^/]+)");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "100%";
    private static final String DEFAULT_HEIGHT = "500px";
    private static final String SERVICE_NAME = "Wufoo";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public WufooRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        Matcher m = PATTERN.matcher(url);
        if (m.find()) {
            String userId = m.group(1);
            String type = m.group(2);
            String id = m.group(3);
            if ("reports".equals(type)) {
                return "//" + userId + ".wufoo.com/reports/" + id + "/";
            }
            return "//" + userId + ".wufoo.com/embed/" + id + "/";
        }
        return null;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.putIfAbsent("width", DEFAULT_WIDTH);
        params.putIfAbsent("height", DEFAULT_HEIGHT);
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

