/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantFloat
extends Constant {
    private final float bytes;

    ConstantFloat(DataInput file) throws IOException {
        super((byte)4);
        this.bytes = file.readFloat();
    }

    public float getBytes() {
        return this.bytes;
    }
}

