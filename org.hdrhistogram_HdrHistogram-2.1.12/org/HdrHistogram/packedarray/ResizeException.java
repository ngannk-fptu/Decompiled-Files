/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

class ResizeException
extends Exception {
    private int newSize;

    ResizeException(int newSize) {
        this.newSize = newSize;
    }

    int getNewSize() {
        return this.newSize;
    }
}

