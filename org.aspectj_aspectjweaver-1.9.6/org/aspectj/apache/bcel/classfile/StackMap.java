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
import org.aspectj.apache.bcel.classfile.StackMapEntry;

public final class StackMap
extends Attribute {
    private int map_length;
    private StackMapEntry[] map;

    public StackMap(int name_index, int length, StackMapEntry[] map, ConstantPool constant_pool) {
        super((byte)11, name_index, length, constant_pool);
        this.setStackMap(map);
    }

    StackMap(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (StackMapEntry[])null, constant_pool);
        this.map_length = file.readUnsignedShort();
        this.map = new StackMapEntry[this.map_length];
        for (int i = 0; i < this.map_length; ++i) {
            this.map[i] = new StackMapEntry(file, constant_pool);
        }
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.map_length);
        for (int i = 0; i < this.map_length; ++i) {
            this.map[i].dump(file);
        }
    }

    public final StackMapEntry[] getStackMap() {
        return this.map;
    }

    public final void setStackMap(StackMapEntry[] map) {
        this.map = map;
        this.map_length = map == null ? 0 : map.length;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer("StackMap(");
        for (int i = 0; i < this.map_length; ++i) {
            buf.append(this.map[i].toString());
            if (i >= this.map_length - 1) continue;
            buf.append(", ");
        }
        buf.append(')');
        return buf.toString();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitStackMap(this);
    }

    public final int getMapLength() {
        return this.map_length;
    }
}

