/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;

public final class ConstantMethodHandle
extends Constant {
    private byte referenceKind;
    private int referenceIndex;

    ConstantMethodHandle(DataInputStream file) throws IOException {
        this(file.readByte(), file.readUnsignedShort());
    }

    public ConstantMethodHandle(byte referenceKind, int referenceIndex) {
        super((byte)15);
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeByte(this.referenceKind);
        file.writeShort(this.referenceIndex);
    }

    public final byte getReferenceKind() {
        return this.referenceKind;
    }

    public final int getReferenceIndex() {
        return this.referenceIndex;
    }

    @Override
    public final String toString() {
        return super.toString() + "(referenceKind=" + this.referenceKind + ",referenceIndex=" + this.referenceIndex + ")";
    }

    @Override
    public String getValue() {
        return this.toString();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantMethodHandle(this);
    }

    public static String kindToString(byte kind) {
        switch (kind) {
            case 1: {
                return "getfield";
            }
            case 2: {
                return "getstatic";
            }
            case 3: {
                return "putfield";
            }
            case 4: {
                return "putstatic";
            }
            case 5: {
                return "invokevirtual";
            }
            case 6: {
                return "invokestatic";
            }
            case 7: {
                return "invokespecial";
            }
            case 8: {
                return "newinvokespecial";
            }
            case 9: {
                return "invokeinterface";
            }
        }
        return "nyi";
    }
}

