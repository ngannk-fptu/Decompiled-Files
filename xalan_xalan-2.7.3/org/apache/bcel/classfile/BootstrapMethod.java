/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;

public class BootstrapMethod
implements Cloneable {
    private int bootstrapMethodRef;
    private int[] bootstrapArguments;

    public BootstrapMethod(BootstrapMethod c) {
        this(c.getBootstrapMethodRef(), c.getBootstrapArguments());
    }

    BootstrapMethod(DataInput input) throws IOException {
        this(input.readUnsignedShort(), input.readUnsignedShort());
        for (int i = 0; i < this.bootstrapArguments.length; ++i) {
            this.bootstrapArguments[i] = input.readUnsignedShort();
        }
    }

    private BootstrapMethod(int bootstrapMethodRef, int numBootstrapArguments) {
        this(bootstrapMethodRef, new int[numBootstrapArguments]);
    }

    public BootstrapMethod(int bootstrapMethodRef, int[] bootstrapArguments) {
        this.bootstrapMethodRef = bootstrapMethodRef;
        this.bootstrapArguments = bootstrapArguments;
    }

    public BootstrapMethod copy() {
        try {
            return (BootstrapMethod)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.bootstrapMethodRef);
        file.writeShort(this.bootstrapArguments.length);
        for (int bootstrapArgument : this.bootstrapArguments) {
            file.writeShort(bootstrapArgument);
        }
    }

    public int[] getBootstrapArguments() {
        return this.bootstrapArguments;
    }

    public int getBootstrapMethodRef() {
        return this.bootstrapMethodRef;
    }

    public int getNumBootstrapArguments() {
        return this.bootstrapArguments.length;
    }

    public void setBootstrapArguments(int[] bootstrapArguments) {
        this.bootstrapArguments = bootstrapArguments;
    }

    public void setBootstrapMethodRef(int bootstrapMethodRef) {
        this.bootstrapMethodRef = bootstrapMethodRef;
    }

    public final String toString() {
        return "BootstrapMethod(" + this.bootstrapMethodRef + ", " + this.bootstrapArguments.length + ", " + Arrays.toString(this.bootstrapArguments) + ")";
    }

    public final String toString(ConstantPool constantPool) {
        StringBuilder buf = new StringBuilder();
        String bootstrapMethodName = constantPool.constantToString(this.bootstrapMethodRef, (byte)15);
        buf.append(Utility.compactClassName(bootstrapMethodName, false));
        int bootstrapArgumentsLen = this.bootstrapArguments.length;
        if (bootstrapArgumentsLen > 0) {
            buf.append("\nMethod Arguments:");
            for (int i = 0; i < bootstrapArgumentsLen; ++i) {
                buf.append("\n  ").append(i).append(": ");
                buf.append(constantPool.constantToString((Constant)constantPool.getConstant(this.bootstrapArguments[i])));
            }
        }
        return buf.toString();
    }
}

