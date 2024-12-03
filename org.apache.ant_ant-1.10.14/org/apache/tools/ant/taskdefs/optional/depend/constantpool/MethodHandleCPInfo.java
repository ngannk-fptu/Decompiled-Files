/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;

public class MethodHandleCPInfo
extends ConstantPoolEntry {
    private ConstantPoolEntry reference;
    private ReferenceKind referenceKind;
    private int referenceIndex;

    public MethodHandleCPInfo() {
        super(15, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.referenceKind = ReferenceKind.values()[cpStream.readUnsignedByte() - 1];
        this.referenceIndex = cpStream.readUnsignedShort();
    }

    public String toString() {
        if (this.isResolved()) {
            return "MethodHandle : " + this.reference.toString();
        }
        return "MethodHandle : Reference kind = " + (Object)((Object)this.referenceKind) + "Reference index = " + this.referenceIndex;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.reference = constantPool.getEntry(this.referenceIndex);
        this.reference.resolve(constantPool);
        super.resolve(constantPool);
    }

    public static enum ReferenceKind {
        REF_getField,
        REF_getStatic,
        REF_putField,
        REF_putStatic,
        REF_invokeVirtual,
        REF_invokeStatic,
        REF_invokeSpecial,
        REF_newInvokeSpecial,
        REF_invokeInterface;


        public int value() {
            return this.ordinal() + 1;
        }
    }
}

