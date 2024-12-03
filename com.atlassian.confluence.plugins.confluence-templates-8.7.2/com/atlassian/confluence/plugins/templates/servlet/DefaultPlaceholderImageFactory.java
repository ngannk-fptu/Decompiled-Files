/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.templates.servlet;

import com.atlassian.confluence.plugins.templates.servlet.FontProvider;
import com.atlassian.confluence.plugins.templates.servlet.PlaceholderImageFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class DefaultPlaceholderImageFactory
implements PlaceholderImageFactory {
    private static final Color BACKGROUND_COLOUR = new Color(87, 148, 50);
    private static final Color FOREGROUND_COLOUR = Color.WHITE;
    private final FontProvider fontProvider;

    public DefaultPlaceholderImageFactory(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        System.setProperty("sun.java2d.opengl", "true");
        ImageIO.setUseCache(false);
    }

    @Override
    public BufferedImage getPlaceholderImage(String variableName) {
        Font font = this.getFont();
        FontMetrics metrics = this.getFontMetrics(font);
        int textHeight = metrics.getMaxAscent() + metrics.getMaxDescent();
        int textWidth = metrics.stringWidth(variableName);
        int horizontalPadding = 3;
        int verticalPadding = 0;
        int placeholderImageWidth = textWidth + horizontalPadding * 2;
        int placeholderImageHeight = textHeight + verticalPadding;
        BufferedImage bufferedImage = new BufferedImage(placeholderImageWidth, placeholderImageHeight, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(BACKGROUND_COLOUR);
        graphics.fillRect(0, 0, placeholderImageWidth, placeholderImageHeight);
        graphics.setFont(font);
        float fontMidlineToBaselineOffset = (float)metrics.getMaxAscent() - (float)textHeight / 2.0f;
        float baselineOffset = (float)(verticalPadding + textHeight) / 2.0f + fontMidlineToBaselineOffset;
        graphics.setColor(FOREGROUND_COLOUR);
        graphics.drawString(variableName, (float)horizontalPadding, baselineOffset);
        graphics.dispose();
        return bufferedImage;
    }

    private FontMetrics getFontMetrics(Font font) {
        BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        graphics.dispose();
        return fontMetrics;
    }

    private Font getFont() {
        return this.fontProvider.getFirstAvailableFont("SansSerif");
    }
}

