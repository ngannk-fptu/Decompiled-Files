/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantLong
extends Constant {
    private final long bytes;

    ConstantLong(DataInput file) throws IOException {
        super((byte)5);
        this.bytes = file.readLong();
    }

    public long getBytes() {
        return this.bytes;
    }
}

