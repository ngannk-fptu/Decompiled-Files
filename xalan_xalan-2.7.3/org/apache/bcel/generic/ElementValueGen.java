/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.AnnotationElementValue;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ClassElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.EnumElementValue;
import org.apache.bcel.classfile.SimpleElementValue;
import org.apache.bcel.generic.AnnotationElementValueGen;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ArrayElementValueGen;
import org.apache.bcel.generic.ClassElementValueGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.EnumElementValueGen;
import org.apache.bcel.generic.SimpleElementValueGen;

public abstract class ElementValueGen {
    public static final int STRING = 115;
    public static final int ENUM_CONSTANT = 101;
    public static final int CLASS = 99;
    public static final int ANNOTATION = 64;
    public static final int ARRAY = 91;
    public static final int PRIMITIVE_INT = 73;
    public static final int PRIMITIVE_BYTE = 66;
    public static final int PRIMITIVE_CHAR = 67;
    public static final int PRIMITIVE_DOUBLE = 68;
    public static final int PRIMITIVE_FLOAT = 70;
    public static final int PRIMITIVE_LONG = 74;
    public static final int PRIMITIVE_SHORT = 83;
    public static final int PRIMITIVE_BOOLEAN = 90;
    @Deprecated
    protected int type;
    @Deprecated
    protected ConstantPoolGen cpGen;

    public static ElementValueGen copy(ElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        switch (value.getElementValueType()) {
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 115: {
                return new SimpleElementValueGen((SimpleElementValue)value, cpool, copyPoolEntries);
            }
            case 101: {
                return new EnumElementValueGen((EnumElementValue)value, cpool, copyPoolEntries);
            }
            case 64: {
                return new AnnotationElementValueGen((AnnotationElementValue)value, cpool, copyPoolEntries);
            }
            case 91: {
                return new ArrayElementValueGen((ArrayElementValue)value, cpool, copyPoolEntries);
            }
            case 99: {
                return new ClassElementValueGen((ClassElementValue)value, cpool, copyPoolEntries);
            }
        }
        throw new UnsupportedOperationException("Not implemented yet! (" + value.getElementValueType() + ")");
    }

    public static ElementValueGen readElementValue(DataInput dis, ConstantPoolGen cpGen) throws IOException {
        int type = dis.readUnsignedByte();
        switch (type) {
            case 66: {
                return new SimpleElementValueGen(66, dis.readUnsignedShort(), cpGen);
            }
            case 67: {
                return new SimpleElementValueGen(67, dis.readUnsignedShort(), cpGen);
            }
            case 68: {
                return new SimpleElementValueGen(68, dis.readUnsignedShort(), cpGen);
            }
            case 70: {
                return new SimpleElementValueGen(70, dis.readUnsignedShort(), cpGen);
            }
            case 73: {
                return new SimpleElementValueGen(73, dis.readUnsignedShort(), cpGen);
            }
            case 74: {
                return new SimpleElementValueGen(74, dis.readUnsignedShort(), cpGen);
            }
            case 83: {
                return new SimpleElementValueGen(83, dis.readUnsignedShort(), cpGen);
            }
            case 90: {
                return new SimpleElementValueGen(90, dis.readUnsignedShort(), cpGen);
            }
            case 115: {
                return new SimpleElementValueGen(115, dis.readUnsignedShort(), cpGen);
            }
            case 101: {
                return new EnumElementValueGen(dis.readUnsignedShort(), dis.readUnsignedShort(), cpGen);
            }
            case 99: {
                return new ClassElementValueGen(dis.readUnsignedShort(), cpGen);
            }
            case 64: {
                return new AnnotationElementValueGen(64, new AnnotationEntryGen(AnnotationEntry.read(dis, cpGen.getConstantPool(), true), cpGen, false), cpGen);
            }
            case 91: {
                int numArrayVals = dis.readUnsignedShort();
                ElementValue[] evalues = new ElementValue[numArrayVals];
                for (int j = 0; j < numArrayVals; ++j) {
                    evalues[j] = ElementValue.readElementValue(dis, cpGen.getConstantPool());
                }
                return new ArrayElementValueGen(91, evalues, cpGen);
            }
        }
        throw new IllegalArgumentException("Unexpected element value kind in annotation: " + type);
    }

    protected ElementValueGen(int type, ConstantPoolGen cpGen) {
        this.type = type;
        this.cpGen = cpGen;
    }

    public abstract void dump(DataOutputStream var1) throws IOException;

    protected ConstantPoolGen getConstantPool() {
        return this.cpGen;
    }

    public abstract ElementValue getElementValue();

    public int getElementValueType() {
        return this.type;
    }

    public abstract String stringifyValue();
}

