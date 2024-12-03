/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantDynamic;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodHandle;
import org.apache.bcel.classfile.ConstantMethodType;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantModule;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPackage;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.BCELComparator;

public abstract class Constant
implements Cloneable,
Node {
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            Constant THIS = (Constant)o1;
            Constant THAT = (Constant)o2;
            return Objects.equals(THIS.toString(), THAT.toString());
        }

        @Override
        public int hashCode(Object o) {
            Constant THIS = (Constant)o;
            return THIS.toString().hashCode();
        }
    };
    @Deprecated
    protected byte tag;

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static Constant readConstant(DataInput dataInput) throws IOException, ClassFormatException {
        byte b = dataInput.readByte();
        switch (b) {
            case 7: {
                return new ConstantClass(dataInput);
            }
            case 9: {
                return new ConstantFieldref(dataInput);
            }
            case 10: {
                return new ConstantMethodref(dataInput);
            }
            case 11: {
                return new ConstantInterfaceMethodref(dataInput);
            }
            case 8: {
                return new ConstantString(dataInput);
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
            case 12: {
                return new ConstantNameAndType(dataInput);
            }
            case 1: {
                return ConstantUtf8.getInstance(dataInput);
            }
            case 15: {
                return new ConstantMethodHandle(dataInput);
            }
            case 16: {
                return new ConstantMethodType(dataInput);
            }
            case 17: {
                return new ConstantDynamic(dataInput);
            }
            case 18: {
                return new ConstantInvokeDynamic(dataInput);
            }
            case 19: {
                return new ConstantModule(dataInput);
            }
            case 20: {
                return new ConstantPackage(dataInput);
            }
        }
        throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    Constant(byte tag) {
        this.tag = tag;
    }

    @Override
    public abstract void accept(Visitor var1);

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
    }

    public Constant copy() {
        try {
            return (Constant)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public abstract void dump(DataOutputStream var1) throws IOException;

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public final byte getTag() {
        return this.tag;
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public String toString() {
        return Const.getConstantName(this.tag) + "[" + this.tag + "]";
    }
}

