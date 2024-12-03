/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.SHDAbstractType;

public final class ShadingDescriptor
extends SHDAbstractType
implements Duplicatable {
    public ShadingDescriptor() {
    }

    public ShadingDescriptor(ShadingDescriptor other) {
        super(other);
    }

    public ShadingDescriptor(byte[] buf, int offset) {
        this.fillFields(buf, offset);
    }

    @Override
    public ShadingDescriptor copy() {
        return new ShadingDescriptor(this);
    }

    public boolean isEmpty() {
        return this.field_3_ipat == 0;
    }

    @Override
    public byte[] serialize() {
        byte[] result = new byte[ShadingDescriptor.getSize()];
        this.serialize(result, 0);
        return result;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[SHD] EMPTY";
        }
        return "[SHD] (cvFore: " + this.getCvFore() + "; cvBack: " + this.getCvBack() + "; iPat: " + this.getIpat() + ")";
    }
}

