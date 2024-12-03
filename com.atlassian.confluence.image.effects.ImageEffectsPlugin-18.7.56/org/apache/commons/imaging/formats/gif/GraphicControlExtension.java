/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import org.apache.commons.imaging.formats.gif.GifBlock;

class GraphicControlExtension
extends GifBlock {
    public final int packed;
    public final int dispose;
    public final boolean transparency;
    public final int delay;
    public final int transparentColorIndex;

    GraphicControlExtension(int blockCode, int packed, int dispose, boolean transparency, int delay, int transparentColorIndex) {
        super(blockCode);
        this.packed = packed;
        this.dispose = dispose;
        this.transparency = transparency;
        this.delay = delay;
        this.transparentColorIndex = transparentColorIndex;
    }
}

