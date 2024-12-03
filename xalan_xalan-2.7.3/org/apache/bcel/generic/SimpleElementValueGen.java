/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.SimpleElementValue;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;

public class SimpleElementValueGen
extends ElementValueGen {
    private final int idx;

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, boolean value) {
        super(type, cpGen);
        this.idx = value ? this.getConstantPool().addInteger(1) : this.getConstantPool().addInteger(0);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, byte value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addInteger(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, char value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addInteger(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, double value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addDouble(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, float value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addFloat(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, int value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addInteger(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, long value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addLong(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, short value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addInteger(value);
    }

    public SimpleElementValueGen(int type, ConstantPoolGen cpGen, String value) {
        super(type, cpGen);
        this.idx = this.getConstantPool().addUtf8(value);
    }

    protected SimpleElementValueGen(int type, int idx, ConstantPoolGen cpGen) {
        super(type, cpGen);
        this.idx = idx;
    }

    public SimpleElementValueGen(SimpleElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        super(value.getElementValueType(), cpool);
        if (!copyPoolEntries) {
            this.idx = value.getIndex();
        } else {
            switch (value.getElementValueType()) {
                case 115: {
                    this.idx = cpool.addUtf8(value.getValueString());
                    break;
                }
                case 73: {
                    this.idx = cpool.addInteger(value.getValueInt());
                    break;
                }
                case 66: {
                    this.idx = cpool.addInteger(value.getValueByte());
                    break;
                }
                case 67: {
                    this.idx = cpool.addInteger(value.getValueChar());
                    break;
                }
                case 74: {
                    this.idx = cpool.addLong(value.getValueLong());
                    break;
                }
                case 70: {
                    this.idx = cpool.addFloat(value.getValueFloat());
                    break;
                }
                case 68: {
                    this.idx = cpool.addDouble(value.getValueDouble());
                    break;
                }
                case 90: {
                    if (value.getValueBoolean()) {
                        this.idx = cpool.addInteger(1);
                        break;
                    }
                    this.idx = cpool.addInteger(0);
                    break;
                }
                case 83: {
                    this.idx = cpool.addInteger(value.getValueShort());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("SimpleElementValueGen class does not know how to copy this type " + super.getElementValueType());
                }
            }
        }
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getElementValueType());
        switch (super.getElementValueType()) {
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 115: {
                dos.writeShort(this.idx);
                break;
            }
            default: {
                throw new IllegalStateException("SimpleElementValueGen doesnt know how to write out type " + super.getElementValueType());
            }
        }
    }

    @Override
    public ElementValue getElementValue() {
        return new SimpleElementValue(super.getElementValueType(), this.idx, this.getConstantPool().getConstantPool());
    }

    public int getIndex() {
        return this.idx;
    }

    public int getValueInt() {
        if (super.getElementValueType() != 73) {
            throw new IllegalStateException("Dont call getValueString() on a non STRING ElementValue");
        }
        ConstantInteger c = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
        return c.getBytes();
    }

    public String getValueString() {
        if (super.getElementValueType() != 115) {
            throw new IllegalStateException("Dont call getValueString() on a non STRING ElementValue");
        }
        ConstantUtf8 c = (ConstantUtf8)this.getConstantPool().getConstant(this.idx);
        return c.getBytes();
    }

    @Override
    public String stringifyValue() {
        switch (super.getElementValueType()) {
            case 73: {
                ConstantInteger c = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
                return Integer.toString(c.getBytes());
            }
            case 74: {
                ConstantLong j = (ConstantLong)this.getConstantPool().getConstant(this.idx);
                return Long.toString(j.getBytes());
            }
            case 68: {
                ConstantDouble d = (ConstantDouble)this.getConstantPool().getConstant(this.idx);
                return Double.toString(d.getBytes());
            }
            case 70: {
                ConstantFloat f = (ConstantFloat)this.getConstantPool().getConstant(this.idx);
                return Float.toString(f.getBytes());
            }
            case 83: {
                ConstantInteger s = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
                return Integer.toString(s.getBytes());
            }
            case 66: {
                ConstantInteger b = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
                return Integer.toString(b.getBytes());
            }
            case 67: {
                ConstantInteger ch = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
                return Integer.toString(ch.getBytes());
            }
            case 90: {
                ConstantInteger bo = (ConstantInteger)this.getConstantPool().getConstant(this.idx);
                if (bo.getBytes() == 0) {
                    return "false";
                }
                return "true";
            }
            case 115: {
                ConstantUtf8 cu8 = (ConstantUtf8)this.getConstantPool().getConstant(this.idx);
                return cu8.getBytes();
            }
        }
        throw new IllegalStateException("SimpleElementValueGen class does not know how to stringify type " + super.getElementValueType());
    }
}

