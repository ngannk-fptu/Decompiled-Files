/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantDynamic;
import org.aspectj.apache.bcel.classfile.ConstantFieldref;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.aspectj.apache.bcel.classfile.ConstantInvokeDynamic;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantMethodHandle;
import org.aspectj.apache.bcel.classfile.ConstantMethodType;
import org.aspectj.apache.bcel.classfile.ConstantMethodref;
import org.aspectj.apache.bcel.classfile.ConstantModule;
import org.aspectj.apache.bcel.classfile.ConstantNameAndType;
import org.aspectj.apache.bcel.classfile.ConstantPackage;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Node;

public abstract class Constant
implements Cloneable,
Node {
    protected byte tag;

    Constant(byte tag) {
        this.tag = tag;
    }

    public final byte getTag() {
        return this.tag;
    }

    public String toString() {
        return Constants.CONSTANT_NAMES[this.tag] + "[" + this.tag + "]";
    }

    @Override
    public abstract void accept(ClassVisitor var1);

    public abstract void dump(DataOutputStream var1) throws IOException;

    public abstract Object getValue();

    public Constant copy() {
        try {
            return (Constant)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    static final Constant readConstant(DataInputStream dis) throws IOException, ClassFormatException {
        byte b = dis.readByte();
        switch (b) {
            case 7: {
                return new ConstantClass(dis);
            }
            case 12: {
                return new ConstantNameAndType(dis);
            }
            case 1: {
                return new ConstantUtf8(dis);
            }
            case 9: {
                return new ConstantFieldref(dis);
            }
            case 10: {
                return new ConstantMethodref(dis);
            }
            case 11: {
                return new ConstantInterfaceMethodref(dis);
            }
            case 8: {
                return new ConstantString(dis);
            }
            case 3: {
                return new ConstantInteger(dis);
            }
            case 4: {
                return new ConstantFloat(dis);
            }
            case 5: {
                return new ConstantLong(dis);
            }
            case 6: {
                return new ConstantDouble(dis);
            }
            case 15: {
                return new ConstantMethodHandle(dis);
            }
            case 16: {
                return new ConstantMethodType(dis);
            }
            case 18: {
                return new ConstantInvokeDynamic(dis);
            }
            case 19: {
                return new ConstantModule(dis);
            }
            case 20: {
                return new ConstantPackage(dis);
            }
            case 17: {
                return new ConstantDynamic(dis);
            }
        }
        throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
    }
}

