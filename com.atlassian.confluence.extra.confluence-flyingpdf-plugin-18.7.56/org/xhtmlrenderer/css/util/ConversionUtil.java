/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.util;

import java.awt.Color;
import org.w3c.dom.css.RGBColor;

public class ConversionUtil {
    public static Color rgbToColor(RGBColor rgbcol) {
        return new Color(rgbcol.getRed().getFloatValue((short)1) / 255.0f, rgbcol.getGreen().getFloatValue((short)1) / 255.0f, rgbcol.getBlue().getFloatValue((short)1) / 255.0f);
    }
}

