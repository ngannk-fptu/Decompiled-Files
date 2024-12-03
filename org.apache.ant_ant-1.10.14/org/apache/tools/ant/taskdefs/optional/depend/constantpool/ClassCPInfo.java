/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class ClassCPInfo
extends ConstantPoolEntry {
    private String className;
    private int index;

    public ClassCPInfo() {
        super(7, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.index = cpStream.readUnsignedShort();
        this.className = "unresolved";
    }

    public String toString() {
        return "Class Constant Pool Entry for " + this.className + "[" + this.index + "]";
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.className = ((Utf8CPInfo)constantPool.getEntry(this.index)).getValue();
        super.resolve(constantPool);
    }

    public String getClassName() {
        return this.className;
    }
}

