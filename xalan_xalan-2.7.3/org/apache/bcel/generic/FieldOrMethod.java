/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

public abstract class FieldOrMethod
extends CPInstruction
implements LoadClass {
    FieldOrMethod() {
    }

    protected FieldOrMethod(short opcode, int index) {
        super(opcode, index);
    }

    @Deprecated
    public String getClassName(ConstantPoolGen cpg) {
        ConstantCP cmr;
        ConstantPool cp = cpg.getConstantPool();
        String className = cp.getConstantString((cmr = (ConstantCP)cp.getConstant(super.getIndex())).getClassIndex(), (byte)7);
        if (className.startsWith("[")) {
            return "java.lang.Object";
        }
        return Utility.pathToPackage(className);
    }

    @Deprecated
    public ObjectType getClassType(ConstantPoolGen cpg) {
        return ObjectType.getInstance(this.getClassName(cpg));
    }

    @Override
    public ObjectType getLoadClassType(ConstantPoolGen cpg) {
        ReferenceType rt = this.getReferenceType(cpg);
        if (rt instanceof ObjectType) {
            return (ObjectType)rt;
        }
        throw new ClassGenException(rt.getClass().getCanonicalName() + " " + rt.getSignature() + " does not represent an ObjectType");
    }

    public String getName(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP)cp.getConstant(super.getIndex());
        ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8)cp.getConstant(cnat.getNameIndex())).getBytes();
    }

    public ReferenceType getReferenceType(ConstantPoolGen cpg) {
        ConstantCP cmr;
        ConstantPool cp = cpg.getConstantPool();
        String className = cp.getConstantString((cmr = (ConstantCP)cp.getConstant(super.getIndex())).getClassIndex(), (byte)7);
        if (className.startsWith("[")) {
            return (ArrayType)Type.getType(className);
        }
        className = Utility.pathToPackage(className);
        return ObjectType.getInstance(className);
    }

    public String getSignature(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP)cp.getConstant(super.getIndex());
        ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cmr.getNameAndTypeIndex());
        return ((ConstantUtf8)cp.getConstant(cnat.getSignatureIndex())).getBytes();
    }
}

