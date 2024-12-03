/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.alignment;

import java.util.Optional;

public enum VerticalAlignment {
    UNDEFINED(-1),
    TOP(4),
    CENTER(5),
    BOTTOM(6),
    BASELINE(7);

    private final int id;

    private VerticalAlignment(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Optional<VerticalAlignment> of(int id) {
        for (VerticalAlignment alignment : VerticalAlignment.values()) {
            if (alignment.id != id) continue;
            return Optional.of(alignment);
        }
        return Optional.empty();
    }
}

