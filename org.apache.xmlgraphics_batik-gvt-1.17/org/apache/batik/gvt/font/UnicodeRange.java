/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

public class UnicodeRange {
    private int firstUnicodeValue;
    private int lastUnicodeValue;

    public UnicodeRange(String unicodeRange) {
        if (unicodeRange.startsWith("U+") && unicodeRange.length() > 2) {
            String lastValue;
            String firstValue;
            int dashIndex = (unicodeRange = unicodeRange.substring(2)).indexOf(45);
            if (dashIndex != -1) {
                firstValue = unicodeRange.substring(0, dashIndex);
                lastValue = unicodeRange.substring(dashIndex + 1);
            } else {
                firstValue = unicodeRange;
                lastValue = unicodeRange;
                if (unicodeRange.indexOf(63) != -1) {
                    firstValue = firstValue.replace('?', '0');
                    lastValue = lastValue.replace('?', 'F');
                }
            }
            try {
                this.firstUnicodeValue = Integer.parseInt(firstValue, 16);
                this.lastUnicodeValue = Integer.parseInt(lastValue, 16);
            }
            catch (NumberFormatException e) {
                this.firstUnicodeValue = -1;
                this.lastUnicodeValue = -1;
            }
        } else {
            this.firstUnicodeValue = -1;
            this.lastUnicodeValue = -1;
        }
    }

    public boolean contains(String unicode) {
        if (unicode.length() == 1) {
            char unicodeVal = unicode.charAt(0);
            return this.contains(unicodeVal);
        }
        return false;
    }

    public boolean contains(int unicodeVal) {
        return unicodeVal >= this.firstUnicodeValue && unicodeVal <= this.lastUnicodeValue;
    }
}

