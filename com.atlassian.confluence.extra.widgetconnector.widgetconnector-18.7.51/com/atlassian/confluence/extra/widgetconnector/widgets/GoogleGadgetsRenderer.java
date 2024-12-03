/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.widgets;

import com.atlassian.confluence.extra.widgetconnector.GoogleWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class GoogleGadgetsRenderer
extends GoogleWidgetRenderer {
    private static final Pattern PATTERN = Pattern.compile("url=([^&]+)&?");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/googlegadgets.vm";
    private static final String DEFAULT_WIDTH = "410px";
    private static final String DEFAULT_HEIGHT = "342px";
    private static final String SERVICE_NAME = "GoogleGadgets";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public GoogleGadgetsRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        Matcher m = PATTERN.matcher(url);
        if (m.find()) {
            String gadgetUrl = m.group(1);
            if (!gadgetUrl.startsWith("http")) {
                gadgetUrl = "//".concat(gadgetUrl);
            }
            return gadgetUrl;
        }
        return null;
    }

    @Override
    public boolean matches(String url) {
        URI uri;
        String path;
        if (super.matches(url) && (path = (uri = URI.create(url.toLowerCase()).normalize()).getPath()) != null) {
            return path.startsWith("/ig");
        }
        return false;
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

