/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.util.Locale;

final class UniUtil {
    private UniUtil() {
    }

    static String getUniNameOfCodePoint(int codePoint) {
        String hex = Integer.toString(codePoint, 16).toUpperCase(Locale.US);
        switch (hex.length()) {
            case 1: {
                return "uni000" + hex;
            }
            case 2: {
                return "uni00" + hex;
            }
            case 3: {
                return "uni0" + hex;
            }
        }
        return "uni" + hex;
    }
}

