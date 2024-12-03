/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.css;

public enum CssIdentifierEscapeLevel {
    LEVEL_1_BASIC_ESCAPE_SET(1),
    LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET(2),
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),
    LEVEL_4_ALL_CHARACTERS(4);

    private final int escapeLevel;

    public static CssIdentifierEscapeLevel forLevel(int level) {
        switch (level) {
            case 1: {
                return LEVEL_1_BASIC_ESCAPE_SET;
            }
            case 2: {
                return LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET;
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

    private CssIdentifierEscapeLevel(int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    public int getEscapeLevel() {
        return this.escapeLevel;
    }
}

