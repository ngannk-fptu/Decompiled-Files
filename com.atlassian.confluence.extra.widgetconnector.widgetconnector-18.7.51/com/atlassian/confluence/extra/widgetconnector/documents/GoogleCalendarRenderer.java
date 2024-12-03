/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

import com.atlassian.confluence.extra.widgetconnector.GoogleWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class GoogleCalendarRenderer
extends GoogleWidgetRenderer {
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "800px";
    private static final String DEFAULT_HEIGHT = "600px";
    private static final String SERVICE_NAME = "GoogleCalendar";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public GoogleCalendarRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    @Override
    public boolean matches(String url) {
        if (super.matches(url)) {
            URI uri = URI.create(url.toLowerCase()).normalize();
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && path != null) {
                return host.startsWith("calendar.") || path.startsWith("/calendar");
            }
        }
        return false;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.putIfAbsent("width", DEFAULT_WIDTH);
        params.putIfAbsent("height", DEFAULT_HEIGHT);
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(url, params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

