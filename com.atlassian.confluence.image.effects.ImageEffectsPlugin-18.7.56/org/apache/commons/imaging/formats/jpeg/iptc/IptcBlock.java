/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.util.Objects;

public class IptcBlock {
    private final int blockType;
    private final byte[] blockNameBytes;
    private final byte[] blockData;

    public IptcBlock(int blockType, byte[] blockNameBytes, byte[] blockData) {
        Objects.requireNonNull(blockNameBytes, "Block name bytes must not be null.");
        Objects.requireNonNull(blockNameBytes, "Block data bytes must not be null.");
        this.blockData = blockData;
        this.blockNameBytes = blockNameBytes;
        this.blockType = blockType;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public byte[] getBlockNameBytes() {
        return (byte[])this.blockNameBytes.clone();
    }

    public byte[] getBlockData() {
        return (byte[])this.blockData.clone();
    }

    public boolean isIPTCBlock() {
        return this.blockType == 1028;
    }
}

