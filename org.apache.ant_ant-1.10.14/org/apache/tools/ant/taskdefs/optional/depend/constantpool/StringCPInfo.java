/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class StringCPInfo
extends ConstantCPInfo {
    private int index;

    public StringCPInfo() {
        super(8, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.index = cpStream.readUnsignedShort();
        this.setValue("unresolved");
    }

    public String toString() {
        return "String Constant Pool Entry for " + this.getValue() + "[" + this.index + "]";
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.setValue(((Utf8CPInfo)constantPool.getEntry(this.index)).getValue());
        super.resolve(constantPool);
    }
}

