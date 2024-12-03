/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

enum ColorComponent {
    ALPHA(24),
    RED(16),
    GREEN(8),
    BLUE(0);

    private final int shift;

    private ColorComponent(int shift) {
        this.shift = shift;
    }

    public int argbComponent(int argb) {
        return argb >> this.shift & 0xFF;
    }
}

