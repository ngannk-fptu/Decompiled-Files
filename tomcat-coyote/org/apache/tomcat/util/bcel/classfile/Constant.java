/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantClass;
import org.apache.tomcat.util.bcel.classfile.ConstantDouble;
import org.apache.tomcat.util.bcel.classfile.ConstantFloat;
import org.apache.tomcat.util.bcel.classfile.ConstantInteger;
import org.apache.tomcat.util.bcel.classfile.ConstantLong;
import org.apache.tomcat.util.bcel.classfile.ConstantUtf8;
import org.apache.tomcat.util.bcel.classfile.Utility;

public abstract class Constant {
    private final byte tag;

    static Constant readConstant(DataInput dataInput) throws IOException, ClassFormatException {
        int skipSize;
        byte b = dataInput.readByte();
        switch (b) {
            case 7: {
                return new ConstantClass(dataInput);
            }
            case 3: {
                return new ConstantInteger(dataInput);
            }
            case 4: {
                return new ConstantFloat(dataInput);
            }
            case 5: {
                return new ConstantLong(dataInput);
            }
            case 6: {
                return new ConstantDouble(dataInput);
            }
            case 1: {
                return ConstantUtf8.getInstance(dataInput);
            }
            case 8: 
            case 16: 
            case 19: 
            case 20: {
                skipSize = 2;
                break;
            }
            case 15: {
                skipSize = 3;
                break;
            }
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 17: 
            case 18: {
                skipSize = 4;
                break;
            }
            default: {
                throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
            }
        }
        Utility.skipFully(dataInput, skipSize);
        return null;
    }

    Constant(byte tag) {
        this.tag = tag;
    }

    public final byte getTag() {
        return this.tag;
    }

    public String toString() {
        return "[" + this.tag + "]";
    }
}

