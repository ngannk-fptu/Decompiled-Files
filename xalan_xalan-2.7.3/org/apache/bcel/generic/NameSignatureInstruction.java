/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;

public abstract class NameSignatureInstruction
extends CPInstruction {
    public NameSignatureInstruction() {
    }

    public NameSignatureInstruction(short opcode, int index) {
        super(opcode, index);
    }

    public String getName(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantNameAndType cnat = this.getNameAndType(cpg);
        return ((ConstantUtf8)cp.getConstant(cnat.getNameIndex())).getBytes();
    }

    public ConstantNameAndType getNameAndType(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP)cp.getConstant(super.getIndex());
        return (ConstantNameAndType)cp.getConstant(cmr.getNameAndTypeIndex());
    }

    public String getSignature(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantNameAndType cnat = this.getNameAndType(cpg);
        return ((ConstantUtf8)cp.getConstant(cnat.getSignatureIndex())).getBytes();
    }
}

