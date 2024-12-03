/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.LFOAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class LFO
extends LFOAbstractType {
    public LFO() {
    }

    public LFO(byte[] std, int offset) {
        this.fillFields(std, offset);
    }
}

