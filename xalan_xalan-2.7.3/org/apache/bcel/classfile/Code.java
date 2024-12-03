/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;
import org.apache.commons.lang3.ArrayUtils;

public final class Code
extends Attribute {
    private int maxStack;
    private int maxLocals;
    private byte[] code;
    private CodeException[] exceptionTable;
    private Attribute[] attributes;

    public Code(Code code) {
        this(code.getNameIndex(), code.getLength(), code.getMaxStack(), code.getMaxLocals(), code.getCode(), code.getExceptionTable(), code.getAttributes(), code.getConstantPool());
    }

    Code(int nameIndex, int length, DataInput file, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, file.readUnsignedShort(), file.readUnsignedShort(), null, null, null, constantPool);
        int codeLength = Args.requireU4(file.readInt(), 1, "Code length attribute");
        this.code = new byte[codeLength];
        file.readFully(this.code);
        int exceptionTableLength = file.readUnsignedShort();
        this.exceptionTable = new CodeException[exceptionTableLength];
        for (int i = 0; i < exceptionTableLength; ++i) {
            this.exceptionTable[i] = new CodeException(file);
        }
        int attributesCount = file.readUnsignedShort();
        this.attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; ++i) {
            this.attributes[i] = Attribute.readAttribute(file, constantPool);
        }
        super.setLength(length);
    }

    public Code(int nameIndex, int length, int maxStack, int maxLocals, byte[] code, CodeException[] exceptionTable, Attribute[] attributes, ConstantPool constantPool) {
        super((byte)2, nameIndex, length, constantPool);
        this.maxStack = Args.requireU2(maxStack, "maxStack");
        this.maxLocals = Args.requireU2(maxLocals, "maxLocals");
        this.code = code != null ? code : ArrayUtils.EMPTY_BYTE_ARRAY;
        this.exceptionTable = exceptionTable != null ? exceptionTable : CodeException.EMPTY_CODE_EXCEPTION_ARRAY;
        Args.requireU2(this.exceptionTable.length, "exceptionTable.length");
        this.attributes = attributes != null ? attributes : EMPTY_ARRAY;
        super.setLength(this.calculateLength());
    }

    @Override
    public void accept(Visitor v) {
        v.visitCode(this);
    }

    private int calculateLength() {
        int len = 0;
        if (this.attributes != null) {
            for (Attribute attribute : this.attributes) {
                len += attribute.getLength() + 6;
            }
        }
        return len + this.getInternalLength();
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        Code c = (Code)this.clone();
        if (this.code != null) {
            c.code = (byte[])this.code.clone();
        }
        c.setConstantPool(constantPool);
        c.exceptionTable = new CodeException[this.exceptionTable.length];
        Arrays.setAll(c.exceptionTable, i -> this.exceptionTable[i].copy());
        c.attributes = new Attribute[this.attributes.length];
        Arrays.setAll(c.attributes, i -> this.attributes[i].copy(constantPool));
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.maxStack);
        file.writeShort(this.maxLocals);
        file.writeInt(this.code.length);
        file.write(this.code, 0, this.code.length);
        file.writeShort(this.exceptionTable.length);
        for (CodeException codeException : this.exceptionTable) {
            codeException.dump(file);
        }
        file.writeShort(this.attributes.length);
        for (Node node : this.attributes) {
            ((Attribute)node).dump(file);
        }
    }

    public Attribute[] getAttributes() {
        return this.attributes;
    }

    public byte[] getCode() {
        return this.code;
    }

    public CodeException[] getExceptionTable() {
        return this.exceptionTable;
    }

    private int getInternalLength() {
        return 8 + this.code.length + 2 + 8 * (this.exceptionTable == null ? 0 : this.exceptionTable.length) + 2;
    }

    public LineNumberTable getLineNumberTable() {
        for (Attribute attribute : this.attributes) {
            if (!(attribute instanceof LineNumberTable)) continue;
            return (LineNumberTable)attribute;
        }
        return null;
    }

    public LocalVariableTable getLocalVariableTable() {
        for (Attribute attribute : this.attributes) {
            if (!(attribute instanceof LocalVariableTable)) continue;
            return (LocalVariableTable)attribute;
        }
        return null;
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes != null ? attributes : EMPTY_ARRAY;
        super.setLength(this.calculateLength());
    }

    public void setCode(byte[] code) {
        this.code = code != null ? code : ArrayUtils.EMPTY_BYTE_ARRAY;
        super.setLength(this.calculateLength());
    }

    public void setExceptionTable(CodeException[] exceptionTable) {
        this.exceptionTable = exceptionTable != null ? exceptionTable : CodeException.EMPTY_CODE_EXCEPTION_ARRAY;
        super.setLength(this.calculateLength());
    }

    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean verbose) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("Code(maxStack = ").append(this.maxStack).append(", maxLocals = ").append(this.maxLocals).append(", code_length = ").append(this.code.length).append(")\n").append(Utility.codeToString(this.code, super.getConstantPool(), 0, -1, verbose));
        if (this.exceptionTable.length > 0) {
            buf.append("\nException handler(s) = \n").append("From\tTo\tHandler\tType\n");
            for (Node node : this.exceptionTable) {
                buf.append(((CodeException)node).toString(super.getConstantPool(), verbose)).append("\n");
            }
        }
        if (this.attributes.length > 0) {
            buf.append("\nAttribute(s) = ");
            for (Node node : this.attributes) {
                buf.append("\n").append(((Attribute)node).getName()).append(":");
                buf.append("\n").append(node);
            }
        }
        return buf.toString();
    }
}

