/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.FSPAAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class FSPA
extends FSPAAbstractType {
    public FSPA(byte[] bytes, int offset) {
        this.fillFields(bytes, offset);
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[FSPA.getSize()];
        this.serialize(buf, 0);
        return buf;
    }
}

