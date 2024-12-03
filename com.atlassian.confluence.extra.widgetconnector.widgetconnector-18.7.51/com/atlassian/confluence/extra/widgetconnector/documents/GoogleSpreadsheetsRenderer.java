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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class GoogleSpreadsheetsRenderer
extends GoogleWidgetRenderer {
    private static final Pattern PATTERN = Pattern.compile("key=([^&]+)");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "720px";
    private static final String DEFAULT_HEIGHT = "360px";
    private static final String SERVICE_NAME = "GoogleSpreadsheets";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public GoogleSpreadsheetsRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        String embeddedUrl = this.matchWithOldSpreadsheetUrl(url);
        if (embeddedUrl == null) {
            embeddedUrl = this.matchWithNewSpreadsheetUrl(url);
        }
        return embeddedUrl;
    }

    @Override
    public boolean matches(String url) {
        if (super.matches(url)) {
            URI uri = URI.create(url.toLowerCase()).normalize();
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && path != null) {
                return host.startsWith("docs.") && path.startsWith("/spreadsheet") || host.startsWith("docs.") && path.contains("/spreadsheet/") || host.startsWith("spreadsheets.");
            }
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

    private String matchWithOldSpreadsheetUrl(String url) {
        StringBuilder embeddedUrl = new StringBuilder("//docs.google.com/spreadsheet/pub?");
        Matcher m = PATTERN.matcher(url);
        if (!m.find()) {
            return null;
        }
        String key = m.group(1);
        embeddedUrl.append("key=").append(key);
        embeddedUrl.append("&output=html&widget=true&element=true&gid=0");
        return embeddedUrl.toString();
    }

    private String matchWithNewSpreadsheetUrl(String url) {
        if (url == null || !url.contains("/pubhtml") && !url.contains("/pubchart")) {
            return null;
        }
        boolean hasQueryParams = url.indexOf(63) > 0;
        return url + (hasQueryParams ? "&" : "?") + "widget=true&headers=false&chrome=false";
    }
}

