/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageDimensions
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.extra.widgetconnector.WidgetConnectorUtil;
import com.atlassian.confluence.extra.widgetconnector.services.PlaceholderService;
import com.atlassian.confluence.extra.widgetconnector.validation.ThumbnailPlaceholderUrlValidator;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PlaceholderService.class})
public class DefaultPlaceholderService
implements PlaceholderService {
    public static final String PLACEHOLDER_SERVLET = "/plugins/servlet/widgetconnector/placeholder";
    public static final String PARAM_THUMB_URL = "thumb";
    public static final String PARAM_OVERLAY_URL = "overlay";
    public static final String PARAM_WIDTH = "width";
    public static final String PARAM_HEIGHT = "height";
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;
    private static final Logger log = LoggerFactory.getLogger(DefaultPlaceholderService.class);
    private final ThumbnailPlaceholderUrlValidator thumbnailPlaceholderUrlValidator;

    @Autowired
    public DefaultPlaceholderService(ThumbnailPlaceholderUrlValidator thumbnailPlaceholderUrlValidator) {
        this.thumbnailPlaceholderUrlValidator = thumbnailPlaceholderUrlValidator;
    }

    @Override
    public ImagePlaceholder generatePlaceholder(String thumbUrl, Map<String, String> params) {
        int height;
        int width;
        if (params == null) {
            log.warn("Invalid parameters map. Reverting to default mode.");
            return WidgetConnectorUtil.generateDefaultImagePlaceholder(thumbUrl);
        }
        String widthStr = params.get(PARAM_WIDTH);
        String heightStr = params.get(PARAM_HEIGHT);
        String overlayUrl = params.get(PARAM_OVERLAY_URL);
        if (thumbUrl == null || widthStr == null || heightStr == null || overlayUrl == null || thumbUrl.isEmpty() || widthStr.isEmpty() || heightStr.isEmpty() || overlayUrl.isEmpty()) {
            log.warn("Invalid url or width or height or placeholder parameters. Reverting to default mode.");
            return WidgetConnectorUtil.generateDefaultImagePlaceholder(thumbUrl);
        }
        if (!this.thumbnailPlaceholderUrlValidator.isValid(thumbUrl, overlayUrl)) {
            log.warn("Thumbnail url is not a valid http url. Reverting to default mode.");
            return WidgetConnectorUtil.generateDefaultImagePlaceholder(null);
        }
        try {
            width = Integer.parseInt(widthStr.replace("px", ""));
            height = Integer.parseInt(heightStr.replace("px", ""));
        }
        catch (NumberFormatException e) {
            log.warn("Invalid width or height values. Reverting to default mode.");
            return WidgetConnectorUtil.generateDefaultImagePlaceholder(thumbUrl);
        }
        ImageDimensions dimension = this.restrictDimensions(width, height);
        String command = this.createThumbnailRedirectCommand(thumbUrl, overlayUrl, dimension);
        return new DefaultImagePlaceholder(command, true, dimension);
    }

    private String createThumbnailRedirectCommand(String thumbUrl, String overlayUrl, ImageDimensions d) {
        return String.format("%s?%s=%s&%s=%s&%s=%d&%s=%d", PLACEHOLDER_SERVLET, PARAM_THUMB_URL, thumbUrl, PARAM_OVERLAY_URL, overlayUrl, PARAM_WIDTH, d.getWidth(), PARAM_HEIGHT, d.getHeight());
    }

    private ImageDimensions restrictDimensions(int width, int height) {
        return new ImageDimensions(Math.min(800, width), Math.min(600, height));
    }
}

