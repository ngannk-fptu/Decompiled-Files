/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.LocalVariable;

public class LocalVariableTable
extends Attribute {
    private boolean isInPackedState = false;
    private byte[] data;
    private int localVariableTableLength;
    private LocalVariable[] localVariableTable;

    public LocalVariableTable(LocalVariableTable c) {
        this(c.getNameIndex(), c.getLength(), c.getLocalVariableTable(), c.getConstantPool());
    }

    public LocalVariableTable(int name_index, int length, LocalVariable[] local_variable_table, ConstantPool constant_pool) {
        super((byte)5, name_index, length, constant_pool);
        this.setLocalVariableTable(local_variable_table);
    }

    LocalVariableTable(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        super((byte)5, name_index, length, constant_pool);
        this.data = new byte[length];
        file.readFully(this.data);
        this.isInPackedState = true;
    }

    @Override
    public void accept(ClassVisitor v) {
        this.unpack();
        v.visitLocalVariableTable(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        if (this.isInPackedState) {
            file.write(this.data);
        } else {
            file.writeShort(this.localVariableTableLength);
            for (int i = 0; i < this.localVariableTableLength; ++i) {
                this.localVariableTable[i].dump(file);
            }
        }
    }

    public final LocalVariable[] getLocalVariableTable() {
        this.unpack();
        return this.localVariableTable;
    }

    public final LocalVariable getLocalVariable(int index) {
        this.unpack();
        for (int i = 0; i < this.localVariableTableLength; ++i) {
            if (this.localVariableTable[i] == null || this.localVariableTable[i].getIndex() != index) continue;
            return this.localVariableTable[i];
        }
        return null;
    }

    public final void setLocalVariableTable(LocalVariable[] local_variable_table) {
        this.data = null;
        this.isInPackedState = false;
        this.localVariableTable = local_variable_table;
        this.localVariableTableLength = local_variable_table == null ? 0 : local_variable_table.length;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer("");
        this.unpack();
        for (int i = 0; i < this.localVariableTableLength; ++i) {
            buf.append(this.localVariableTable[i].toString());
            if (i >= this.localVariableTableLength - 1) continue;
            buf.append('\n');
        }
        return buf.toString();
    }

    public final int getTableLength() {
        this.unpack();
        return this.localVariableTableLength;
    }

    private void unpack() {
        if (!this.isInPackedState) {
            return;
        }
        try {
            ByteArrayInputStream bs = new ByteArrayInputStream(this.data);
            DataInputStream dis = new DataInputStream(bs);
            this.localVariableTableLength = dis.readUnsignedShort();
            this.localVariableTable = new LocalVariable[this.localVariableTableLength];
            for (int i = 0; i < this.localVariableTableLength; ++i) {
                this.localVariableTable[i] = new LocalVariable(dis, this.cpool);
            }
            dis.close();
            this.data = null;
        }
        catch (IOException e) {
            throw new RuntimeException("Unpacking of LocalVariableTable attribute failed");
        }
        this.isInPackedState = false;
    }
}

