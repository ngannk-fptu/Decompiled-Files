/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public final class PMGClass
extends Attribute {
    private int pmgClassIndex;
    private int pmgIndex;

    PMGClass(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, input.readUnsignedShort(), input.readUnsignedShort(), constantPool);
    }

    public PMGClass(int nameIndex, int length, int pmgIndex, int pmgClassIndex, ConstantPool constantPool) {
        super((byte)9, nameIndex, length, constantPool);
        this.pmgIndex = pmgIndex;
        this.pmgClassIndex = pmgClassIndex;
    }

    public PMGClass(PMGClass pgmClass) {
        this(pgmClass.getNameIndex(), pgmClass.getLength(), pgmClass.getPMGIndex(), pgmClass.getPMGClassIndex(), pgmClass.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        PMGClass.println("Visiting non-standard PMGClass object");
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.pmgIndex);
        file.writeShort(this.pmgClassIndex);
    }

    public int getPMGClassIndex() {
        return this.pmgClassIndex;
    }

    public String getPMGClassName() {
        return super.getConstantPool().getConstantUtf8(this.pmgClassIndex).getBytes();
    }

    public int getPMGIndex() {
        return this.pmgIndex;
    }

    public String getPMGName() {
        return super.getConstantPool().getConstantUtf8(this.pmgIndex).getBytes();
    }

    public void setPMGClassIndex(int pmgClassIndex) {
        this.pmgClassIndex = pmgClassIndex;
    }

    public void setPMGIndex(int pmgIndex) {
        this.pmgIndex = pmgIndex;
    }

    @Override
    public String toString() {
        return "PMGClass(" + this.getPMGName() + ", " + this.getPMGClassName() + ")";
    }
}

