/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class ModulePackages
extends Attribute {
    private static int[] NO_PACKAGES = new int[0];
    private int[] packageIndices;

    public ModulePackages(ModulePackages c) {
        this(c.getNameIndex(), c.getLength(), c.getPackageIndices(), c.getConstantPool());
    }

    public ModulePackages(int nameIndex, int length, int[] packageIndices, ConstantPool cp) {
        super((byte)24, nameIndex, length, cp);
        this.setPackageIndices(packageIndices);
    }

    ModulePackages(int nameIndex, int length, DataInputStream stream, ConstantPool cp) throws IOException {
        this(nameIndex, length, (int[])null, cp);
        int packageIndicesCount = stream.readUnsignedShort();
        this.packageIndices = new int[packageIndicesCount];
        for (int i = 0; i < packageIndicesCount; ++i) {
            this.packageIndices[i] = stream.readUnsignedShort();
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitModulePackages(this);
    }

    @Override
    public final void dump(DataOutputStream stream) throws IOException {
        super.dump(stream);
        stream.writeShort(this.packageIndices.length);
        for (int i = 0; i < this.packageIndices.length; ++i) {
            stream.writeShort(this.packageIndices[i]);
        }
    }

    public final int[] getPackageIndices() {
        return this.packageIndices;
    }

    public final void setPackageIndices(int[] packageIndices) {
        this.packageIndices = packageIndices == null ? NO_PACKAGES : packageIndices;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.packageIndices.length; ++i) {
            buf.append(this.cpool.getPackageName(this.packageIndices[i]) + "\n");
        }
        return buf.toString();
    }
}

