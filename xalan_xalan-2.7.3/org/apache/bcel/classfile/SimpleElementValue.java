/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class SimpleElementValue
extends ElementValue {
    private int index;

    public SimpleElementValue(int type, int index, ConstantPool cpool) {
        super(type, cpool);
        this.index = index;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        int type = super.getType();
        dos.writeByte(type);
        switch (type) {
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 115: {
                dos.writeShort(this.getIndex());
                break;
            }
            default: {
                throw new ClassFormatException("SimpleElementValue doesnt know how to write out type " + type);
            }
        }
    }

    public int getIndex() {
        return this.index;
    }

    public boolean getValueBoolean() {
        if (super.getType() != 90) {
            throw new IllegalStateException("Dont call getValueBoolean() on a non BOOLEAN ElementValue");
        }
        ConstantInteger bo = (ConstantInteger)super.getConstantPool().getConstant(this.getIndex());
        return bo.getBytes() != 0;
    }

    public byte getValueByte() {
        if (super.getType() != 66) {
            throw new IllegalStateException("Dont call getValueByte() on a non BYTE ElementValue");
        }
        return (byte)super.getConstantPool().getConstantInteger(this.getIndex()).getBytes();
    }

    public char getValueChar() {
        if (super.getType() != 67) {
            throw new IllegalStateException("Dont call getValueChar() on a non CHAR ElementValue");
        }
        return (char)super.getConstantPool().getConstantInteger(this.getIndex()).getBytes();
    }

    public double getValueDouble() {
        if (super.getType() != 68) {
            throw new IllegalStateException("Dont call getValueDouble() on a non DOUBLE ElementValue");
        }
        ConstantDouble d = (ConstantDouble)super.getConstantPool().getConstant(this.getIndex());
        return d.getBytes();
    }

    public float getValueFloat() {
        if (super.getType() != 70) {
            throw new IllegalStateException("Dont call getValueFloat() on a non FLOAT ElementValue");
        }
        ConstantFloat f = (ConstantFloat)super.getConstantPool().getConstant(this.getIndex());
        return f.getBytes();
    }

    public int getValueInt() {
        if (super.getType() != 73) {
            throw new IllegalStateException("Dont call getValueInt() on a non INT ElementValue");
        }
        return super.getConstantPool().getConstantInteger(this.getIndex()).getBytes();
    }

    public long getValueLong() {
        if (super.getType() != 74) {
            throw new IllegalStateException("Dont call getValueLong() on a non LONG ElementValue");
        }
        ConstantLong j = (ConstantLong)super.getConstantPool().getConstant(this.getIndex());
        return j.getBytes();
    }

    public short getValueShort() {
        if (super.getType() != 83) {
            throw new IllegalStateException("Dont call getValueShort() on a non SHORT ElementValue");
        }
        ConstantInteger s = (ConstantInteger)super.getConstantPool().getConstant(this.getIndex());
        return (short)s.getBytes();
    }

    public String getValueString() {
        if (super.getType() != 115) {
            throw new IllegalStateException("Dont call getValueString() on a non STRING ElementValue");
        }
        return super.getConstantPool().getConstantUtf8(this.getIndex()).getBytes();
    }

    public void setIndex(int index) {
        this.index = index;
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
                ConstantLong j = cpool.getConstant(this.getIndex(), (byte)5, ConstantLong.class);
                return Long.toString(j.getBytes());
            }
            case 68: {
                ConstantDouble d = cpool.getConstant(this.getIndex(), (byte)6, ConstantDouble.class);
                return Double.toString(d.getBytes());
            }
            case 70: {
                ConstantFloat f = cpool.getConstant(this.getIndex(), (byte)4, ConstantFloat.class);
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

    @Override
    public String toString() {
        return this.stringifyValue();
    }
}

