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

public final class ModuleProvides
implements Cloneable,
Node {
    private final int providesIndex;
    private final int providesWithCount;
    private final int[] providesWithIndex;

    ModuleProvides(DataInput file) throws IOException {
        this.providesIndex = file.readUnsignedShort();
        this.providesWithCount = file.readUnsignedShort();
        this.providesWithIndex = new int[this.providesWithCount];
        for (int i = 0; i < this.providesWithCount; ++i) {
            this.providesWithIndex[i] = file.readUnsignedShort();
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitModuleProvides(this);
    }

    public ModuleProvides copy() {
        try {
            return (ModuleProvides)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.providesIndex);
        file.writeShort(this.providesWithCount);
        for (int entry : this.providesWithIndex) {
            file.writeShort(entry);
        }
    }

    public String toString() {
        return "provides(" + this.providesIndex + ", " + this.providesWithCount + ", ...)";
    }

    public String toString(ConstantPool constantPool) {
        StringBuilder buf = new StringBuilder();
        String interfaceName = constantPool.constantToString(this.providesIndex, (byte)7);
        buf.append(Utility.compactClassName(interfaceName, false));
        buf.append(", with(").append(this.providesWithCount).append("):\n");
        for (int index : this.providesWithIndex) {
            String className = constantPool.getConstantString(index, (byte)7);
            buf.append("      ").append(Utility.compactClassName(className, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

