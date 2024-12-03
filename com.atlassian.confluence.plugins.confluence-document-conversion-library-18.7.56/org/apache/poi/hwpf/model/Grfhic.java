/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.GrfhicAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class Grfhic
extends GrfhicAbstractType {
    public Grfhic() {
    }

    public Grfhic(byte[] bytes, int offset) {
        this.fillFields(bytes, offset);
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[Grfhic.getSize()];
        this.serialize(buf, 0);
        return buf;
    }
}

