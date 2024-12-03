/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.imaging.formats.gif.GifBlock;

class GenericGifBlock
extends GifBlock {
    final List<byte[]> subblocks;

    GenericGifBlock(int blockCode, List<byte[]> subblocks) {
        super(blockCode);
        this.subblocks = subblocks;
    }

    public byte[] appendSubBlocks() throws IOException {
        return this.appendSubBlocks(false);
    }

    public byte[] appendSubBlocks(boolean includeLengths) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < this.subblocks.size(); ++i) {
            byte[] subblock = this.subblocks.get(i);
            if (includeLengths && i > 0) {
                out.write(subblock.length);
            }
            out.write(subblock);
        }
        return out.toByteArray();
    }
}

