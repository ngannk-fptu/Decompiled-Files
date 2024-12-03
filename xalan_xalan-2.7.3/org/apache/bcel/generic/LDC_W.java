/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.IOException;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.util.ByteSequence;

public class LDC_W
extends LDC {
    LDC_W() {
    }

    public LDC_W(int index) {
        super(index);
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.setIndex(bytes.readUnsignedShort());
        super.setOpcode((short)19);
        super.setLength(3);
    }
}

