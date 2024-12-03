/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class LocalVariable
implements Cloneable,
Node,
Constants {
    static final LocalVariable[] EMPTY_ARRAY = new LocalVariable[0];
    private int startPc;
    private int length;
    private int nameIndex;
    private int signatureIndex;
    private int index;
    private ConstantPool constantPool;
    private final int origIndex;

    LocalVariable(DataInput file, ConstantPool constantPool) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), constantPool);
    }

    public LocalVariable(int startPc, int length, int nameIndex, int signatureIndex, int index, ConstantPool constantPool) {
        this(startPc, length, nameIndex, signatureIndex, index, constantPool, index);
    }

    public LocalVariable(int startPc, int length, int nameIndex, int signatureIndex, int index, ConstantPool constantPool, int origIndex) {
        this.startPc = Args.requireU2(startPc, "startPc");
        this.length = Args.requireU2(length, "length");
        this.nameIndex = Args.requireU2(nameIndex, "nameIndex");
        this.signatureIndex = Args.requireU2(signatureIndex, "signatureIndex");
        this.index = Args.requireU2(index, "index");
        this.origIndex = Args.requireU2(origIndex, "origIndex");
        this.constantPool = constantPool;
    }

    public LocalVariable(LocalVariable localVariable) {
        this(localVariable.getStartPC(), localVariable.getLength(), localVariable.getNameIndex(), localVariable.getSignatureIndex(), localVariable.getIndex(), localVariable.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitLocalVariable(this);
    }

    public LocalVariable copy() {
        try {
            return (LocalVariable)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.startPc);
        dataOutputStream.writeShort(this.length);
        dataOutputStream.writeShort(this.nameIndex);
        dataOutputStream.writeShort(this.signatureIndex);
        dataOutputStream.writeShort(this.index);
    }

    public ConstantPool getConstantPool() {
        return this.constantPool;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLength() {
        return this.length;
    }

    public String getName() {
        return this.constantPool.getConstantUtf8(this.nameIndex).getBytes();
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    public int getOrigIndex() {
        return this.origIndex;
    }

    public String getSignature() {
        return this.constantPool.getConstantUtf8(this.signatureIndex).getBytes();
    }

    public int getSignatureIndex() {
        return this.signatureIndex;
    }

    public int getStartPC() {
        return this.startPc;
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public void setSignatureIndex(int signatureIndex) {
        this.signatureIndex = signatureIndex;
    }

    public void setStartPC(int startPc) {
        this.startPc = startPc;
    }

    public String toString() {
        return this.toStringShared(false);
    }

    String toStringShared(boolean typeTable) {
        String name = this.getName();
        String signature = Utility.signatureToString(this.getSignature(), false);
        String label = "LocalVariable" + (typeTable ? "Types" : "");
        return label + "(startPc = " + this.startPc + ", length = " + this.length + ", index = " + this.index + ":" + signature + " " + name + ")";
    }
}

