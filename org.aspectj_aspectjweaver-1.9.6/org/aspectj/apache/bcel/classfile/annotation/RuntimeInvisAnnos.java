/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;

public class RuntimeInvisAnnos
extends RuntimeAnnos {
    public RuntimeInvisAnnos(int nameIdx, int len, ConstantPool cpool) {
        super((byte)13, false, nameIdx, len, cpool);
    }

    public RuntimeInvisAnnos(int nameIdx, int len, DataInputStream dis, ConstantPool cpool) throws IOException {
        this(nameIdx, len, cpool);
        this.readAnnotations(dis, cpool);
    }

    public RuntimeInvisAnnos(int nameIndex, int len, byte[] rvaData, ConstantPool cpool) {
        super((byte)13, false, nameIndex, len, rvaData, cpool);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitRuntimeInvisibleAnnotations(this);
    }

    @Override
    public final void dump(DataOutputStream dos) throws IOException {
        super.dump(dos);
        this.writeAnnotations(dos);
    }

    public Attribute copy(ConstantPool constant_pool) {
        throw new RuntimeException("Not implemented yet!");
    }
}

