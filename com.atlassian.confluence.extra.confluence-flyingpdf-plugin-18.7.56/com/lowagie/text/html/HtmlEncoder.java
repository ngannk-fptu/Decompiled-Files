/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html;

import java.awt.Color;

public final class HtmlEncoder {
    private static final String[] htmlCode;

    private HtmlEncoder() {
    }

    public static String encode(String string) {
        int n = string.length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            char character = string.charAt(i);
            if (character < '\u0100') {
                buffer.append(htmlCode[character]);
                continue;
            }
            buffer.append("&#").append((int)character).append(';');
        }
        return buffer.toString();
    }

    public static String encode(Color color) {
        StringBuilder buffer = new StringBuilder("#");
        if (color.getRed() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getRed(), 16));
        if (color.getGreen() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getGreen(), 16));
        if (color.getBlue() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getBlue(), 16));
        return buffer.toString();
    }

    public static String getAlignment(int alignment) {
        switch (alignment) {
            case 0: {
                return "Left";
            }
            case 1: {
                return "Center";
            }
            case 2: {
                return "Right";
            }
            case 3: 
            case 8: {
                return "Justify";
            }
            case 4: {
                return "Top";
            }
            case 5: {
                return "Middle";
            }
            case 6: {
                return "Bottom";
            }
            case 7: {
                return "Baseline";
            }
        }
        return "";
    }

    static {
        int i;
        htmlCode = new String[256];
        for (i = 0; i < 10; ++i) {
            HtmlEncoder.htmlCode[i] = "&#00" + i + ";";
        }
        for (i = 10; i < 32; ++i) {
            HtmlEncoder.htmlCode[i] = "&#0" + i + ";";
        }
        for (i = 32; i < 128; ++i) {
            HtmlEncoder.htmlCode[i] = String.valueOf((char)i);
        }
        HtmlEncoder.htmlCode[9] = "\t";
        HtmlEncoder.htmlCode[10] = "<br />\n";
        HtmlEncoder.htmlCode[34] = "&quot;";
        HtmlEncoder.htmlCode[38] = "&amp;";
        HtmlEncoder.htmlCode[60] = "&lt;";
        HtmlEncoder.htmlCode[62] = "&gt;";
        for (i = 128; i < 256; ++i) {
            HtmlEncoder.htmlCode[i] = "&#" + i + ";";
        }
    }
}

