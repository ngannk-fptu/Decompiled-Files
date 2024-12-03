/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.weaver.ConstantPoolWriter;

class BcelConstantPoolWriter
implements ConstantPoolWriter {
    ConstantPool pool;

    public BcelConstantPoolWriter(ConstantPool pool) {
        this.pool = pool;
    }

    @Override
    public int writeUtf8(String name) {
        return this.pool.addUtf8(name);
    }
}

