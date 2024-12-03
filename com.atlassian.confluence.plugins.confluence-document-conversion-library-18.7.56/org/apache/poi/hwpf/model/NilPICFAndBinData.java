/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.util.LittleEndian;

public class NilPICFAndBinData {
    private static final Logger LOGGER = LogManager.getLogger(NilPICFAndBinData.class);
    private byte[] _binData;

    public NilPICFAndBinData(byte[] data, int offset) {
        this.fillFields(data, offset);
    }

    public void fillFields(byte[] data, int offset) {
        int lcb = LittleEndian.getInt(data, offset);
        int cbHeader = LittleEndian.getUShort(data, offset + 4);
        if (cbHeader != 68) {
            LOGGER.atWarn().log("NilPICFAndBinData at offset {} cbHeader 0x{} != 0x44", (Object)Unbox.box(offset), (Object)Integer.toHexString(cbHeader));
        }
        int binaryLength = lcb - cbHeader;
        this._binData = Arrays.copyOfRange(data, offset + cbHeader, offset + cbHeader + binaryLength);
    }

    public byte[] getBinData() {
        return this._binData;
    }

    public byte[] serialize() {
        byte[] bs = new byte[this._binData.length + 68];
        LittleEndian.putInt(bs, 0, this._binData.length + 68);
        System.arraycopy(this._binData, 0, bs, 68, this._binData.length);
        return bs;
    }

    public int serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, offset, this._binData.length + 68);
        System.arraycopy(this._binData, 0, data, offset + 68, this._binData.length);
        return 68 + this._binData.length;
    }
}

