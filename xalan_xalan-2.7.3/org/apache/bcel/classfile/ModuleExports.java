/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public final class ModuleExports
implements Cloneable,
Node {
    private final int exportsIndex;
    private final int exportsFlags;
    private final int exportsToCount;
    private final int[] exportsToIndex;

    ModuleExports(DataInput file) throws IOException {
        this.exportsIndex = file.readUnsignedShort();
        this.exportsFlags = file.readUnsignedShort();
        this.exportsToCount = file.readUnsignedShort();
        this.exportsToIndex = new int[this.exportsToCount];
        for (int i = 0; i < this.exportsToCount; ++i) {
            this.exportsToIndex[i] = file.readUnsignedShort();
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitModuleExports(this);
    }

    public ModuleExports copy() {
        try {
            return (ModuleExports)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.exportsIndex);
        file.writeShort(this.exportsFlags);
        file.writeShort(this.exportsToCount);
        for (int entry : this.exportsToIndex) {
            file.writeShort(entry);
        }
    }

    public String toString() {
        return "exports(" + this.exportsIndex + ", " + this.exportsFlags + ", " + this.exportsToCount + ", ...)";
    }

    public String toString(ConstantPool constantPool) {
        StringBuilder buf = new StringBuilder();
        String packageName = constantPool.constantToString(this.exportsIndex, (byte)20);
        buf.append(Utility.compactClassName(packageName, false));
        buf.append(", ").append(String.format("%04x", this.exportsFlags));
        buf.append(", to(").append(this.exportsToCount).append("):\n");
        for (int index : this.exportsToIndex) {
            String moduleName = constantPool.getConstantString(index, (byte)19);
            buf.append("      ").append(Utility.compactClassName(moduleName, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

