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

public class MethodRefCPInfo
extends ConstantPoolEntry {
    private String methodClassName;
    private String methodName;
    private String methodType;
    private int classIndex;
    private int nameAndTypeIndex;

    public MethodRefCPInfo() {
        super(10, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }

    public String toString() {
        if (this.isResolved()) {
            return "Method : Class = " + this.methodClassName + ", name = " + this.methodName + ", type = " + this.methodType;
        }
        return "Method : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        ClassCPInfo methodClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        methodClass.resolve(constantPool);
        this.methodClassName = methodClass.getClassName();
        NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.methodName = nt.getName();
        this.methodType = nt.getType();
        super.resolve(constantPool);
    }

    public String getMethodClassName() {
        return this.methodClassName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getMethodType() {
        return this.methodType;
    }
}

