/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class NameAndTypeCPInfo
extends ConstantPoolEntry {
    private String name;
    private String type;
    private int nameIndex;
    private int descriptorIndex;

    public NameAndTypeCPInfo() {
        super(12, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.nameIndex = cpStream.readUnsignedShort();
        this.descriptorIndex = cpStream.readUnsignedShort();
    }

    public String toString() {
        if (this.isResolved()) {
            return "Name = " + this.name + ", type = " + this.type;
        }
        return "Name index = " + this.nameIndex + ", descriptor index = " + this.descriptorIndex;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.name = ((Utf8CPInfo)constantPool.getEntry(this.nameIndex)).getValue();
        this.type = ((Utf8CPInfo)constantPool.getEntry(this.descriptorIndex)).getValue();
        super.resolve(constantPool);
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}

