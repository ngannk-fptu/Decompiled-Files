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

public final class ModulePackages
extends Attribute {
    private int[] packageIndexTable;

    ModulePackages(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (int[])null, constantPool);
        int packageCount = input.readUnsignedShort();
        this.packageIndexTable = new int[packageCount];
        for (int i = 0; i < packageCount; ++i) {
            this.packageIndexTable[i] = input.readUnsignedShort();
        }
    }

    public ModulePackages(int nameIndex, int length, int[] packageIndexTable, ConstantPool constantPool) {
        super((byte)23, nameIndex, length, constantPool);
        this.packageIndexTable = packageIndexTable != null ? packageIndexTable : ArrayUtils.EMPTY_INT_ARRAY;
        Args.requireU2(this.packageIndexTable.length, "packageIndexTable.length");
    }

    public ModulePackages(ModulePackages c) {
        this(c.getNameIndex(), c.getLength(), c.getPackageIndexTable(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitModulePackages(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        ModulePackages c = (ModulePackages)this.clone();
        if (this.packageIndexTable != null) {
            c.packageIndexTable = (int[])this.packageIndexTable.clone();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.packageIndexTable.length);
        for (int index : this.packageIndexTable) {
            file.writeShort(index);
        }
    }

    public int getNumberOfPackages() {
        return this.packageIndexTable == null ? 0 : this.packageIndexTable.length;
    }

    public int[] getPackageIndexTable() {
        return this.packageIndexTable;
    }

    public String[] getPackageNames() {
        String[] names = new String[this.packageIndexTable.length];
        Arrays.setAll(names, i -> Utility.pathToPackage(super.getConstantPool().getConstantString(this.packageIndexTable[i], (byte)20)));
        return names;
    }

    public void setPackageIndexTable(int[] packageIndexTable) {
        this.packageIndexTable = packageIndexTable != null ? packageIndexTable : ArrayUtils.EMPTY_INT_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ModulePackages(");
        buf.append(this.packageIndexTable.length);
        buf.append("):\n");
        for (int index : this.packageIndexTable) {
            String packageName = super.getConstantPool().getConstantString(index, (byte)20);
            buf.append("  ").append(Utility.compactClassName(packageName, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

