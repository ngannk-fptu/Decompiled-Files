/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ResolutionUnit {
    NONE(1, "None"),
    INCH(2, "Inch"),
    CENTIMETER(3, "Centimeter");

    private static final Map<Integer, ResolutionUnit> LOOKUP;
    private final int value;
    private final String description;

    private ResolutionUnit(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static ResolutionUnit get(int value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap<Integer, ResolutionUnit>();
        for (ResolutionUnit unit : EnumSet.allOf(ResolutionUnit.class)) {
            LOOKUP.put(unit.getValue(), unit);
        }
    }
}

