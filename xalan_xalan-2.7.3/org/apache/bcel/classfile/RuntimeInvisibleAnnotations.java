/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;

public class RuntimeInvisibleAnnotations
extends Annotations {
    public RuntimeInvisibleAnnotations(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        super((byte)13, nameIndex, length, input, constantPool, false);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public final void dump(DataOutputStream dos) throws IOException {
        super.dump(dos);
        this.writeAnnotations(dos);
    }
}

