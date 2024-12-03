/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.ObjectFactory;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.Util;

public final class ObjectType
extends Type {
    private String _javaClassName = "java.lang.Object";
    private Class _clazz = Object.class;

    protected ObjectType(String javaClassName) {
        this._javaClassName = javaClassName;
        try {
            this._clazz = ObjectFactory.findProviderClass(javaClassName, ObjectFactory.findClassLoader(), true);
        }
        catch (ClassNotFoundException e) {
            this._clazz = null;
        }
    }

    protected ObjectType(Class clazz) {
        this._clazz = clazz;
        this._javaClassName = clazz.getName();
    }

    public int hashCode() {
        return Object.class.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof ObjectType;
    }

    public String getJavaClassName() {
        return this._javaClassName;
    }

    public Class getJavaClass() {
        return this._clazz;
    }

    @Override
    public String toString() {
        return this._javaClassName;
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        StringBuffer result = new StringBuffer("L");
        result.append(this._javaClassName.replace('.', '/')).append(';');
        return result.toString();
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return Util.getJCRefType(this.toSignature());
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(DUP);
        BranchHandle ifNull = il.append(new IFNULL(null));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(this._javaClassName, "toString", "()Ljava/lang/String;")));
        BranchHandle gotobh = il.append(new GOTO(null));
        ifNull.setTarget(il.append(POP));
        il.append(new PUSH(cpg, ""));
        gotobh.setTarget(il.append(NOP));
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        if (clazz.isAssignableFrom(this._clazz)) {
            methodGen.getInstructionList().append(NOP);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getClass().toString());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public Instruction LOAD(int slot) {
        return new ALOAD(slot);
    }

    @Override
    public Instruction STORE(int slot) {
        return new ASTORE(slot);
    }
}

