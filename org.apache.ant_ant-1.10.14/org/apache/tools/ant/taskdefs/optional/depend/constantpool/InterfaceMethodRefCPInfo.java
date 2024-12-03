/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.NameAndTypeCPInfo;

public class InterfaceMethodRefCPInfo
extends ConstantPoolEntry {
    private String interfaceMethodClassName;
    private String interfaceMethodName;
    private String interfaceMethodType;
    private int classIndex;
    private int nameAndTypeIndex;

    public InterfaceMethodRefCPInfo() {
        super(11, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        ClassCPInfo interfaceMethodClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        interfaceMethodClass.resolve(constantPool);
        this.interfaceMethodClassName = interfaceMethodClass.getClassName();
        NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.interfaceMethodName = nt.getName();
        this.interfaceMethodType = nt.getType();
        super.resolve(constantPool);
    }

    public String toString() {
        if (this.isResolved()) {
            return "InterfaceMethod : Class = " + this.interfaceMethodClassName + ", name = " + this.interfaceMethodName + ", type = " + this.interfaceMethodType;
        }
        return "InterfaceMethod : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    public String getInterfaceMethodClassName() {
        return this.interfaceMethodClassName;
    }

    public String getInterfaceMethodName() {
        return this.interfaceMethodName;
    }

    public String getInterfaceMethodType() {
        return this.interfaceMethodType;
    }
}

