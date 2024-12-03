/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.SimpleConstant;
import org.aspectj.apache.bcel.classfile.Utility;

public final class ConstantUtf8
extends Constant
implements SimpleConstant {
    private String string;

    ConstantUtf8(DataInputStream file) throws IOException {
        super((byte)1);
        this.string = file.readUTF();
    }

    public ConstantUtf8(String string) {
        super((byte)1);
        assert (string != null);
        this.string = string;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantUtf8(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeUTF(this.string);
    }

    @Override
    public final String toString() {
        return super.toString() + "(\"" + Utility.replace(this.string, "\n", "\\n") + "\")";
    }

    @Override
    public String getValue() {
        return this.string;
    }

    @Override
    public String getStringValue() {
        return this.string;
    }
}

