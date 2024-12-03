/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.j3d.util.ImageGenerator
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.tinymceplugin.FontProvider;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderImageFactory;
import com.atlassian.confluence.tinymceplugin.placeholder.StyledString;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import org.j3d.util.ImageGenerator;

public class DefaultPlaceholderImageFactory
implements PlaceholderImageFactory {
    private static final int MACRO_ICON_SIZE = 24;
    private static final int HEADING_WIDTH = 2560;
    private final FontProvider fontProvider;

    public DefaultPlaceholderImageFactory(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        System.setProperty("sun.java2d.opengl", "true");
        ImageIO.setUseCache(false);
    }

    @Override
    public BufferedImage getPlaceholderImage(StyledString styledString, InputStream iconStream, Color backgroundColor) {
        return this.getPlaceholderImage(Collections.singletonList(styledString), iconStream, backgroundColor);
    }

    @Override
    public BufferedImage getPlaceholderImage(StyledString styledString, InputStream iconStream, Color backgroundColor, Color borderColor, boolean roundedCorner) {
        return this.getPlaceholderImage(Collections.singletonList(styledString), iconStream, backgroundColor, borderColor, roundedCorner);
    }

    @Override
    public BufferedImage getPlaceholderImage(List<StyledString> styledStrings, InputStream iconStream, Color backgroundColor) {
        return this.getPlaceholderImage(styledStrings, iconStream, backgroundColor, null, false);
    }

    @Override
    public BufferedImage getPlaceholderImage(List<StyledString> styledStrings, InputStream iconStream, Color backgroundColor, Color borderColor, boolean roundedCorner) {
        float macroTitleTextOffsetX;
        Font font = this.getFont();
        FontMetrics metrics = this.getFontMetrics(font);
        int textHeight = metrics.getMaxAscent() + metrics.getMaxDescent();
        int textWidth = 0;
        for (StyledString styledString : styledStrings) {
            textWidth += metrics.stringWidth(styledString.getValue());
        }
        int horizontalPadding = 15;
        int verticalPadding = 15;
        int iconToTextSpacing = 5;
        int cornerRadius = roundedCorner ? 10 : 0;
        int placeholderImageWidth = textWidth + horizontalPadding + 24 + iconToTextSpacing;
        int placeholderImageHeight = textHeight + verticalPadding;
        BufferedImage bufferedImage = new BufferedImage(placeholderImageWidth, placeholderImageHeight, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(backgroundColor);
        RoundRectangle2D.Float roundedRectangle = new RoundRectangle2D.Float(0.0f, 0.0f, placeholderImageWidth, placeholderImageHeight, cornerRadius, cornerRadius);
        graphics.fill(roundedRectangle);
        if (borderColor != null) {
            graphics.setColor(borderColor);
            RoundRectangle2D.Float borderRectangle = new RoundRectangle2D.Float(0.0f, 0.0f, placeholderImageWidth - 1, placeholderImageHeight - 1, 10.0f, 10.0f);
            graphics.draw(borderRectangle);
        }
        Image macroIconImage = this.getIcon(iconStream, 24);
        int macroIconImageWidth = macroIconImage.getWidth(null);
        int macroIconImageHeight = macroIconImage.getHeight(null);
        int macroIconOffsetX = (placeholderImageHeight - macroIconImageWidth) / 2;
        int macroIconOffsetY = (placeholderImageHeight - macroIconImageHeight) / 2;
        graphics.drawImage(macroIconImage, macroIconOffsetX, macroIconOffsetY, macroIconImageWidth, macroIconImageHeight, null);
        graphics.setFont(font);
        float fontMidlineToBaselineOffset = (float)metrics.getMaxAscent() - (float)textHeight / 2.0f;
        float baselineOffset = (float)(verticalPadding + textHeight) / 2.0f + fontMidlineToBaselineOffset;
        float currentOffsetX = macroTitleTextOffsetX = (float)horizontalPadding / 2.0f + 24.0f;
        for (int i = 0; i < styledStrings.size(); ++i) {
            StyledString styledString = styledStrings.get(i);
            graphics.setColor(styledString.getColor());
            graphics.drawString(styledString.getValue(), currentOffsetX, baselineOffset);
            if (i == styledStrings.size() - 1) continue;
            currentOffsetX += (float)metrics.stringWidth(styledString.getValue());
        }
        graphics.dispose();
        return bufferedImage;
    }

    @Override
    public BufferedImage getPlaceholderImage(InputStream iconStream, int sizeInPixels) {
        BufferedImage bufferedImage = new BufferedImage(sizeInPixels, sizeInPixels, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(0, 0, 0, 0));
        graphics.fillRect(0, 0, sizeInPixels, sizeInPixels);
        Image macroIconImage = this.getIcon(iconStream, 24);
        int macroIconImageWidth = macroIconImage.getWidth(null);
        int macroIconImageHeight = macroIconImage.getHeight(null);
        int macroIconOffsetX = (sizeInPixels - macroIconImageWidth) / 2;
        int macroIconOffsetY = (sizeInPixels - macroIconImageHeight) / 2;
        graphics.drawImage(macroIconImage, macroIconOffsetX, macroIconOffsetY, macroIconImageWidth, macroIconImageHeight, null);
        graphics.dispose();
        return bufferedImage;
    }

    @Override
    public BufferedImage getPlaceholderHeading(List<StyledString> styledStrings, InputStream iconStream, Color backgroundColor) {
        float macroTitleTextOffsetX;
        Font font = this.getFont();
        FontMetrics metrics = this.getFontMetrics(font);
        int textHeight = metrics.getMaxAscent() + metrics.getMaxDescent();
        int horizontalPadding = 10;
        int verticalPadding = 10;
        int placeholderImageHeight = textHeight + verticalPadding;
        BufferedImage bufferedImage = new BufferedImage(2560, placeholderImageHeight, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, 2560, placeholderImageHeight);
        Image macroIconImage = this.getIcon(iconStream, 24);
        int macroIconImageWidth = macroIconImage.getWidth(null);
        int macroIconImageHeight = macroIconImage.getHeight(null);
        int macroIconOffsetX = (placeholderImageHeight - macroIconImageWidth) / 2;
        int macroIconOffsetY = (placeholderImageHeight - macroIconImageHeight) / 2;
        graphics.drawImage(macroIconImage, macroIconOffsetX, macroIconOffsetY, macroIconImageWidth, macroIconImageHeight, null);
        graphics.setFont(font);
        float fontMidlineToBaselineOffset = (float)metrics.getMaxAscent() - (float)textHeight / 2.0f;
        float baselineOffset = (float)(verticalPadding + textHeight) / 2.0f + fontMidlineToBaselineOffset;
        float currentOffsetX = macroTitleTextOffsetX = (float)horizontalPadding / 2.0f + 24.0f;
        for (int i = 0; i < styledStrings.size(); ++i) {
            StyledString styledString = styledStrings.get(i);
            graphics.setColor(styledString.getColor());
            graphics.drawString(styledString.getValue(), currentOffsetX, baselineOffset);
            if (i == styledStrings.size() - 1) continue;
            currentOffsetX += (float)metrics.stringWidth(styledString.getValue());
        }
        graphics.dispose();
        return bufferedImage;
    }

    private Image getIcon(InputStream inputStream, int iconSize) {
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

