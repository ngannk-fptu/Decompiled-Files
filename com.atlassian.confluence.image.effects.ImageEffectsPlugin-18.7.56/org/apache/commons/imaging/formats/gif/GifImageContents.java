/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import java.util.List;
import org.apache.commons.imaging.formats.gif.GifBlock;
import org.apache.commons.imaging.formats.gif.GifHeaderInfo;

class GifImageContents {
    final GifHeaderInfo gifHeaderInfo;
    final List<GifBlock> blocks;
    final byte[] globalColorTable;

    GifImageContents(GifHeaderInfo gifHeaderInfo, byte[] globalColorTable, List<GifBlock> blocks) {
        this.gifHeaderInfo = gifHeaderInfo;
        this.globalColorTable = globalColorTable;
        this.blocks = blocks;
    }
}

