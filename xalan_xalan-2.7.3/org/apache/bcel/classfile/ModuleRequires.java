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

public final class ModuleRequires
implements Cloneable,
Node {
    private final int requiresIndex;
    private final int requiresFlags;
    private final int requiresVersionIndex;

    ModuleRequires(DataInput file) throws IOException {
        this.requiresIndex = file.readUnsignedShort();
        this.requiresFlags = file.readUnsignedShort();
        this.requiresVersionIndex = file.readUnsignedShort();
    }

    @Override
    public void accept(Visitor v) {
        v.visitModuleRequires(this);
    }

    public ModuleRequires copy() {
        try {
            return (ModuleRequires)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.requiresIndex);
        file.writeShort(this.requiresFlags);
        file.writeShort(this.requiresVersionIndex);
    }

    public String toString() {
        return "requires(" + this.requiresIndex + ", " + String.format("%04x", this.requiresFlags) + ", " + this.requiresVersionIndex + ")";
    }

    public String toString(ConstantPool constantPool) {
        StringBuilder buf = new StringBuilder();
        String moduleName = constantPool.constantToString(this.requiresIndex, (byte)19);
        buf.append(Utility.compactClassName(moduleName, false));
        buf.append(", ").append(String.format("%04x", this.requiresFlags));
        String version = this.requiresVersionIndex == 0 ? "0" : constantPool.getConstantString(this.requiresVersionIndex, (byte)1);
        buf.append(", ").append(version);
        return buf.toString();
    }
}

