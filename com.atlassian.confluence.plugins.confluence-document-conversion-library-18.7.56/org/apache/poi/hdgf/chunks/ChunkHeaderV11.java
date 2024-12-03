/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.poi.hdgf.chunks.ChunkHeaderV6;

public final class ChunkHeaderV11
extends ChunkHeaderV6 {
    @Override
    public boolean hasSeparator() {
        short unknown2 = this.getUnknown2();
        short unknown3 = this.getUnknown3();
        switch (this.getType()) {
            case 31: 
            case 201: {
                return false;
            }
            case 105: {
                return true;
            }
            case 169: 
            case 170: 
            case 180: 
            case 182: {
                if (unknown2 != 2 || unknown3 != 84) break;
                return true;
            }
        }
        if (unknown2 == 2 && unknown3 == 85 || unknown2 == 3 && unknown3 != 80) {
            return true;
        }
        return this.hasTrailer();
    }

    @Override
    public Charset getChunkCharset() {
        return StandardCharsets.UTF_16LE;
    }
}

