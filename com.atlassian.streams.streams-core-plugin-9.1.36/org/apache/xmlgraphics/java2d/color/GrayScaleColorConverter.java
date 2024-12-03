/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import org.apache.xmlgraphics.java2d.color.ColorConverter;
import org.apache.xmlgraphics.java2d.color.ColorUtil;

public final class GrayScaleColorConverter
implements ColorConverter {
    private static final int RED_WEIGHT = 77;
    private static final int GREEN_WEIGTH = 150;
    private static final int BLUE_WEIGHT = 28;
    private static final GrayScaleColorConverter SINGLETON = new GrayScaleColorConverter();

    private GrayScaleColorConverter() {
    }

    public static GrayScaleColorConverter getInstance() {
        return SINGLETON;
    }

    @Override
    public Color convert(Color color) {
        float kValue = (float)(77 * color.getRed() + 150 * color.getGreen() + 28 * color.getBlue()) / 255.0f / 255.0f;
        return ColorUtil.toCMYKGrayColor(kValue);
    }
}

