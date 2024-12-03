/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantCP;
import org.aspectj.apache.bcel.classfile.ConstantNameAndType;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.ObjectType;

public abstract class FieldOrMethod
extends InstructionCP {
    protected String signature;
    protected String name;
    private String classname;

    protected FieldOrMethod(short opcode, int index) {
        super(opcode, index);
    }

    public String getSignature(ConstantPool cp) {
        if (this.signature == null) {
            Constant c = cp.getConstant(this.index);
            ConstantCP cmr = (ConstantCP)c;
            ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cmr.getNameAndTypeIndex());
            this.signature = ((ConstantUtf8)cp.getConstant(cnat.getSignatureIndex())).getValue();
        }
        return this.signature;
    }

    public String getName(ConstantPool cp) {
        if (this.name == null) {
            ConstantCP cmr = (ConstantCP)cp.getConstant(this.index);
            ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cmr.getNameAndTypeIndex());
            this.name = ((ConstantUtf8)cp.getConstant(cnat.getNameIndex())).getValue();
        }
        return this.name;
    }

    public String getClassName(ConstantPool cp) {
        if (this.classname == null) {
            ConstantCP cmr = (ConstantCP)cp.getConstant(this.index);
            String str = cp.getConstantString(cmr.getClassIndex(), (byte)7);
            this.classname = str.charAt(0) == '[' ? str : str.replace('/', '.');
        }
        return this.classname;
    }

    public ObjectType getClassType(ConstantPool cpg) {
        return new ObjectType(this.getClassName(cpg));
    }

    @Override
    public ObjectType getLoadClassType(ConstantPool cpg) {
        return this.getClassType(cpg);
    }
}

