/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.confluence.extra.widgetconnector.validation.ThumbnailPlaceholderUrlValidator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageGeneratorServlet
extends HttpServlet {
    private static final String RESOURCE_FOLDER = "com/atlassian/confluence/extra/widgetconnector/";
    private static final String WIDGET_LOGO_RESOURCE = "com/atlassian/confluence/extra/widgetconnector/templates/widget.png";
    private static final float OVERLAY_MAX_WIDTH = 0.28f;
    private static final float OVERLAY_PADDING = 0.03f;
    private static final int DEFAULT_PLACEHOLDER_MAX_NAME_LENGTH = 25;
    private static final int DEFAULT_WIDTH = 300;
    private static final int MAX_WIDTH = 5000;
    private static final Logger log = LoggerFactory.getLogger(ImageGeneratorServlet.class);
    private final PermissionManager permissionManager;
    private final ThumbnailPlaceholderUrlValidator thumbnailPlaceholderUrlValidator;

    public ImageGeneratorServlet(@ComponentImport PermissionManager permissionManager, ThumbnailPlaceholderUrlValidator thumbnailPlaceholderUrlValidator) {
        this.permissionManager = permissionManager;
        this.thumbnailPlaceholderUrlValidator = thumbnailPlaceholderUrlValidator;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403);
            return;
        }
        String thumbUrl = req.getParameter("thumb");
        String overlayUrl = req.getParameter("overlay");
        int width = NumberUtils.toInt((String)req.getParameter("width"));
        int n = width = width > 0 && width <= 5000 ? width : 300;
        if (overlayUrl != null) {
            overlayUrl = overlayUrl.toLowerCase();
        }
        BufferedImage placeholder = null;
        if (this.thumbnailPlaceholderUrlValidator.isValid(thumbUrl, overlayUrl)) {
            placeholder = this.createThumbnailPlaceholder(thumbUrl, overlayUrl, width);
        }
        if (placeholder == null) {
            placeholder = this.createDefaultPlaceholder("", width);
        }
        resp.setContentType("image/png");
        ImageIO.write((RenderedImage)placeholder, "png", (OutputStream)resp.getOutputStream());
    }

    private BufferedImage createDefaultPlaceholder(String baseUrl, int width) throws IOException {
        ClassLoader loader = ImageGeneratorServlet.class.getClassLoader();
        InputStream widgetLogoIn = loader.getResourceAsStream(WIDGET_LOGO_RESOURCE);
        BufferedImage widgetLogo = ImageIO.read(widgetLogoIn);
        int thumbWidth = width;
        int padding = (int)((float)thumbWidth * 0.03f);
        int thumbHeight = widgetLogo.getHeight() + 2 * padding;
        BufferedImage placeholder = new BufferedImage(thumbWidth, thumbHeight, 2);
        Graphics2D g = this.getHighQualityGraphicsRenderMode(placeholder);
        Font font = new Font("Arial", 0, 12);
        g.setFont(font);
        g.setColor(Color.DARK_GRAY);
        if (((String)baseUrl).length() > 25) {
            baseUrl = ((String)baseUrl).substring(0, 25);
            baseUrl = (String)baseUrl + "...";
        }
        int txtWidth = g.getFontMetrics().stringWidth((String)baseUrl);
        int posX = padding;
        int posY = padding;
        g.drawImage(widgetLogo, posX, posY, widgetLogo.getWidth(), widgetLogo.getHeight(), null);
        int widgetFullWidth = widgetLogo.getWidth() + 2 * padding;
        int offsetX = (int)((double)(thumbWidth - widgetFullWidth) / 2.0) + widgetFullWidth;
        int txtPosX = offsetX - (int)((double)txtWidth / 2.0);
        int txtPosY = (int)((double)thumbHeight / 2.0);
        g.drawString((String)baseUrl, txtPosX, txtPosY);
        g.dispose();
        return placeholder;
    }

    @VisibleForTesting
    BufferedImage createThumbnailPlaceholder(String thumbUrl, String overlayUrl, int width) {
        int overlayPadY;
        int overlayPadX;
        int overlayHeight;
        int overlayWidth;
        int thumbHeight;
        int thumbWidth;
        BufferedImage shade;
        BufferedImage overlay;
        ClassLoader loader = ImageGeneratorServlet.class.getClassLoader();
        InputStream overlayIn = loader.getResourceAsStream("com/atlassian/confluence/extra/widgetconnector/logos/" + overlayUrl + ".png");
        InputStream shadeIn = loader.getResourceAsStream("com/atlassian/confluence/extra/widgetconnector/logos/shade.png");
        if (shadeIn == null || overlayIn == null) {
            log.warn("Cannot load resource for shade and/or overlay. Reverting to default mode.");
            return null;
        }
        BufferedImage thumbnail = null;
        boolean genericOverlay = false;
        try {
            thumbnail = ImageIO.read(new URL(thumbUrl));
        }
        catch (IOException e) {
            genericOverlay = true;
            log.warn("Reverting to generic overlay mode. Thumbnail can not be loaded from: " + thumbUrl);
        }
        if (thumbnail == null) {
            genericOverlay = true;
        }
        try {
            overlay = ImageIO.read(overlayIn);
            shade = ImageIO.read(shadeIn);
        }
        catch (IOException e) {
            log.warn("Cannot read images from resource. Reverting to default mode.");
            return null;
        }
        if (genericOverlay) {
            thumbWidth = width;
            thumbHeight = (int)((double)thumbWidth * ((double)overlay.getHeight() / (double)overlay.getWidth()));
            overlayWidth = (int)((float)overlay.getWidth() * 0.28f) * 2;
            overlayHeight = (int)((double)overlayWidth * ((double)overlay.getHeight() / (double)overlay.getWidth()));
            overlayPadX = (int)((double)thumbWidth / 2.0 - (double)overlayWidth / 2.0);
            overlayPadY = (int)((double)thumbHeight / 2.0 - (double)overlayHeight / 2.0);
        } else {
            thumbWidth = width;
            thumbHeight = (int)((double)thumbWidth * ((double)thumbnail.getHeight() / (double)thumbnail.getWidth()));
            overlayWidth = (int)((float)overlay.getWidth() * 0.28f);
            overlayHeight = (int)((double)overlayWidth * ((double)overlay.getHeight() / (double)overlay.getWidth()));
            overlayPadX = (int)((float)thumbWidth * 0.03f);
            overlayPadY = (int)((float)thumbHeight * 0.03f);
            overlayPadX = thumbWidth - overlayWidth - overlayPadX;
            overlayPadY = thumbHeight - overlayHeight - overlayPadY;
        }
        BufferedImage placeholder = new BufferedImage(thumbWidth, thumbHeight, 2);
        Graphics2D g = this.getHighQualityGraphicsRenderMode(placeholder);
        if (!genericOverlay) {
            g.drawImage(thumbnail, 0, 0, thumbWidth, thumbHeight, null);
            g.drawImage(shade, 0, 0, thumbWidth, thumbHeight, null);
        }
        g.drawImage(overlay, overlayPadX, overlayPadY, overlayWidth, overlayHeight, null);
        g.dispose();
        return placeholder;
    }

    private Graphics2D getHighQualityGraphicsRenderMode(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }
}

