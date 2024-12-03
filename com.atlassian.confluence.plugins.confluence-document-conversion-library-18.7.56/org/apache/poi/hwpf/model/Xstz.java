/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.Xst;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class Xstz {
    private static final Logger LOGGER = LogManager.getLogger(Xstz.class);
    private final short _chTerm = 0;
    private Xst _xst;

    public Xstz() {
        this._xst = new Xst();
    }

    public Xstz(byte[] data, int startOffset) {
        this.fillFields(data, startOffset);
    }

    public void fillFields(byte[] data, int startOffset) {
        int offset = startOffset;
        this._xst = new Xst(data, offset);
        short term = LittleEndian.getShort(data, offset += this._xst.getSize());
        if (term != 0) {
            LOGGER.atWarn().log("chTerm at the end of Xstz at offset {} is not 0", (Object)Unbox.box(offset));
        }
    }

    public String getAsJavaString() {
        return this._xst.getAsJavaString();
    }

    public int getSize() {
        return this._xst.getSize() + 2;
    }

    public int serialize(byte[] data, int startOffset) {
        int offset = startOffset;
        this._xst.serialize(data, offset);
        LittleEndian.putUShort(data, offset += this._xst.getSize(), 0);
        return (offset += 2) - startOffset;
    }

    public String toString() {
        return "[Xstz]" + this._xst.getAsJavaString() + "[/Xstz]";
    }
}

