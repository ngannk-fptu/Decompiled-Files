/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.usermodel;

import org.apache.poi.hwmf.usermodel.HwmfEmbeddedType;

public class HwmfEmbedded {
    private HwmfEmbeddedType embeddedType;
    private byte[] data;

    public HwmfEmbeddedType getEmbeddedType() {
        return this.embeddedType;
    }

    public byte[] getRawData() {
        return this.data;
    }

    public void setEmbeddedType(HwmfEmbeddedType embeddedType) {
        this.embeddedType = embeddedType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

