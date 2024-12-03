/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.PropertyValue;

public class FontSizeHelper {
    private static final LinkedHashMap PROPORTIONAL_FONT_SIZES = new LinkedHashMap();
    private static final LinkedHashMap FIXED_FONT_SIZES = new LinkedHashMap();
    private static final PropertyValue DEFAULT_SMALLER = new PropertyValue(3, 0.8f, "0.8em");
    private static final PropertyValue DEFAULT_LARGER = new PropertyValue(3, 1.2f, "1.2em");

    public static IdentValue getNextSmaller(IdentValue absFontSize) {
        IdentValue prev = null;
        for (IdentValue ident : PROPORTIONAL_FONT_SIZES.keySet()) {
            if (ident == absFontSize) {
                return prev;
            }
            prev = ident;
        }
        return null;
    }

    public static IdentValue getNextLarger(IdentValue absFontSize) {
        Iterator i = PROPORTIONAL_FONT_SIZES.keySet().iterator();
        while (i.hasNext()) {
            IdentValue ident = (IdentValue)i.next();
            if (ident != absFontSize || !i.hasNext()) continue;
            return (IdentValue)i.next();
        }
        return null;
    }

    public static PropertyValue resolveAbsoluteFontSize(IdentValue fontSize, String[] fontFamilies) {
        boolean monospace = FontSizeHelper.isMonospace(fontFamilies);
        if (monospace) {
            return (PropertyValue)FIXED_FONT_SIZES.get(fontSize);
        }
        return (PropertyValue)PROPORTIONAL_FONT_SIZES.get(fontSize);
    }

    public static PropertyValue getDefaultRelativeFontSize(IdentValue fontSize) {
        if (fontSize == IdentValue.LARGER) {
            return DEFAULT_LARGER;
        }
        if (fontSize == IdentValue.SMALLER) {
            return DEFAULT_SMALLER;
        }
        return null;
    }

    private static boolean isMonospace(String[] fontFamilies) {
        for (int i = 0; i < fontFamilies.length; ++i) {
            if (!fontFamilies[i].equals("monospace")) continue;
            return true;
        }
        return false;
    }

    static {
        PROPORTIONAL_FONT_SIZES.put(IdentValue.XX_SMALL, new PropertyValue(5, 9.0f, "9px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.X_SMALL, new PropertyValue(5, 10.0f, "10px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.SMALL, new PropertyValue(5, 13.0f, "13px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.MEDIUM, new PropertyValue(5, 16.0f, "16px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.LARGE, new PropertyValue(5, 18.0f, "18px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.X_LARGE, new PropertyValue(5, 24.0f, "24px"));
        PROPORTIONAL_FONT_SIZES.put(IdentValue.XX_LARGE, new PropertyValue(5, 32.0f, "32px"));
        FIXED_FONT_SIZES.put(IdentValue.XX_SMALL, new PropertyValue(5, 9.0f, "9px"));
        FIXED_FONT_SIZES.put(IdentValue.X_SMALL, new PropertyValue(5, 10.0f, "10px"));
        FIXED_FONT_SIZES.put(IdentValue.SMALL, new PropertyValue(5, 12.0f, "12px"));
        FIXED_FONT_SIZES.put(IdentValue.MEDIUM, new PropertyValue(5, 13.0f, "13px"));
        FIXED_FONT_SIZES.put(IdentValue.LARGE, new PropertyValue(5, 16.0f, "16px"));
        FIXED_FONT_SIZES.put(IdentValue.X_LARGE, new PropertyValue(5, 20.0f, "20px"));
        FIXED_FONT_SIZES.put(IdentValue.XX_LARGE, new PropertyValue(5, 26.0f, "26px"));
    }
}

