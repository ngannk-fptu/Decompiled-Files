/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.TBDAbstractType;

public class TabDescriptor
extends TBDAbstractType
implements Duplicatable {
    public TabDescriptor() {
    }

    public TabDescriptor(TabDescriptor other) {
        super(other);
    }

    public TabDescriptor(byte[] bytes, int offset) {
        this.fillFields(bytes, offset);
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[TabDescriptor.getSize()];
        this.serialize(buf, 0);
        return buf;
    }

    @Override
    public TabDescriptor copy() {
        return new TabDescriptor(this);
    }
}

