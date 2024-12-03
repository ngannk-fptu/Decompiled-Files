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

public class LocalVariableTypeTable
extends Attribute
implements Iterable<LocalVariable> {
    private LocalVariable[] localVariableTypeTable;

    LocalVariableTypeTable(int nameIdx, int len, DataInput input, ConstantPool cpool) throws IOException {
        this(nameIdx, len, (LocalVariable[])null, cpool);
        int localVariableTypeTableLength = input.readUnsignedShort();
        this.localVariableTypeTable = new LocalVariable[localVariableTypeTableLength];
        for (int i = 0; i < localVariableTypeTableLength; ++i) {
            this.localVariableTypeTable[i] = new LocalVariable(input, cpool);
        }
    }

    public LocalVariableTypeTable(int nameIndex, int length, LocalVariable[] localVariableTypeTable, ConstantPool constantPool) {
        super((byte)17, nameIndex, length, constantPool);
        this.localVariableTypeTable = localVariableTypeTable != null ? localVariableTypeTable : LocalVariable.EMPTY_ARRAY;
        Args.requireU2(this.localVariableTypeTable.length, "localVariableTypeTable.length");
    }

    public LocalVariableTypeTable(LocalVariableTypeTable c) {
        this(c.getNameIndex(), c.getLength(), c.getLocalVariableTypeTable(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitLocalVariableTypeTable(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        LocalVariableTypeTable c = (LocalVariableTypeTable)this.clone();
        c.localVariableTypeTable = new LocalVariable[this.localVariableTypeTable.length];
        Arrays.setAll(c.localVariableTypeTable, i -> this.localVariableTypeTable[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.localVariableTypeTable.length);
        for (LocalVariable variable : this.localVariableTypeTable) {
            variable.dump(file);
        }
    }

    public final LocalVariable getLocalVariable(int index) {
        for (LocalVariable variable : this.localVariableTypeTable) {
            if (variable.getIndex() != index) continue;
            return variable;
        }
        return null;
    }

    public final LocalVariable[] getLocalVariableTypeTable() {
        return this.localVariableTypeTable;
    }

    public final int getTableLength() {
        return this.localVariableTypeTable == null ? 0 : this.localVariableTypeTable.length;
    }

    @Override
    public Iterator<LocalVariable> iterator() {
        return Stream.of(this.localVariableTypeTable).iterator();
    }

    public final void setLocalVariableTable(LocalVariable[] localVariableTable) {
        this.localVariableTypeTable = localVariableTable;
    }

    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.localVariableTypeTable.length; ++i) {
            buf.append(this.localVariableTypeTable[i].toStringShared(true));
            if (i >= this.localVariableTypeTable.length - 1) continue;
            buf.append('\n');
        }
        return buf.toString();
    }
}

