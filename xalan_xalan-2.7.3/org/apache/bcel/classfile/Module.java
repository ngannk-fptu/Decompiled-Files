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
import org.apache.bcel.classfile.ModuleExports;
import org.apache.bcel.classfile.ModuleOpens;
import org.apache.bcel.classfile.ModuleProvides;
import org.apache.bcel.classfile.ModuleRequires;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public final class Module
extends Attribute {
    public static final String EXTENSION = ".jmod";
    private final int moduleNameIndex;
    private final int moduleFlags;
    private final int moduleVersionIndex;
    private ModuleRequires[] requiresTable;
    private ModuleExports[] exportsTable;
    private ModuleOpens[] opensTable;
    private final int usesCount;
    private final int[] usesIndex;
    private ModuleProvides[] providesTable;

    Module(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        super((byte)22, nameIndex, length, constantPool);
        int i;
        this.moduleNameIndex = input.readUnsignedShort();
        this.moduleFlags = input.readUnsignedShort();
        this.moduleVersionIndex = input.readUnsignedShort();
        int requiresCount = input.readUnsignedShort();
        this.requiresTable = new ModuleRequires[requiresCount];
        for (int i2 = 0; i2 < requiresCount; ++i2) {
            this.requiresTable[i2] = new ModuleRequires(input);
        }
        int exportsCount = input.readUnsignedShort();
        this.exportsTable = new ModuleExports[exportsCount];
        for (int i3 = 0; i3 < exportsCount; ++i3) {
            this.exportsTable[i3] = new ModuleExports(input);
        }
        int opensCount = input.readUnsignedShort();
        this.opensTable = new ModuleOpens[opensCount];
        for (i = 0; i < opensCount; ++i) {
            this.opensTable[i] = new ModuleOpens(input);
        }
        this.usesCount = input.readUnsignedShort();
        this.usesIndex = new int[this.usesCount];
        for (i = 0; i < this.usesCount; ++i) {
            this.usesIndex[i] = input.readUnsignedShort();
        }
        int providesCount = input.readUnsignedShort();
        this.providesTable = new ModuleProvides[providesCount];
        for (int i4 = 0; i4 < providesCount; ++i4) {
            this.providesTable[i4] = new ModuleProvides(input);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitModule(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        Module c = (Module)this.clone();
        c.requiresTable = new ModuleRequires[this.requiresTable.length];
        Arrays.setAll(c.requiresTable, i -> this.requiresTable[i].copy());
        c.exportsTable = new ModuleExports[this.exportsTable.length];
        Arrays.setAll(c.exportsTable, i -> this.exportsTable[i].copy());
        c.opensTable = new ModuleOpens[this.opensTable.length];
        Arrays.setAll(c.opensTable, i -> this.opensTable[i].copy());
        c.providesTable = new ModuleProvides[this.providesTable.length];
        Arrays.setAll(c.providesTable, i -> this.providesTable[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.moduleNameIndex);
        file.writeShort(this.moduleFlags);
        file.writeShort(this.moduleVersionIndex);
        file.writeShort(this.requiresTable.length);
        for (ModuleRequires moduleRequires : this.requiresTable) {
            moduleRequires.dump(file);
        }
        file.writeShort(this.exportsTable.length);
        for (ModuleExports moduleExports : this.exportsTable) {
            moduleExports.dump(file);
        }
        file.writeShort(this.opensTable.length);
        for (ModuleOpens moduleOpens : this.opensTable) {
            moduleOpens.dump(file);
        }
        file.writeShort(this.usesIndex.length);
        for (int n : this.usesIndex) {
            file.writeShort(n);
        }
        file.writeShort(this.providesTable.length);
        for (ModuleProvides moduleProvides : this.providesTable) {
            moduleProvides.dump(file);
        }
    }

    public ModuleExports[] getExportsTable() {
        return this.exportsTable;
    }

    public ModuleOpens[] getOpensTable() {
        return this.opensTable;
    }

    public ModuleProvides[] getProvidesTable() {
        return this.providesTable;
    }

    public ModuleRequires[] getRequiresTable() {
        return this.requiresTable;
    }

    @Override
    public String toString() {
        ConstantPool cp = super.getConstantPool();
        StringBuilder buf = new StringBuilder();
        buf.append("Module:\n");
        buf.append("  name:    ").append(Utility.pathToPackage(cp.getConstantString(this.moduleNameIndex, (byte)19))).append("\n");
        buf.append("  flags:   ").append(String.format("%04x", this.moduleFlags)).append("\n");
        String version = this.moduleVersionIndex == 0 ? "0" : cp.getConstantString(this.moduleVersionIndex, (byte)1);
        buf.append("  version: ").append(version).append("\n");
        buf.append("  requires(").append(this.requiresTable.length).append("):\n");
        for (ModuleRequires moduleRequires : this.requiresTable) {
            buf.append("    ").append(moduleRequires.toString(cp)).append("\n");
        }
        buf.append("  exports(").append(this.exportsTable.length).append("):\n");
        for (ModuleExports moduleExports : this.exportsTable) {
            buf.append("    ").append(moduleExports.toString(cp)).append("\n");
        }
        buf.append("  opens(").append(this.opensTable.length).append("):\n");
        for (ModuleOpens moduleOpens : this.opensTable) {
            buf.append("    ").append(moduleOpens.toString(cp)).append("\n");
        }
        buf.append("  uses(").append(this.usesIndex.length).append("):\n");
        for (int n : this.usesIndex) {
            String className = cp.getConstantString(n, (byte)7);
            buf.append("    ").append(Utility.compactClassName(className, false)).append("\n");
        }
        buf.append("  provides(").append(this.providesTable.length).append("):\n");
        for (ModuleProvides moduleProvides : this.providesTable) {
            buf.append("    ").append(moduleProvides.toString(cp)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

