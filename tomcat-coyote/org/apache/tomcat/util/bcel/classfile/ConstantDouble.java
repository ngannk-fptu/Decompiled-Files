/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantDouble
extends Constant {
    private final double bytes;

    ConstantDouble(DataInput file) throws IOException {
        super((byte)6);
        this.bytes = file.readDouble();
    }

    public double getBytes() {
        return this.bytes;
    }
}

