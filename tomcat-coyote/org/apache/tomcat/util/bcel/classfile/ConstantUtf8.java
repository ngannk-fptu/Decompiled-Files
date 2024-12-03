/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Objects;
import org.apache.tomcat.util.bcel.classfile.Constant;

public final class ConstantUtf8
extends Constant {
    private final String value;

    static ConstantUtf8 getInstance(DataInput dataInput) throws IOException {
        return new ConstantUtf8(dataInput.readUTF());
    }

    private ConstantUtf8(String value) {
        super((byte)1);
        this.value = Objects.requireNonNull(value, "value");
    }

    public String getBytes() {
        return this.value;
    }
}

