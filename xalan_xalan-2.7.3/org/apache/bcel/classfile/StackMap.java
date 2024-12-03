/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class StackMap
extends Attribute {
    private StackMapEntry[] table;

    StackMap(int nameIndex, int length, DataInput dataInput, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (StackMapEntry[])null, constantPool);
        int mapLength = dataInput.readUnsignedShort();
        this.table = new StackMapEntry[mapLength];
        for (int i = 0; i < mapLength; ++i) {
            this.table[i] = new StackMapEntry(dataInput, constantPool);
        }
    }

    public StackMap(int nameIndex, int length, StackMapEntry[] table, ConstantPool constantPool) {
        super((byte)11, nameIndex, length, constantPool);
        this.table = table != null ? table : StackMapEntry.EMPTY_ARRAY;
        Args.requireU2(this.table.length, "table.length");
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackMap(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        StackMap c = (StackMap)this.clone();
        c.table = new StackMapEntry[this.table.length];
        Arrays.setAll(c.table, i -> this.table[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.table.length);
        for (StackMapEntry entry : this.table) {
            entry.dump(file);
        }
    }

    public int getMapLength() {
        return this.table.length;
    }

    public StackMapEntry[] getStackMap() {
        return this.table;
    }

    public void setStackMap(StackMapEntry[] table) {
        this.table = table != null ? table : StackMapEntry.EMPTY_ARRAY;
        int len = 2;
        for (StackMapEntry element : this.table) {
            len += element.getMapEntrySize();
        }
        this.setLength(len);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("StackMap(");
        int runningOffset = -1;
        for (int i = 0; i < this.table.length; ++i) {
            runningOffset = this.table[i].getByteCodeOffset() + runningOffset + 1;
            buf.append(String.format("%n@%03d %s", runningOffset, this.table[i]));
            if (i >= this.table.length - 1) continue;
            buf.append(", ");
        }
        buf.append(')');
        return buf.toString();
    }
}

