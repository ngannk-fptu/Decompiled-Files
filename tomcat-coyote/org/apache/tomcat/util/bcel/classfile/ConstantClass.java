/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantClass
extends Constant {
    private final int nameIndex;

    ConstantClass(DataInput dataInput) throws IOException {
        super((byte)7);
        this.nameIndex = dataInput.readUnsignedShort();
    }

    public int getNameIndex() {
        return this.nameIndex;
    }
}

