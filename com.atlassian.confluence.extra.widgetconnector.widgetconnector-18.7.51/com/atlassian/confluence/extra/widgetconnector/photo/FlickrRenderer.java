/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.photo;

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
public class FlickrRenderer
extends AbstractWidgetRenderer {
    public static final Pattern TAGS_PATTERN = Pattern.compile("/photos/tags/([^/]+)/?");
    public static final Pattern ALBUM_PATTERN = Pattern.compile("/photos/([^/]+)/(?:sets|albums)/(\\d+)/?");
    public static final Pattern PATTERN = Pattern.compile("id=([^&]+@[^&]+)&amp;");
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/flickr.vm";
    private static final String USERNAME_PLACEHOLDER = "[USER_NAME]";
    private static final String SET_PLACEHOLDER = "[SET]";
    public static final String SET_URL = "&offsite=true&intl_lang=en-us&page_show_url=%2Fphotos%2F[USER_NAME]%2Fsets%2F[SET]%2Fshow%2F&page_show_back_url=%2Fphotos%2F[USER_NAME]%2Fsets%2F[SET]%2F&set_id=[SET]&jump_to=";
    private static final String TAG_PLACEHOLDER = "[TAG]";
    public static final String TAG_URL = "&offsite=true&intl_lang=en-us&page_show_url=%2Fphotos%2Ftags%2F[TAG]%2Fshow%2F&page_show_back_url=%2Fphotos%2Ftags%2F[TAG]%2F&tags=[TAG]&jump_to=&st[TAG]_index=";
    private static final String SERVICE_NAME = "Flickr";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public FlickrRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        Matcher tagMatcher = TAGS_PATTERN.matcher(url);
        if (tagMatcher.find()) {
            return this.renderTag(tagMatcher);
        }
        Matcher albumMatcher = ALBUM_PATTERN.matcher(url);
        if (albumMatcher.find()) {
            return this.renderSet(albumMatcher);
        }
        return null;
    }

    private String renderSet(Matcher m) {
        return SET_URL.replace(USERNAME_PLACEHOLDER, m.group(1)).replace(SET_PLACEHOLDER, m.group(2));
    }

    private String renderTag(Matcher m) {
        return TAG_URL.replace(TAG_PLACEHOLDER, m.group(1));
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

