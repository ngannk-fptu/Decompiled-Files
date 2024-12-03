/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import org.apache.tomcat.util.bcel.classfile.ConstantDouble;
import org.apache.tomcat.util.bcel.classfile.ConstantFloat;
import org.apache.tomcat.util.bcel.classfile.ConstantInteger;
import org.apache.tomcat.util.bcel.classfile.ConstantLong;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class SimpleElementValue
extends ElementValue {
    private final int index;

    SimpleElementValue(int type, int index, ConstantPool cpool) {
        super(type, cpool);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String stringifyValue() {
        ConstantPool cpool = super.getConstantPool();
        int type = super.getType();
        switch (type) {
            case 73: {
                return Integer.toString(cpool.getConstantInteger(this.getIndex()).getBytes());
            }
            case 74: {
                ConstantLong j = (ConstantLong)cpool.getConstant(this.getIndex(), (byte)5);
                return Long.toString(j.getBytes());
            }
            case 68: {
                ConstantDouble d = (ConstantDouble)cpool.getConstant(this.getIndex(), (byte)6);
                return Double.toString(d.getBytes());
            }
            case 70: {
                ConstantFloat f = (ConstantFloat)cpool.getConstant(this.getIndex(), (byte)4);
                return Float.toString(f.getBytes());
            }
            case 83: {
                ConstantInteger s = cpool.getConstantInteger(this.getIndex());
                return Integer.toString(s.getBytes());
            }
            case 66: {
                ConstantInteger b = cpool.getConstantInteger(this.getIndex());
                return Integer.toString(b.getBytes());
            }
            case 67: {
                ConstantInteger ch = cpool.getConstantInteger(this.getIndex());
                return String.valueOf((char)ch.getBytes());
            }
            case 90: {
                ConstantInteger bo = cpool.getConstantInteger(this.getIndex());
                if (bo.getBytes() == 0) {
                    return "false";
                }
                return "true";
            }
            case 115: {
                return cpool.getConstantUtf8(this.getIndex()).getBytes();
            }
        }
        throw new IllegalStateException("SimpleElementValue class does not know how to stringify type " + type);
    }
}

