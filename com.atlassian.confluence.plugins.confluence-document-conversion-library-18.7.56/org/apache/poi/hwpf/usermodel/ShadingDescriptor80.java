/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.Colorref;
import org.apache.poi.hwpf.model.types.SHD80AbstractType;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;

public final class ShadingDescriptor80
extends SHD80AbstractType
implements Duplicatable {
    public ShadingDescriptor80() {
    }

    public ShadingDescriptor80(ShadingDescriptor80 other) {
        super(other);
    }

    public ShadingDescriptor80(byte[] buf, int offset) {
        this.fillFields(buf, offset);
    }

    public ShadingDescriptor80(short value) {
        this.field_1_value = value;
    }

    @Override
    public ShadingDescriptor80 copy() {
        return new ShadingDescriptor80(this);
    }

    public boolean isEmpty() {
        return this.field_1_value == 0;
    }

    @Override
    public byte[] serialize() {
        byte[] result = new byte[ShadingDescriptor80.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public ShadingDescriptor toShadingDescriptor() {
        ShadingDescriptor result = new ShadingDescriptor();
        result.setCvFore(Colorref.valueOfIco(this.getIcoFore()));
        result.setCvBack(Colorref.valueOfIco(this.getIcoBack()));
        result.setIpat(this.getIpat());
        return result;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[SHD80] EMPTY";
        }
        return "[SHD80] (icoFore: " + this.getIcoFore() + "; icoBack: " + this.getIcoBack() + "; iPat: " + this.getIpat() + ")";
    }
}

