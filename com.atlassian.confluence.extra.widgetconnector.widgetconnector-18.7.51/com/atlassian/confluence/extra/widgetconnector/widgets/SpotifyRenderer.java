/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.widgets;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class SpotifyRenderer
extends AbstractWidgetRenderer {
    private static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("^https://open.spotify.com/(embed/)?(?<embed>(playlist|track|album|artist)/([A-Za-z0-9]){22})(\\?si=([A-Za-z0-9\\-_]){22})?$");
    private final SoyTemplateRenderer soyTemplateRenderer;
    private static final String SERVICE_NAME = "Spotify";

    @Autowired
    public SpotifyRenderer(@ComponentImport SoyTemplateRenderer soyTemplateRenderer) {
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    @Override
    @Nullable
    public String getEmbeddedHtml(String url, Map<String, String> parameters) {
        Matcher matcher = SPOTIFY_URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        String width = parameters.get("width");
        String height = parameters.get("height");
        ImmutableMap data = ImmutableMap.builder().put((Object)"width", (Object)StringUtils.defaultString((String)width, (String)"300")).put((Object)"height", (Object)StringUtils.defaultString((String)height, (String)"380")).put((Object)"url", (Object)String.format("https://open.spotify.com/embed/%s", matcher.group("embed"))).build();
        return this.soyTemplateRenderer.render("com.atlassian.confluence.extra.widgetconnector:soy-templates", "Confluence.Widget.Connector.spotify", (Map)data);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

