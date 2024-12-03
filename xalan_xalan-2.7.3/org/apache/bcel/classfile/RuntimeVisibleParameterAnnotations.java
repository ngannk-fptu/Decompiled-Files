/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ParameterAnnotations;

public class RuntimeVisibleParameterAnnotations
extends ParameterAnnotations {
    public RuntimeVisibleParameterAnnotations(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        super((byte)14, nameIndex, length, input, constantPool);
    }
}

