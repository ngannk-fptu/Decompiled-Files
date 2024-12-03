/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.internal.Debug;
import org.apache.commons.imaging.palette.ColorCount;
import org.apache.commons.imaging.palette.ColorGroup;
import org.apache.commons.imaging.palette.MedianCut;
import org.apache.commons.imaging.palette.MedianCutPalette;
import org.apache.commons.imaging.palette.Palette;
import org.apache.commons.imaging.palette.SimplePalette;

public class MedianCutQuantizer {
    private final boolean ignoreAlpha;

    public MedianCutQuantizer(boolean ignoreAlpha) {
        this.ignoreAlpha = ignoreAlpha;
    }

    private Map<Integer, ColorCount> groupColors1(BufferedImage image, int max, int mask) {
        HashMap<Integer, ColorCount> colorMap = new HashMap<Integer, ColorCount>();
        int width = image.getWidth();
        int height = image.getHeight();
        int[] row = new int[width];
        for (int y = 0; y < height; ++y) {
            image.getRGB(0, y, width, 1, row, 0, width);
            for (int x = 0; x < width; ++x) {
                ColorCount color;
                int argb = row[x];
                if (this.ignoreAlpha) {
                    argb &= 0xFFFFFF;
                }
                if ((color = (ColorCount)colorMap.get(argb &= mask)) == null) {
                    color = new ColorCount(argb);
                    colorMap.put(argb, color);
                    if (colorMap.keySet().size() > max) {
                        return null;
                    }
                }
                ++color.count;
            }
        }
        return colorMap;
    }

    public Map<Integer, ColorCount> groupColors(BufferedImage image, int maxColors) {
        int max = Integer.MAX_VALUE;
        for (int i = 0; i < 8; ++i) {
            int mask = 0xFF & 255 << i;
            mask = mask | mask << 8 | mask << 16 | mask << 24;
            Debug.debug("mask(" + i + "): " + mask + " (" + Integer.toHexString(mask) + ")");
            Map<Integer, ColorCount> result = this.groupColors1(image, Integer.MAX_VALUE, mask);
            if (result == null) continue;
            return result;
        }
        throw new Error("");
    }

    public Palette process(BufferedImage image, int maxColors, MedianCut medianCut) throws ImageWriteException {
        Map<Integer, ColorCount> colorMap = this.groupColors(image, maxColors);
        int discreteColors = colorMap.keySet().size();
        if (discreteColors <= maxColors) {
            Debug.debug("lossless palette: " + discreteColors);
            int[] palette = new int[discreteColors];
            ArrayList<ColorCount> colorCounts = new ArrayList<ColorCount>(colorMap.values());
            for (int i = 0; i < colorCounts.size(); ++i) {
                ColorCount colorCount = (ColorCount)colorCounts.get(i);
                palette[i] = colorCount.argb;
                if (!this.ignoreAlpha) continue;
                int n = i;
                palette[n] = palette[n] | 0xFF000000;
            }
            return new SimplePalette(palette);
        }
        Debug.debug("discrete colors: " + discreteColors);
        ArrayList<ColorGroup> colorGroups = new ArrayList<ColorGroup>();
        ColorGroup root = new ColorGroup(new ArrayList<ColorCount>(colorMap.values()), this.ignoreAlpha);
        colorGroups.add(root);
        while (colorGroups.size() < maxColors && medianCut.performNextMedianCut(colorGroups, this.ignoreAlpha)) {
        }
        int paletteSize = colorGroups.size();
        Debug.debug("palette size: " + paletteSize);
        int[] palette = new int[paletteSize];
        int i = 0;
        while (i < colorGroups.size()) {
            ColorGroup colorGroup = (ColorGroup)colorGroups.get(i);
            palette[i] = colorGroup.getMedianValue();
            colorGroup.paletteIndex = i++;
            if (!colorGroup.getColorCounts().isEmpty()) continue;
            throw new ImageWriteException("empty color_group: " + colorGroup);
        }
        if (paletteSize > discreteColors) {
            throw new ImageWriteException("palette_size > discrete_colors");
        }
        return new MedianCutPalette(root, palette);
    }
}

