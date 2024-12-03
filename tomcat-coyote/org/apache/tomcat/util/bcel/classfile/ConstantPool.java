/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.apache.tomcat.util.bcel.classfile.ConstantInteger;
import org.apache.tomcat.util.bcel.classfile.ConstantUtf8;

public class ConstantPool {
    private final Constant[] constantPool;

    ConstantPool(DataInput input) throws IOException, ClassFormatException {
        int constantPoolCount = input.readUnsignedShort();
        this.constantPool = new Constant[constantPoolCount];
        for (int i = 1; i < constantPoolCount; ++i) {
            byte tag;
            this.constantPool[i] = Constant.readConstant(input);
            if (this.constantPool[i] == null || (tag = this.constantPool[i].getTag()) != 6 && tag != 5) continue;
            ++i;
        }
    }

    public <T extends Constant> T getConstant(int index) throws ClassFormatException {
        return (T)this.getConstant(index, Constant.class);
    }

    public <T extends Constant> T getConstant(int index, byte tag) throws ClassFormatException {
        T c = this.getConstant(index);
        if (((Constant)c).getTag() != tag) {
            throw new ClassFormatException("Expected class '" + Const.getConstantName(tag) + "' at index " + index + " and got " + c);
        }
        return c;
    }

    public <T extends Constant> T getConstant(int index, Class<T> castTo) throws ClassFormatException {
        Constant prev;
        if (index >= this.constantPool.length || index < 1) {
            throw new ClassFormatException("Invalid constant pool reference using index: " + index + ". Constant pool size is: " + this.constantPool.length);
        }
        if (this.constantPool[index] != null && !castTo.isAssignableFrom(this.constantPool[index].getClass())) {
            throw new ClassFormatException("Invalid constant pool reference at index: " + index + ". Expected " + castTo + " but was " + this.constantPool[index].getClass());
        }
        if (index > 1 && (prev = this.constantPool[index - 1]) != null && (prev.getTag() == 6 || prev.getTag() == 5)) {
            throw new ClassFormatException("Constant pool at index " + index + " is invalid. The index is unused due to the preceeding " + Const.getConstantName(prev.getTag()) + ".");
        }
        Constant c = (Constant)castTo.cast(this.constantPool[index]);
        if (c == null) {
            throw new ClassFormatException("Constant pool at index " + index + " is null.");
        }
        return (T)c;
    }

    public ConstantInteger getConstantInteger(int index) {
        return (ConstantInteger)this.getConstant(index, (byte)3);
    }

    public ConstantUtf8 getConstantUtf8(int index) throws ClassFormatException {
        return (ConstantUtf8)this.getConstant(index, (byte)1);
    }
}

