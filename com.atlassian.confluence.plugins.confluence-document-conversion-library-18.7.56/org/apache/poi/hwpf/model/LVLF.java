/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.LVLFAbstractType;
import org.apache.poi.util.Internal;

@Internal
class LVLF
extends LVLFAbstractType {
    public LVLF() {
    }

    public LVLF(byte[] std, int offset) {
        this.fillFields(std, offset);
    }
}

