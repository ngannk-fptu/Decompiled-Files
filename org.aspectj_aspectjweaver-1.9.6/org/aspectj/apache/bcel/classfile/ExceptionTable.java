/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Utility;

public final class ExceptionTable
extends Attribute {
    private int number_of_exceptions;
    private int[] exception_index_table;

    public ExceptionTable(ExceptionTable c) {
        this(c.getNameIndex(), c.getLength(), c.getExceptionIndexTable(), c.getConstantPool());
    }

    public ExceptionTable(int name_index, int length, int[] exception_index_table, ConstantPool constant_pool) {
        super((byte)3, name_index, length, constant_pool);
        this.setExceptionIndexTable(exception_index_table);
    }

    ExceptionTable(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (int[])null, constant_pool);
        this.number_of_exceptions = file.readUnsignedShort();
        this.exception_index_table = new int[this.number_of_exceptions];
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            this.exception_index_table[i] = file.readUnsignedShort();
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitExceptionTable(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.number_of_exceptions);
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            file.writeShort(this.exception_index_table[i]);
        }
    }

    public final int[] getExceptionIndexTable() {
        return this.exception_index_table;
    }

    public final int getNumberOfExceptions() {
        return this.number_of_exceptions;
    }

    public final String[] getExceptionNames() {
        String[] names = new String[this.number_of_exceptions];
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            names[i] = this.cpool.getConstantString(this.exception_index_table[i], (byte)7).replace('/', '.');
        }
        return names;
    }

    public final void setExceptionIndexTable(int[] exception_index_table) {
        this.exception_index_table = exception_index_table;
        this.number_of_exceptions = exception_index_table == null ? 0 : exception_index_table.length;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            String str = this.cpool.getConstantString(this.exception_index_table[i], (byte)7);
            buf.append(Utility.compactClassName(str, false));
            if (i >= this.number_of_exceptions - 1) continue;
            buf.append(", ");
        }
        return buf.toString();
    }
}

