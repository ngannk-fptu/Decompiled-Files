/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantInteger
extends Constant {
    private final int bytes;

    ConstantInteger(DataInput file) throws IOException {
        super((byte)3);
        this.bytes = file.readInt();
    }

    public int getBytes() {
        return this.bytes;
    }
}

