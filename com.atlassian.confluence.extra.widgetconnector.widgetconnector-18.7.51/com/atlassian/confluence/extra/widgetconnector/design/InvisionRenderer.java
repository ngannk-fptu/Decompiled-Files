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
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class InvisionRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("//([^/.]+).invisionapp.com/share/([^/]+)");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 360;
    private static final String SERVICE_NAME = "Invision";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public InvisionRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", String.valueOf(640));
        params.putIfAbsent("height", String.valueOf(360));
        return this.velocityRenderService.render(url, params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

