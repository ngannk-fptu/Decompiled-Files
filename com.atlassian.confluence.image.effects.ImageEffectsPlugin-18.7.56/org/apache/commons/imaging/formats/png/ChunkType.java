/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.common.BinaryFunctions;

public enum ChunkType {
    IHDR,
    PLTE,
    IDAT,
    IEND,
    tRNS,
    cHRM,
    gAMA,
    iCCP,
    sBIT,
    sRGB,
    tEXt,
    zTXt,
    iTXt,
    bKGD,
    hIST,
    pHYs,
    sCAL,
    sPLT,
    tIME;

    final byte[] array;
    final int value;

    private ChunkType() {
        char[] chars = this.name().toCharArray();
        this.array = this.name().getBytes(StandardCharsets.UTF_8);
        this.value = BinaryFunctions.charsToQuad(chars[0], chars[1], chars[2], chars[3]);
    }
}

