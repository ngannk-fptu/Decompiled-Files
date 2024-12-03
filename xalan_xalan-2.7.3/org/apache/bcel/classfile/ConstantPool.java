/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantDynamic;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodHandle;
import org.apache.bcel.classfile.ConstantMethodType;
import org.apache.bcel.classfile.ConstantModule;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPackage;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public class ConstantPool
implements Cloneable,
Node,
Iterable<Constant> {
    private Constant[] constantPool;

    private static String escape(String str) {
        int len = str.length();
        StringBuilder buf = new StringBuilder(len + 5);
        char[] ch = str.toCharArray();
        block7: for (int i = 0; i < len; ++i) {
            switch (ch[i]) {
                case '\n': {
                    buf.append("\\n");
                    continue block7;
                }
                case '\r': {
                    buf.append("\\r");
                    continue block7;
                }
                case '\t': {
                    buf.append("\\t");
                    continue block7;
                }
                case '\b': {
                    buf.append("\\b");
                    continue block7;
                }
                case '\"': {
                    buf.append("\\\"");
                    continue block7;
                }
                default: {
                    buf.append(ch[i]);
                }
            }
        }
        return buf.toString();
    }

    public ConstantPool(Constant[] constantPool) {
        this.constantPool = constantPool;
    }

    public ConstantPool(DataInput input) throws IOException {
        int constantPoolCount = input.readUnsignedShort();
        this.constantPool = new Constant[constantPoolCount];
        for (int i = 1; i < constantPoolCount; ++i) {
            this.constantPool[i] = Constant.readConstant(input);
            byte tag = this.constantPool[i].getTag();
            if (tag != 6 && tag != 5) continue;
            ++i;
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantPool(this);
    }

    public String constantToString(Constant c) throws IllegalArgumentException {
        String str;
        byte tag = c.getTag();
        switch (tag) {
            case 7: {
                int i = ((ConstantClass)c).getNameIndex();
                c = this.getConstantUtf8(i);
                str = Utility.compactClassName(((ConstantUtf8)c).getBytes(), false);
                break;
            }
            case 8: {
                int i = ((ConstantString)c).getStringIndex();
                c = this.getConstantUtf8(i);
                str = "\"" + ConstantPool.escape(((ConstantUtf8)c).getBytes()) + "\"";
                break;
            }
            case 1: {
                str = ((ConstantUtf8)c).getBytes();
                break;
            }
            case 6: {
                str = String.valueOf(((ConstantDouble)c).getBytes());
                break;
            }
            case 4: {
                str = String.valueOf(((ConstantFloat)c).getBytes());
                break;
            }
            case 5: {
                str = String.valueOf(((ConstantLong)c).getBytes());
                break;
            }
            case 3: {
                str = String.valueOf(((ConstantInteger)c).getBytes());
                break;
            }
            case 12: {
                str = this.constantToString(((ConstantNameAndType)c).getNameIndex(), (byte)1) + " " + this.constantToString(((ConstantNameAndType)c).getSignatureIndex(), (byte)1);
                break;
            }
            case 9: 
            case 10: 
            case 11: {
                str = this.constantToString(((ConstantCP)c).getClassIndex(), (byte)7) + "." + this.constantToString(((ConstantCP)c).getNameAndTypeIndex(), (byte)12);
                break;
            }
            case 15: {
                ConstantMethodHandle cmh = (ConstantMethodHandle)c;
                str = Const.getMethodHandleName(cmh.getReferenceKind()) + " " + this.constantToString(cmh.getReferenceIndex(), ((Constant)this.getConstant(cmh.getReferenceIndex())).getTag());
                break;
            }
            case 16: {
                ConstantMethodType cmt = (ConstantMethodType)c;
                str = this.constantToString(cmt.getDescriptorIndex(), (byte)1);
                break;
            }
            case 18: {
                ConstantInvokeDynamic cid = (ConstantInvokeDynamic)c;
                str = cid.getBootstrapMethodAttrIndex() + ":" + this.constantToString(cid.getNameAndTypeIndex(), (byte)12);
                break;
            }
            case 17: {
                ConstantDynamic cd = (ConstantDynamic)c;
                str = cd.getBootstrapMethodAttrIndex() + ":" + this.constantToString(cd.getNameAndTypeIndex(), (byte)12);
                break;
            }
            case 19: {
                int i = ((ConstantModule)c).getNameIndex();
                c = this.getConstantUtf8(i);
                str = Utility.compactClassName(((ConstantUtf8)c).getBytes(), false);
                break;
            }
            case 20: {
                int i = ((ConstantPackage)c).getNameIndex();
                c = this.getConstantUtf8(i);
                str = Utility.compactClassName(((ConstantUtf8)c).getBytes(), false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown constant type " + tag);
            }
        }
        return str;
    }

    public String constantToString(int index, byte tag) {
        return this.constantToString((Constant)this.getConstant(index, tag));
    }

    public ConstantPool copy() {
        ConstantPool c = null;
        try {
            c = (ConstantPool)this.clone();
            c.constantPool = new Constant[this.constantPool.length];
            for (int i = 1; i < this.constantPool.length; ++i) {
                if (this.constantPool[i] == null) continue;
                c.constantPool[i] = this.constantPool[i].copy();
            }
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return c;
    }

    public void dump(DataOutputStream file) throws IOException {
        int size = Math.min(this.constantPool.length, 65535);
        file.writeShort(size);
        for (int i = 1; i < size; ++i) {
            if (this.constantPool[i] == null) continue;
            this.constantPool[i].dump(file);
        }
    }

    public <T extends Constant> T getConstant(int index) throws ClassFormatException {
        return (T)this.getConstant(index, Constant.class);
    }

    public <T extends Constant> T getConstant(int index, byte tag) throws ClassFormatException {
        return (T)this.getConstant(index, tag, Constant.class);
    }

    public <T extends Constant> T getConstant(int index, byte tag, Class<T> castTo) throws ClassFormatException {
        T c = this.getConstant(index);
        if (((Constant)c).getTag() != tag) {
            throw new ClassFormatException("Expected class '" + Const.getConstantName(tag) + "' at index " + index + " and got " + c);
        }
        return c;
    }

    public <T extends Constant> T getConstant(int index, Class<T> castTo) throws ClassFormatException {
        Constant prev;
        if (index >= this.constantPool.length || index < 0) {
            throw new ClassFormatException("Invalid constant pool reference using index: " + index + ". Constant pool size is: " + this.constantPool.length);
        }
        if (this.constantPool[index] != null && !castTo.isAssignableFrom(this.constantPool[index].getClass())) {
            throw new ClassFormatException("Invalid constant pool reference at index: " + index + ". Expected " + castTo + " but was " + this.constantPool[index].getClass());
        }
        Constant c = (Constant)castTo.cast(this.constantPool[index]);
        if (c == null && index != 0 && ((prev = this.constantPool[index - 1]) == null || prev.getTag() != 6 && prev.getTag() != 5)) {
            throw new ClassFormatException("Constant pool at index " + index + " is null.");
        }
        return (T)c;
    }

    public ConstantInteger getConstantInteger(int index) {
        return this.getConstant(index, (byte)3, ConstantInteger.class);
    }

    public Constant[] getConstantPool() {
        return this.constantPool;
    }

    public String getConstantString(int index, byte tag) throws IllegalArgumentException {
        int i;
        switch (tag) {
            case 7: {
                i = this.getConstant(index, ConstantClass.class).getNameIndex();
                break;
            }
            case 8: {
                i = this.getConstant(index, ConstantString.class).getStringIndex();
                break;
            }
            case 19: {
                i = this.getConstant(index, ConstantModule.class).getNameIndex();
                break;
            }
            case 20: {
                i = this.getConstant(index, ConstantPackage.class).getNameIndex();
                break;
            }
            case 1: {
                return this.getConstantUtf8(index).getBytes();
            }
            default: {
                throw new IllegalArgumentException("getConstantString called with illegal tag " + tag);
            }
        }
        return this.getConstantUtf8(i).getBytes();
    }

    public ConstantUtf8 getConstantUtf8(int index) throws ClassFormatException {
        return this.getConstant(index, (byte)1, ConstantUtf8.class);
    }

    public int getLength() {
        return this.constantPool == null ? 0 : this.constantPool.length;
    }

    @Override
    public Iterator<Constant> iterator() {
        return Arrays.stream(this.constantPool).iterator();
    }

    public void setConstant(int index, Constant constant) {
        this.constantPool[index] = constant;
    }

    public void setConstantPool(Constant[] constantPool) {
        this.constantPool = constantPool;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < this.constantPool.length; ++i) {
            buf.append(i).append(")").append(this.constantPool[i]).append("\n");
        }
        return buf.toString();
    }
}

