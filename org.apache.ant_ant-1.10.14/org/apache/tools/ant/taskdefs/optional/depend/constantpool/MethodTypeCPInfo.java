/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class MethodTypeCPInfo
extends ConstantCPInfo {
    private int methodDescriptorIndex;
    private String methodDescriptor;

    public MethodTypeCPInfo() {
        super(16, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.methodDescriptorIndex = cpStream.readUnsignedShort();
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        Utf8CPInfo methodClass = (Utf8CPInfo)constantPool.getEntry(this.methodDescriptorIndex);
        methodClass.resolve(constantPool);
        this.methodDescriptor = methodClass.getValue();
        super.resolve(constantPool);
    }

    public String toString() {
        if (this.isResolved()) {
            return "MethodDescriptor: " + this.methodDescriptor;
        }
        return "MethodDescriptorIndex: " + this.methodDescriptorIndex;
    }
}

