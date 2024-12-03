/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.FFDataBaseAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class FFDataBase
extends FFDataBaseAbstractType {
    public FFDataBase() {
    }

    public FFDataBase(byte[] std, int offset) {
        this.fillFields(std, offset);
    }
}

