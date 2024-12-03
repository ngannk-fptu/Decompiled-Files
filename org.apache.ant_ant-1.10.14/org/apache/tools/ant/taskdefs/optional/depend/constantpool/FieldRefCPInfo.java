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

public class FieldRefCPInfo
extends ConstantPoolEntry {
    private String fieldClassName;
    private String fieldName;
    private String fieldType;
    private int classIndex;
    private int nameAndTypeIndex;

    public FieldRefCPInfo() {
        super(9, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        ClassCPInfo fieldClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        fieldClass.resolve(constantPool);
        this.fieldClassName = fieldClass.getClassName();
        NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.fieldName = nt.getName();
        this.fieldType = nt.getType();
        super.resolve(constantPool);
    }

    public String toString() {
        if (this.isResolved()) {
            return "Field : Class = " + this.fieldClassName + ", name = " + this.fieldName + ", type = " + this.fieldType;
        }
        return "Field : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    public String getFieldClassName() {
        return this.fieldClassName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldType() {
        return this.fieldType;
    }
}

