/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.weaver.ConstantPoolReader;

public class BcelConstantPoolReader
implements ConstantPoolReader {
    private ConstantPool constantPool;

    public BcelConstantPoolReader(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    @Override
    public String readUtf8(int cpIndex) {
        return this.constantPool.getConstantUtf8(cpIndex).getValue();
    }
}

