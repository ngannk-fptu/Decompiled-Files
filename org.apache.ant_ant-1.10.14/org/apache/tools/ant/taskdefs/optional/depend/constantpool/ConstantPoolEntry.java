/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.DoubleCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.FieldRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.FloatCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.IntegerCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.InterfaceMethodRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.InvokeDynamicCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.LongCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodHandleCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodTypeCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ModuleCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.NameAndTypeCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.PackageCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.StringCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public abstract class ConstantPoolEntry {
    public static final int CONSTANT_UTF8 = 1;
    public static final int CONSTANT_INTEGER = 3;
    public static final int CONSTANT_FLOAT = 4;
    public static final int CONSTANT_LONG = 5;
    public static final int CONSTANT_DOUBLE = 6;
    public static final int CONSTANT_CLASS = 7;
    public static final int CONSTANT_STRING = 8;
    public static final int CONSTANT_FIELDREF = 9;
    public static final int CONSTANT_METHODREF = 10;
    public static final int CONSTANT_INTERFACEMETHODREF = 11;
    public static final int CONSTANT_NAMEANDTYPE = 12;
    public static final int CONSTANT_METHODHANDLE = 15;
    public static final int CONSTANT_METHODTYPE = 16;
    public static final int CONSTANT_INVOKEDYNAMIC = 18;
    public static final int CONSTANT_MODULEINFO = 19;
    public static final int CONSTANT_PACKAGEINFO = 20;
    private int tag;
    private int numEntries;
    private boolean resolved;

    public ConstantPoolEntry(int tagValue, int entries) {
        this.tag = tagValue;
        this.numEntries = entries;
        this.resolved = false;
    }

    public static ConstantPoolEntry readEntry(DataInputStream cpStream) throws IOException {
        ConstantPoolEntry cpInfo;
        int cpTag = cpStream.readUnsignedByte();
        switch (cpTag) {
            case 1: {
                cpInfo = new Utf8CPInfo();
                break;
            }
            case 3: {
                cpInfo = new IntegerCPInfo();
                break;
            }
            case 4: {
                cpInfo = new FloatCPInfo();
                break;
            }
            case 5: {
                cpInfo = new LongCPInfo();
                break;
            }
            case 6: {
                cpInfo = new DoubleCPInfo();
                break;
            }
            case 7: {
                cpInfo = new ClassCPInfo();
                break;
            }
            case 8: {
                cpInfo = new StringCPInfo();
                break;
            }
            case 9: {
                cpInfo = new FieldRefCPInfo();
                break;
            }
            case 10: {
                cpInfo = new MethodRefCPInfo();
                break;
            }
            case 11: {
                cpInfo = new InterfaceMethodRefCPInfo();
                break;
            }
            case 12: {
                cpInfo = new NameAndTypeCPInfo();
                break;
            }
            case 15: {
                cpInfo = new MethodHandleCPInfo();
                break;
            }
            case 16: {
                cpInfo = new MethodTypeCPInfo();
                break;
            }
            case 18: {
                cpInfo = new InvokeDynamicCPInfo();
                break;
            }
            case 19: {
                cpInfo = new ModuleCPInfo();
                break;
            }
            case 20: {
                cpInfo = new PackageCPInfo();
                break;
            }
            default: {
                throw new ClassFormatError("Invalid Constant Pool entry Type " + cpTag);
            }
        }
        cpInfo.read(cpStream);
        return cpInfo;
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public void resolve(ConstantPool constantPool) {
        this.resolved = true;
    }

    public abstract void read(DataInputStream var1) throws IOException;

    public int getTag() {
        return this.tag;
    }

    public final int getNumEntries() {
        return this.numEntries;
    }
}

