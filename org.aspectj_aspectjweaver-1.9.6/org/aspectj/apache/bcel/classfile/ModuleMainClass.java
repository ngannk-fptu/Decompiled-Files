/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class ModuleMainClass
extends Attribute {
    private int mainClassIndex;

    public ModuleMainClass(ModuleMainClass c) {
        this(c.getNameIndex(), c.getLength(), c.getMainClassIndex(), c.getConstantPool());
    }

    public ModuleMainClass(int nameIndex, int length, int mainClassIndex, ConstantPool cp) {
        super((byte)25, nameIndex, length, cp);
        this.mainClassIndex = mainClassIndex;
    }

    ModuleMainClass(int nameIndex, int length, DataInputStream stream, ConstantPool cp) throws IOException {
        this(nameIndex, length, 0, cp);
        this.mainClassIndex = stream.readUnsignedShort();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitModuleMainClass(this);
    }

    @Override
    public final void dump(DataOutputStream stream) throws IOException {
        super.dump(stream);
        stream.writeShort(this.mainClassIndex);
    }

    public final int getMainClassIndex() {
        return this.mainClassIndex;
    }

    @Override
    public final String toString() {
        return this.cpool.getConstantString_CONSTANTClass(this.mainClassIndex);
    }
}

