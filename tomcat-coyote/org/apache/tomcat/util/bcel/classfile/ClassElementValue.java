/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class ClassElementValue
extends ElementValue {
    private final int idx;

    ClassElementValue(int type, int idx, ConstantPool cpool) {
        super(type, cpool);
        this.idx = idx;
    }

    @Override
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.idx).getBytes();
    }
}

