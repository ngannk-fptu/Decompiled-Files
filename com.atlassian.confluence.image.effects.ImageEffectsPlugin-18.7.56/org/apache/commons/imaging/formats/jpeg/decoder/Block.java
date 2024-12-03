/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.decoder;

final class Block {
    final int[] samples;
    final int width;
    final int height;

    Block(int width, int height) {
        this.samples = new int[width * height];
        this.width = width;
        this.height = height;
    }
}

