/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.AnnotationElementValue;
import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.ArrayElementValue;
import org.apache.tomcat.util.bcel.classfile.ClassElementValue;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.EnumElementValue;
import org.apache.tomcat.util.bcel.classfile.SimpleElementValue;

public abstract class ElementValue {
    public static final byte STRING = 115;
    public static final byte ENUM_CONSTANT = 101;
    public static final byte CLASS = 99;
    public static final byte ANNOTATION = 64;
    public static final byte ARRAY = 91;
    public static final byte PRIMITIVE_INT = 73;
    public static final byte PRIMITIVE_BYTE = 66;
    public static final byte PRIMITIVE_CHAR = 67;
    public static final byte PRIMITIVE_DOUBLE = 68;
    public static final byte PRIMITIVE_FLOAT = 70;
    public static final byte PRIMITIVE_LONG = 74;
    public static final byte PRIMITIVE_SHORT = 83;
    public static final byte PRIMITIVE_BOOLEAN = 90;
    private final int type;
    private final ConstantPool cpool;

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool) throws IOException {
        return ElementValue.readElementValue(input, cpool, 0);
    }

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool, int arrayNesting) throws IOException {
        byte tag = input.readByte();
        switch (tag) {
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 115: {
                return new SimpleElementValue(tag, input.readUnsignedShort(), cpool);
            }
            case 101: {
                input.readUnsignedShort();
                return new EnumElementValue(101, input.readUnsignedShort(), cpool);
            }
            case 99: {
                return new ClassElementValue(99, input.readUnsignedShort(), cpool);
            }
            case 64: {
                return new AnnotationElementValue(64, new AnnotationEntry(input, cpool), cpool);
            }
            case 91: {
                if (++arrayNesting > 255) {
                    throw new ClassFormatException(String.format("Arrays are only valid if they represent %,d or fewer dimensions.", 255));
                }
                int numArrayVals = input.readUnsignedShort();
                ElementValue[] evalues = new ElementValue[numArrayVals];
                for (int j = 0; j < numArrayVals; ++j) {
                    evalues[j] = ElementValue.readElementValue(input, cpool, arrayNesting);
                }
                return new ArrayElementValue(91, evalues, cpool);
            }
        }
        throw new ClassFormatException("Unexpected element value kind in annotation: " + tag);
    }

    ElementValue(int type, ConstantPool cpool) {
        this.type = type;
        this.cpool = cpool;
    }

    final ConstantPool getConstantPool() {
        return this.cpool;
    }

    final int getType() {
        return this.type;
    }

    public abstract String stringifyValue();
}

