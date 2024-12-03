/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFLT;
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
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;

public final class NodeSetType
extends Type {
    protected NodeSetType() {
    }

    @Override
    public String toString() {
        return "node-set";
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        return "Lorg/apache/xml/dtm/DTMAxisIterator;";
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return new org.apache.bcel.generic.ObjectType("org.apache.xml.dtm.DTMAxisIterator");
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        } else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        } else if (type == Type.Node) {
            this.translateTo(classGen, methodGen, (NodeType)type);
        } else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        } else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (ObjectType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        InstructionList il = methodGen.getInstructionList();
        ConstantPoolGen cpg = classGen.getConstantPool();
        if (clazz.getName().equals("org.w3c.dom.NodeList")) {
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            int convert = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "nodeList2Iterator", "(Lorg/w3c/dom/NodeList;Lorg/apache/xalan/xsltc/Translet;Lorg/apache/xalan/xsltc/DOM;)Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(new INVOKESTATIC(convert));
        } else if (clazz.getName().equals("org.w3c.dom.Node")) {
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            int convert = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "node2Iterator", "(Lorg/w3c/dom/Node;Lorg/apache/xalan/xsltc/Translet;Lorg/apache/xalan/xsltc/DOM;)Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(new INVOKESTATIC(convert));
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        FlowList falsel = this.translateToDesynthesized(classGen, methodGen, type);
        il.append(ICONST_1);
        BranchHandle truec = il.append(new GOTO(null));
        falsel.backPatch(il.append(ICONST_0));
        truec.setTarget(il.append(NOP));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        InstructionList il = methodGen.getInstructionList();
        this.getFirstNode(classGen, methodGen);
        il.append(DUP);
        BranchHandle falsec = il.append(new IFLT(null));
        Type.Node.translateTo(classGen, methodGen, type);
        BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(POP));
        il.append(new PUSH(classGen.getConstantPool(), ""));
        truec.setTarget(il.append(NOP));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
        this.translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeType type) {
        this.getFirstNode(classGen, methodGen);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        this.getFirstNode(classGen, methodGen);
        return new FlowList(il.append(new IFLT(null)));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        String className = clazz.getName();
        il.append(methodGen.loadDOM());
        il.append(SWAP);
        if (className.equals("org.w3c.dom.Node")) {
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "makeNode", "(Lorg/apache/xml/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
            il.append(new INVOKEINTERFACE(index, 2));
        } else if (className.equals("org.w3c.dom.NodeList") || className.equals("java.lang.Object")) {
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "makeNodeList", "(Lorg/apache/xml/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
            il.append(new INVOKEINTERFACE(index, 2));
        } else if (className.equals("java.lang.String")) {
            int next = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "next", "()I");
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
            il.append(new INVOKEINTERFACE(next, 1));
            il.append(new INVOKEINTERFACE(index, 2));
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)className);
            classGen.getParser().reportError(2, err);
        }
    }

    private void getFirstNode(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "next", "()I"), 1));
    }

    @Override
    public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }

    @Override
    public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public String getClassName() {
        return "org.apache.xml.dtm.DTMAxisIterator";
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

