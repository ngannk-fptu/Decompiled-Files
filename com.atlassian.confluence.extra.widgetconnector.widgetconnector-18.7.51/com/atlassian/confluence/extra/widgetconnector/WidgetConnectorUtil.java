/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageDimensions
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WidgetConnectorUtil {
    public static final Pattern BASEURL_PATTERN = Pattern.compile("^https?://([^/]++)(?:/.*)?");

    public static ImagePlaceholder generateDefaultImagePlaceholder(String baseUrl) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "Widget Connector";
        }
        ImageDimensions dimension = new ImageDimensions(300, 225);
        String command = String.format("%s?%s=%s&%s=%d&%s=%d", "/plugins/servlet/widgetconnector/placeholder", "thumb", baseUrl, "width", dimension.getWidth(), "height", dimension.getHeight());
        return new DefaultImagePlaceholder(command, true, dimension);
    }

    public static String getBaseUrl(String url) {
        Matcher m = BASEURL_PATTERN.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return url;
    }
}

