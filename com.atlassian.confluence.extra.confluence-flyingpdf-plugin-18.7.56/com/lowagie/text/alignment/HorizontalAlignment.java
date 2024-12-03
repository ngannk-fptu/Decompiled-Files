/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.alignment;

import java.util.Optional;

public enum HorizontalAlignment {
    UNDEFINED(-1),
    LEFT(0),
    CENTER(1),
    RIGHT(2),
    JUSTIFIED(3),
    JUSTIFIED_ALL(8);

    private final int id;

    private HorizontalAlignment(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Optional<HorizontalAlignment> of(int id) {
        for (HorizontalAlignment alignment : HorizontalAlignment.values()) {
            if (alignment.id != id) continue;
            return Optional.of(alignment);
        }
        return Optional.empty();
    }
}

