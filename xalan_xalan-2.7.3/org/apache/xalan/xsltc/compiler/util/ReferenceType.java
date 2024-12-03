/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ResultTreeType;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;

public final class ReferenceType
extends Type {
    protected ReferenceType() {
    }

    @Override
    public String toString() {
        return "reference";
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        return "Ljava/lang/Object;";
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return org.apache.bcel.generic.Type.OBJECT;
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        } else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        } else if (type == Type.NodeSet) {
            this.translateTo(classGen, methodGen, (NodeSetType)type);
        } else if (type == Type.Node) {
            this.translateTo(classGen, methodGen, (NodeType)type);
        } else if (type == Type.ResultTree) {
            this.translateTo(classGen, methodGen, (ResultTreeType)type);
        } else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (ObjectType)type);
        } else if (type != Type.Reference) {
            ErrorMsg err = new ErrorMsg("INTERNAL_ERR", type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        int current = methodGen.getLocalIndex("current");
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (current < 0) {
            il.append(new PUSH(cpg, 0));
        } else {
            il.append(new ILOAD(current));
        }
        il.append(methodGen.loadDOM());
        int stringF = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "stringF", "(Ljava/lang/Object;ILorg/apache/xalan/xsltc/DOM;)Ljava/lang/String;");
        il.append(new INVOKESTATIC(stringF));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadDOM());
        int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "numberF", "(Ljava/lang/Object;Lorg/apache/xalan/xsltc/DOM;)D");
        il.append(new INVOKESTATIC(index));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "booleanF", "(Ljava/lang/Object;)Z");
        il.append(new INVOKESTATIC(index));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeSetType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lorg/apache/xml/dtm/DTMAxisIterator;");
        il.append(new INVOKESTATIC(index));
        index = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "reset", "()Lorg/apache/xml/dtm/DTMAxisIterator;");
        il.append(new INVOKEINTERFACE(index, 1));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeType type) {
        this.translateTo(classGen, methodGen, Type.NodeSet);
        Type.NodeSet.translateTo(classGen, methodGen, type);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ResultTreeType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToResultTree", "(Ljava/lang/Object;)Lorg/apache/xalan/xsltc/DOM;");
        il.append(new INVOKESTATIC(index));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int referenceToLong = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToLong", "(Ljava/lang/Object;)J");
        int referenceToDouble = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToDouble", "(Ljava/lang/Object;)D");
        int referenceToBoolean = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToBoolean", "(Ljava/lang/Object;)Z");
        if (clazz.getName().equals("java.lang.Object")) {
            il.append(NOP);
        } else if (clazz == Double.TYPE) {
            il.append(new INVOKESTATIC(referenceToDouble));
        } else if (clazz.getName().equals("java.lang.Double")) {
            il.append(new INVOKESTATIC(referenceToDouble));
            Type.Real.translateTo(classGen, methodGen, Type.Reference);
        } else if (clazz == Float.TYPE) {
            il.append(new INVOKESTATIC(referenceToDouble));
            il.append(D2F);
        } else if (clazz.getName().equals("java.lang.String")) {
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToString", "(Ljava/lang/Object;Lorg/apache/xalan/xsltc/DOM;)Ljava/lang/String;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        } else if (clazz.getName().equals("org.w3c.dom.Node")) {
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToNode", "(Ljava/lang/Object;Lorg/apache/xalan/xsltc/DOM;)Lorg/w3c/dom/Node;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        } else if (clazz.getName().equals("org.w3c.dom.NodeList")) {
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "referenceToNodeList", "(Ljava/lang/Object;Lorg/apache/xalan/xsltc/DOM;)Lorg/w3c/dom/NodeList;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        } else if (clazz.getName().equals("org.apache.xalan.xsltc.DOM")) {
            this.translateTo(classGen, methodGen, Type.ResultTree);
        } else if (clazz == Long.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
        } else if (clazz == Integer.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(L2I);
        } else if (clazz == Short.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(L2I);
            il.append(I2S);
        } else if (clazz == Byte.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(L2I);
            il.append(I2B);
        } else if (clazz == Character.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(L2I);
            il.append(I2C);
        } else if (clazz == java.lang.Boolean.TYPE) {
            il.append(new INVOKESTATIC(referenceToBoolean));
        } else if (clazz.getName().equals("java.lang.Boolean")) {
            il.append(new INVOKESTATIC(referenceToBoolean));
            Type.Boolean.translateTo(classGen, methodGen, Type.Reference);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        if (clazz.getName().equals("java.lang.Object")) {
            methodGen.getInstructionList().append(NOP);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        this.translateTo(classGen, methodGen, type);
        return new FlowList(il.append(new IFEQ(null)));
    }

    @Override
    public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
    }

    @Override
    public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
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

