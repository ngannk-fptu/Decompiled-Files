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
public class GoogleMapsRenderer
extends GoogleWidgetRenderer {
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "425px";
    private static final String DEFAULT_HEIGHT = "350px";
    private static final Pattern PATTERN_EMBED = Pattern.compile("google([.a-z]+)/maps/embed\\?pb=(?<locationCode>.+)\\??");
    private static final Pattern PATTERN_SHARE = Pattern.compile("google([.a-z]+)/maps(?<mapInfo>.+)");
    private static final Pattern PATTERN_FULL_PLACE = Pattern.compile("place/(?<placeName>[^/@]+)/");
    private static final Pattern PATTERN_LAT_LONG = Pattern.compile("@?(?<latLong>-?[0-9]+\\.[0-9]+,-?[0-9]+\\.[0-9]+),(?<zoomLevel>[0-9]+)(?<zoomType>[zm])");
    private static final Pattern PATTERN_DATA = Pattern.compile("data=(?<data>[^/&]+)");
    private final VelocityRenderService velocityRenderService;
    private static final String SERVICE_NAME = "GoogleMaps";

    @Autowired
    public GoogleMapsRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        Matcher embedPatternMatcher = PATTERN_EMBED.matcher(url);
        Matcher sharePatternMatcher = PATTERN_SHARE.matcher(url);
        if (embedPatternMatcher.find()) {
            return String.format("https://www.google.com/maps/embed?pb=%s", embedPatternMatcher.group("locationCode"));
        }
        if (sharePatternMatcher.find()) {
            String mapInfo = sharePatternMatcher.group("mapInfo").replace("%40", "@");
            Matcher placeNameMatcher = PATTERN_FULL_PLACE.matcher(mapInfo);
            boolean matchesPlaceName = placeNameMatcher.find();
            Matcher latLongMatcher = PATTERN_LAT_LONG.matcher(mapInfo);
            boolean matchesLatLong = latLongMatcher.find();
            Matcher dataMatcher = PATTERN_DATA.matcher(mapInfo);
            StringBuilder mapsQuery = new StringBuilder();
            if (!matchesPlaceName && !matchesLatLong || mapInfo.contains("q=")) {
                return String.format("https://www.google.com/maps%s&output=embed", sharePatternMatcher.group("mapInfo"));
            }
            if (matchesPlaceName) {
                mapsQuery.append("q=").append(placeNameMatcher.group("placeName"));
            }
            if (matchesLatLong) {
                if (matchesPlaceName) {
                    mapsQuery.append("&");
                }
                String zoomLevel = latLongMatcher.group("zoomType").equals("z") ? latLongMatcher.group("zoomLevel") : "12";
                mapsQuery.append("ll=").append(latLongMatcher.group("latLong")).append("&z=").append(zoomLevel);
            }
            if (dataMatcher.find()) {
                mapsQuery.append("&data=").append(dataMatcher.group("data"));
            }
            return String.format("https://www.google.com/maps?%s&output=embed", mapsQuery.toString());
        }
        return url + "&output=embed";
    }

    @Override
    public boolean matches(String url) {
        if (super.matches(url)) {
            URI uri = URI.create(url.toLowerCase()).normalize();
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && path != null) {
                return host.startsWith("maps.") || path.startsWith("/maps");
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
}

