/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeParamAnnos;

public class RuntimeInvisParamAnnos
extends RuntimeParamAnnos {
    public RuntimeInvisParamAnnos(int nameIdx, int len, ConstantPool cpool) {
        super((byte)15, false, nameIdx, len, cpool);
    }

    public RuntimeInvisParamAnnos(int nameIdx, int len, DataInputStream dis, ConstantPool cpool) throws IOException {
        this(nameIdx, len, cpool);
        this.readParameterAnnotations(dis, cpool);
    }

    public RuntimeInvisParamAnnos(int nameIndex, int len, byte[] rvaData, ConstantPool cpool) {
        super((byte)15, false, nameIndex, len, rvaData, cpool);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitRuntimeInvisibleParameterAnnotations(this);
    }

    @Override
    public Attribute copy(ConstantPool constant_pool) {
        throw new RuntimeException("Not implemented yet!");
    }
}

