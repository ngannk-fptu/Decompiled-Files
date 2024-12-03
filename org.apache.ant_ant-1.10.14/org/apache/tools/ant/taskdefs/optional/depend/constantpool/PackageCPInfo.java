/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class PackageCPInfo
extends ConstantCPInfo {
    private int packageNameIndex;
    private String packageName;

    public PackageCPInfo() {
        super(20, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.packageNameIndex = cpStream.readUnsignedShort();
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.packageName = ((Utf8CPInfo)constantPool.getEntry(this.packageNameIndex)).getValue();
        super.resolve(constantPool);
    }

    public String toString() {
        return "Package info Constant Pool Entry for " + this.packageName + "[" + this.packageNameIndex + "]";
    }
}

