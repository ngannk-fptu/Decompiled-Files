/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.html;

public enum HtmlEscapeLevel {
    LEVEL_0_ONLY_MARKUP_SIGNIFICANT_EXCEPT_APOS(0),
    LEVEL_1_ONLY_MARKUP_SIGNIFICANT(1),
    LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT(2),
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),
    LEVEL_4_ALL_CHARACTERS(4);

    private final int escapeLevel;

    public static HtmlEscapeLevel forLevel(int level) {
        switch (level) {
            case 0: {
                return LEVEL_0_ONLY_MARKUP_SIGNIFICANT_EXCEPT_APOS;
            }
            case 1: {
                return LEVEL_1_ONLY_MARKUP_SIGNIFICANT;
            }
            case 2: {
                return LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT;
            }
            case 3: {
                return LEVEL_3_ALL_NON_ALPHANUMERIC;
            }
            case 4: {
                return LEVEL_4_ALL_CHARACTERS;
            }
        }
        throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
    }

    private HtmlEscapeLevel(int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    public int getEscapeLevel() {
        return this.escapeLevel;
    }
}

