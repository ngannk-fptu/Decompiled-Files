/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.LFOLVLBaseAbstractType;

class LFOLVLBase
extends LFOLVLBaseAbstractType {
    LFOLVLBase() {
    }

    LFOLVLBase(byte[] buf, int offset) {
        this.fillFields(buf, offset);
    }
}

