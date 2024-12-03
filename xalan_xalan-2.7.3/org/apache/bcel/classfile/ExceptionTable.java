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
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;
import org.apache.commons.lang3.ArrayUtils;

public final class ExceptionTable
extends Attribute {
    private int[] exceptionIndexTable;

    public ExceptionTable(ExceptionTable c) {
        this(c.getNameIndex(), c.getLength(), c.getExceptionIndexTable(), c.getConstantPool());
    }

    ExceptionTable(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (int[])null, constantPool);
        int exceptionCount = input.readUnsignedShort();
        this.exceptionIndexTable = new int[exceptionCount];
        for (int i = 0; i < exceptionCount; ++i) {
            this.exceptionIndexTable[i] = input.readUnsignedShort();
        }
    }

    public ExceptionTable(int nameIndex, int length, int[] exceptionIndexTable, ConstantPool constantPool) {
        super((byte)3, nameIndex, length, constantPool);
        this.exceptionIndexTable = exceptionIndexTable != null ? exceptionIndexTable : ArrayUtils.EMPTY_INT_ARRAY;
        Args.requireU2(this.exceptionIndexTable.length, "exceptionIndexTable.length");
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionTable(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        ExceptionTable c = (ExceptionTable)this.clone();
        if (this.exceptionIndexTable != null) {
            c.exceptionIndexTable = (int[])this.exceptionIndexTable.clone();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.exceptionIndexTable.length);
        for (int index : this.exceptionIndexTable) {
            file.writeShort(index);
        }
    }

    public int[] getExceptionIndexTable() {
        return this.exceptionIndexTable;
    }

    public String[] getExceptionNames() {
        String[] names = new String[this.exceptionIndexTable.length];
        Arrays.setAll(names, i -> Utility.pathToPackage(super.getConstantPool().getConstantString(this.exceptionIndexTable[i], (byte)7)));
        return names;
    }

    public int getNumberOfExceptions() {
        return this.exceptionIndexTable == null ? 0 : this.exceptionIndexTable.length;
    }

    public void setExceptionIndexTable(int[] exceptionIndexTable) {
        this.exceptionIndexTable = exceptionIndexTable != null ? exceptionIndexTable : ArrayUtils.EMPTY_INT_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Exceptions: ");
        for (int i = 0; i < this.exceptionIndexTable.length; ++i) {
            String str = super.getConstantPool().getConstantString(this.exceptionIndexTable[i], (byte)7);
            buf.append(Utility.compactClassName(str, false));
            if (i >= this.exceptionIndexTable.length - 1) continue;
            buf.append(", ");
        }
        return buf.toString();
    }
}

