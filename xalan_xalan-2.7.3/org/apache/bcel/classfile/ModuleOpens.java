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

public final class ModuleOpens
implements Cloneable,
Node {
    private final int opensIndex;
    private final int opensFlags;
    private final int opensToCount;
    private final int[] opensToIndex;

    ModuleOpens(DataInput file) throws IOException {
        this.opensIndex = file.readUnsignedShort();
        this.opensFlags = file.readUnsignedShort();
        this.opensToCount = file.readUnsignedShort();
        this.opensToIndex = new int[this.opensToCount];
        for (int i = 0; i < this.opensToCount; ++i) {
            this.opensToIndex[i] = file.readUnsignedShort();
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitModuleOpens(this);
    }

    public ModuleOpens copy() {
        try {
            return (ModuleOpens)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.opensIndex);
        file.writeShort(this.opensFlags);
        file.writeShort(this.opensToCount);
        for (int entry : this.opensToIndex) {
            file.writeShort(entry);
        }
    }

    public String toString() {
        return "opens(" + this.opensIndex + ", " + this.opensFlags + ", " + this.opensToCount + ", ...)";
    }

    public String toString(ConstantPool constantPool) {
        StringBuilder buf = new StringBuilder();
        String packageName = constantPool.constantToString(this.opensIndex, (byte)20);
        buf.append(Utility.compactClassName(packageName, false));
        buf.append(", ").append(String.format("%04x", this.opensFlags));
        buf.append(", to(").append(this.opensToCount).append("):\n");
        for (int index : this.opensToIndex) {
            String moduleName = constantPool.getConstantString(index, (byte)19);
            buf.append("      ").append(Utility.compactClassName(moduleName, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

