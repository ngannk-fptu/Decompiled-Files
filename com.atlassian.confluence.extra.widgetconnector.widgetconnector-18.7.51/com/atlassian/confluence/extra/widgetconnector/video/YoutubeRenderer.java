/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.video;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetImagePlaceholder;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.PlaceholderService;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class, WidgetImagePlaceholder.class})
public class YoutubeRenderer
extends AbstractWidgetRenderer
implements WidgetImagePlaceholder {
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("https?://(.+\\.)?youtube.com.*(\\?v=([^&]+)).*$");
    private static final Pattern YOUTUBE_URL_PATTERN_NO_COOKIE = Pattern.compile("https?://(.+\\.)?youtube-nocookie.com/embed/(.*$)");
    private final PlaceholderService placeholderService;
    private static final String DEFAULT_YOUTUBE_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/youtube.vm";
    private static final String DEFAULT_WIDTH = "400px";
    private static final String DEFAULT_HEIGHT = "300px";
    private static final String PIXEL = "px";
    private static final String MED_QUALITY_THUMBNAIL = "mqdefault.jpg";
    private static final String SERVICE_NAME = "YouTube";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public YoutubeRenderer(VelocityRenderService velocityRenderService, PlaceholderService placeholderService) {
        this.velocityRenderService = velocityRenderService;
        this.placeholderService = placeholderService;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        return this.velocityRenderService.render(this.getEmbedUrl(url), this.setDefaultParam(params));
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Nullable
    public String getEmbedUrl(String url) {
        Matcher youtubeUrlMatcher = YOUTUBE_URL_PATTERN.matcher(this.verifyEmbeddedPlayerString(url));
        if (youtubeUrlMatcher.matches()) {
            return String.format("//www.youtube.com/embed/%s?wmode=opaque", youtubeUrlMatcher.group(3));
        }
        Matcher youtubeNoCookieUrlMatcher = YOUTUBE_URL_PATTERN_NO_COOKIE.matcher(url);
        return youtubeNoCookieUrlMatcher.matches() ? String.format("//www.youtube-nocookie.com/embed/%s?wmode=opaque", StringUtils.substringBefore((String)youtubeNoCookieUrlMatcher.group(2), (String)"?")) : null;
    }

    private String verifyEmbeddedPlayerString(String url) {
        return !url.contains("feature=player_embedded&") ? url : url.replace("feature=player_embedded&", "");
    }

    private Map<String, String> setDefaultParam(Map<String, String> params) {
        String width = params.get("width");
        String height = params.get("height");
        if (!params.containsKey("_template")) {
            params.put("_template", DEFAULT_YOUTUBE_TEMPLATE);
        }
        if (StringUtils.isEmpty((CharSequence)width)) {
            params.put("width", DEFAULT_WIDTH);
        } else if (StringUtils.isNumeric((CharSequence)width)) {
            params.put("width", width.concat(PIXEL));
        }
        if (StringUtils.isEmpty((CharSequence)height)) {
            params.put("height", DEFAULT_HEIGHT);
        } else if (StringUtils.isNumeric((CharSequence)height)) {
            params.put("height", height.concat(PIXEL));
        }
        return params;
    }

    private Map<String, String> populateImagePlaceholderParam(Map<String, String> params) {
        Map<String, String> defaultParams = this.setDefaultParam(params);
        defaultParams.put("overlay", "youtube");
        return defaultParams;
    }

    @Nullable
    private String getThumbnailUrl(String url) {
        Matcher youtubeUrlMatcher = YOUTUBE_URL_PATTERN.matcher(url);
        if (youtubeUrlMatcher.matches()) {
            return String.format("http://img.youtube.com/vi/%s/mqdefault.jpg", youtubeUrlMatcher.group(3));
        }
        Matcher youtubeNoCookieUrlMatcher = YOUTUBE_URL_PATTERN_NO_COOKIE.matcher(url);
        return youtubeNoCookieUrlMatcher.matches() ? String.format("http://img.youtube.com/vi/%s/mqdefault.jpg", StringUtils.substringBefore((String)youtubeNoCookieUrlMatcher.group(2), (String)"?")) : null;
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(String url, Map<String, String> params) {
        return this.placeholderService.generatePlaceholder(this.getThumbnailUrl(url), this.populateImagePlaceholderParam(params));
    }
}

