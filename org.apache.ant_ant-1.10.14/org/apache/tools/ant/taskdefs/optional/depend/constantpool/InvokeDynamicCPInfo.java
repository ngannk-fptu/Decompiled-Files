/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.NameAndTypeCPInfo;

public class InvokeDynamicCPInfo
extends ConstantCPInfo {
    private int bootstrapMethodAttrIndex;
    private int nameAndTypeIndex;
    private NameAndTypeCPInfo nameAndTypeCPInfo;

    public InvokeDynamicCPInfo() {
        super(18, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.bootstrapMethodAttrIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }

    public String toString() {
        if (this.isResolved()) {
            return "Name = " + this.nameAndTypeCPInfo.getName() + ", type = " + this.nameAndTypeCPInfo.getType();
        }
        return "BootstrapMethodAttrIndex inx = " + this.bootstrapMethodAttrIndex + "NameAndType index = " + this.nameAndTypeIndex;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.nameAndTypeCPInfo = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        this.nameAndTypeCPInfo.resolve(constantPool);
        super.resolve(constantPool);
    }
}

