/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class ModuleCPInfo
extends ConstantCPInfo {
    private int moduleNameIndex;
    private String moduleName;

    public ModuleCPInfo() {
        super(19, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.moduleNameIndex = cpStream.readUnsignedShort();
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.moduleName = ((Utf8CPInfo)constantPool.getEntry(this.moduleNameIndex)).getValue();
        super.resolve(constantPool);
    }

    public String toString() {
        return "Module info Constant Pool Entry for " + this.moduleName + "[" + this.moduleNameIndex + "]";
    }
}

