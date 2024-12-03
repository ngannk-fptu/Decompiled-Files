/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import org.apache.poi.hwpf.converter.FontReplacer;

public class DefaultFontReplacer
implements FontReplacer {
    @Override
    public FontReplacer.Triplet update(FontReplacer.Triplet original) {
        if (DefaultFontReplacer.isNotEmpty(original.fontName)) {
            String fontName = original.fontName;
            if (fontName.endsWith(" Regular")) {
                fontName = DefaultFontReplacer.substringBeforeLast(fontName, " Regular");
            }
            if (fontName.endsWith(" \u041f\u043e\u043b\u0443\u0436\u0438\u0440\u043d\u044b\u0439")) {
                fontName = DefaultFontReplacer.substringBeforeLast(fontName, " \u041f\u043e\u043b\u0443\u0436\u0438\u0440\u043d\u044b\u0439") + " Bold";
            }
            if (fontName.endsWith(" \u041f\u043e\u043b\u0443\u0436\u0438\u0440\u043d\u044b\u0439 \u041a\u0443\u0440\u0441\u0438\u0432")) {
                fontName = DefaultFontReplacer.substringBeforeLast(fontName, " \u041f\u043e\u043b\u0443\u0436\u0438\u0440\u043d\u044b\u0439 \u041a\u0443\u0440\u0441\u0438\u0432") + " Bold Italic";
            }
            if (fontName.endsWith(" \u041a\u0443\u0440\u0441\u0438\u0432")) {
                fontName = DefaultFontReplacer.substringBeforeLast(fontName, " \u041a\u0443\u0440\u0441\u0438\u0432") + " Italic";
            }
            original.fontName = fontName;
        }
        if (DefaultFontReplacer.isNotEmpty(original.fontName)) {
            if ("Times Regular".equals(original.fontName) || "Times-Regular".equals(original.fontName) || "Times Roman".equals(original.fontName)) {
                original.fontName = "Times";
                original.bold = false;
                original.italic = false;
            }
            if ("Times Bold".equals(original.fontName) || "Times-Bold".equals(original.fontName)) {
                original.fontName = "Times";
                original.bold = true;
                original.italic = false;
            }
            if ("Times Italic".equals(original.fontName) || "Times-Italic".equals(original.fontName)) {
                original.fontName = "Times";
                original.bold = false;
                original.italic = true;
            }
            if ("Times Bold Italic".equals(original.fontName) || "Times-BoldItalic".equals(original.fontName)) {
                original.fontName = "Times";
                original.bold = true;
                original.italic = true;
            }
        }
        return original;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static boolean isNotEmpty(String str) {
        return !DefaultFontReplacer.isEmpty(str);
    }

    private static String substringBeforeLast(String str, String separator) {
        if (DefaultFontReplacer.isEmpty(str) || DefaultFontReplacer.isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
}

