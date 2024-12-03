/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public class LocalVariableTable
extends Attribute
implements Iterable<LocalVariable> {
    private LocalVariable[] localVariableTable;

    LocalVariableTable(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (LocalVariable[])null, constantPool);
        int localVariableTableLength = input.readUnsignedShort();
        this.localVariableTable = new LocalVariable[localVariableTableLength];
        for (int i = 0; i < localVariableTableLength; ++i) {
            this.localVariableTable[i] = new LocalVariable(input, constantPool);
        }
    }

    public LocalVariableTable(int nameIndex, int length, LocalVariable[] localVariableTable, ConstantPool constantPool) {
        super((byte)5, nameIndex, length, constantPool);
        this.localVariableTable = localVariableTable != null ? localVariableTable : LocalVariable.EMPTY_ARRAY;
        Args.requireU2(this.localVariableTable.length, "localVariableTable.length");
    }

    public LocalVariableTable(LocalVariableTable c) {
        this(c.getNameIndex(), c.getLength(), c.getLocalVariableTable(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitLocalVariableTable(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        LocalVariableTable c = (LocalVariableTable)this.clone();
        c.localVariableTable = new LocalVariable[this.localVariableTable.length];
        Arrays.setAll(c.localVariableTable, i -> this.localVariableTable[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.localVariableTable.length);
        for (LocalVariable variable : this.localVariableTable) {
            variable.dump(file);
        }
    }

    @Deprecated
    public final LocalVariable getLocalVariable(int index) {
        for (LocalVariable variable : this.localVariableTable) {
            if (variable.getIndex() != index) continue;
            return variable;
        }
        return null;
    }

    public final LocalVariable getLocalVariable(int index, int pc) {
        for (LocalVariable variable : this.localVariableTable) {
            if (variable.getIndex() != index) continue;
            int startPc = variable.getStartPC();
            int endPc = startPc + variable.getLength();
            if (pc < startPc || pc > endPc) continue;
            return variable;
        }
        return null;
    }

    public final LocalVariable[] getLocalVariableTable() {
        return this.localVariableTable;
    }

    public final int getTableLength() {
        return this.localVariableTable == null ? 0 : this.localVariableTable.length;
    }

    @Override
    public Iterator<LocalVariable> iterator() {
        return Stream.of(this.localVariableTable).iterator();
    }

    public final void setLocalVariableTable(LocalVariable[] localVariableTable) {
        this.localVariableTable = localVariableTable;
    }

    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.localVariableTable.length; ++i) {
            buf.append(this.localVariableTable[i]);
            if (i >= this.localVariableTable.length - 1) continue;
            buf.append('\n');
        }
        return buf.toString();
    }
}

