/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.j3d.util.ImageGenerator
 */
package com.atlassian.plugins.roadmap.placeholder;

import com.atlassian.sal.api.message.I18nResolver;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.j3d.util.ImageGenerator;

public class PlaceholderImageFactory {
    private static final String TITLE_LANGUAGE_KEY = "com.atlassian.confluence.plugins.confluence-roadmap-plugin.roadmap.label";
    private static final String ICON_PATH = "images/roadmap.png";
    private static final Color PLACEHOLDER_BACKGROUND = new Color(240, 240, 240);
    private static final Color COLOR_PLACEHOLDER = Color.BLACK;
    private static final int MACRO_ICON_SIZE = 24;
    private static final int HORIZONTAL_PADDING = 14;
    private static final int VERTICAL_PADDING = 14;

    public static void drawPlaceholderImage(Graphics2D graphics, Font font, I18nResolver i18nResolver) {
        float macroTitleTextOffsetX;
        String macroTitle = i18nResolver.getText(TITLE_LANGUAGE_KEY);
        InputStream iconStream = PlaceholderImageFactory.class.getClassLoader().getResourceAsStream(ICON_PATH);
        Font placeholderFont = font.deriveFont(0, 13.0f);
        FontMetrics placeholderFontMetrics = graphics.getFontMetrics(placeholderFont);
        int textHeight = placeholderFontMetrics.getMaxAscent() + placeholderFontMetrics.getMaxDescent();
        int placeholderImageHeight = textHeight + 14;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(PLACEHOLDER_BACKGROUND);
        Image macroIconImage = PlaceholderImageFactory.getIcon(iconStream, 24);
        int macroIconImageWidth = macroIconImage.getWidth(null);
        int macroIconImageHeight = macroIconImage.getHeight(null);
        int macroIconOffsetX = 0;
        int macroIconOffsetY = (placeholderImageHeight - macroIconImageHeight) / 2;
        graphics.drawImage(macroIconImage, macroIconOffsetX, macroIconOffsetY, macroIconImageWidth, macroIconImageHeight, null);
        graphics.setFont(placeholderFont);
        float fontMidlineToBaselineOffset = (float)placeholderFontMetrics.getMaxAscent() - (float)textHeight / 2.0f;
        float baselineOffset = (float)(14 + textHeight) / 2.0f + fontMidlineToBaselineOffset;
        float currentOffsetX = macroTitleTextOffsetX = 31.0f;
        graphics.setColor(COLOR_PLACEHOLDER);
        graphics.drawString(macroTitle, currentOffsetX, baselineOffset);
    }

    private static Image getIcon(InputStream inputStream, int iconSize) {
        BufferedImage result;
        try {
            result = ImageIO.read(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (((Image)result).getWidth(null) > iconSize || ((Image)result).getHeight(null) > iconSize) {
            AreaAveragingScaleFilter scaleFilter = new AreaAveragingScaleFilter(iconSize, iconSize);
            FilteredImageSource filteredImage = new FilteredImageSource(((Image)result).getSource(), scaleFilter);
            ImageGenerator generator = new ImageGenerator();
            filteredImage.startProduction((ImageConsumer)generator);
            result = generator.getImage();
            result.flush();
        }
        return result;
    }
}

