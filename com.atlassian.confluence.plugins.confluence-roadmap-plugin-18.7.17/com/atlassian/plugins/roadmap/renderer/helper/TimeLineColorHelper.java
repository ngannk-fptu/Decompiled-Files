/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer.helper;

import java.awt.Color;

public class TimeLineColorHelper {
    public static boolean isContrasted(Color c1, Color c2) {
        double L2;
        double L1 = TimeLineColorHelper.getLuminosity(c1);
        double contrast = (L1 + 0.05) / ((L2 = TimeLineColorHelper.getLuminosity(c2)) + 0.05);
        return contrast > 0.5;
    }

    public static Color decodeColor(String color) {
        if (color.startsWith("#")) {
            return new Color(Integer.decode("0x" + color.substring(1)));
        }
        return new Color(Integer.decode("0x" + color));
    }

    private static double getLuminosity(Color color) {
        return 0.2126 * TimeLineColorHelper.getLinearisedColor(color.getRed()) + 0.7152 * TimeLineColorHelper.getLinearisedColor(color.getGreen()) + 0.0722 * TimeLineColorHelper.getLinearisedColor(color.getBlue());
    }

    private static double getLinearisedColor(int component) {
        return Math.pow(component / 255, 2.2);
    }
}

