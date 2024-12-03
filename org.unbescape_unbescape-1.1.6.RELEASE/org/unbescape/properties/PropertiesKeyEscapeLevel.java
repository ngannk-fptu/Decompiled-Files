/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.properties;

public enum PropertiesKeyEscapeLevel {
    LEVEL_1_BASIC_ESCAPE_SET(1),
    LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET(2),
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),
    LEVEL_4_ALL_CHARACTERS(4);

    private final int escapeLevel;

    public static PropertiesKeyEscapeLevel forLevel(int level) {
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

    private PropertiesKeyEscapeLevel(int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    public int getEscapeLevel() {
        return this.escapeLevel;
    }
}

