/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeTypeAnnos;

public class RuntimeVisTypeAnnos
extends RuntimeTypeAnnos {
    public RuntimeVisTypeAnnos(int nameIdx, int len, DataInputStream dis, ConstantPool cpool) throws IOException {
        this(nameIdx, len, cpool);
        this.readTypeAnnotations(dis, cpool);
    }

    public RuntimeVisTypeAnnos(int nameIdx, int len, ConstantPool cpool) {
        super((byte)20, true, nameIdx, len, cpool);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitRuntimeVisibleTypeAnnotations(this);
    }
}

