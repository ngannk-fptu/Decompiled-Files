/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.LSTFAbstractType;

class LSTF
extends LSTFAbstractType {
    LSTF() {
    }

    LSTF(byte[] buf, int offset) {
        this.fillFields(buf, offset);
    }
}

