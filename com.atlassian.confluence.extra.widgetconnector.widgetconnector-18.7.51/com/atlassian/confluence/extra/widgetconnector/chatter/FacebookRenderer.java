/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.chatter;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class FacebookRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN_STORY = Pattern.compile("https://(www.|m.)?facebook.com/story.php\\?story_fbid=(?<postId>[0-9]+)&id=(?<pageId>[0-9]+)");
    public static final Pattern PATTERN_EMBED = Pattern.compile("https://(www.)?facebook.com/plugins/post.php");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "640";
    private static final String DEFAULT_HEIGHT = "360";
    private static final String SERVICE_NAME = "Facebook";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public FacebookRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url, String width, String height) {
        String requestUrl = url;
        Matcher embedUrlMatcher = PATTERN_EMBED.matcher(url);
        if (embedUrlMatcher.find()) {
            return url;
        }
        Matcher storyUrlMatcher = PATTERN_STORY.matcher(url);
        if (storyUrlMatcher.find()) {
            requestUrl = String.format("https://www.facebook.com/%s/posts/%s", storyUrlMatcher.group("pageId"), storyUrlMatcher.group("postId"));
        }
        Object heightQueryString = height == null ? "" : "&height=" + height;
        try {
            return String.format("https://www.facebook.com/plugins/post.php?href=%s&width=%s%s", URLEncoder.encode(requestUrl, "UTF-8"), width, heightQueryString);
        }
        catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.putIfAbsent("width", DEFAULT_WIDTH);
        String userSetHeight = params.getOrDefault("height", null);
        params.putIfAbsent("height", DEFAULT_HEIGHT);
        String embedUrl = this.getEmbedUrl(url, params.get("width"), userSetHeight);
        return this.velocityRenderService.render(embedUrl, params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

