/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.Util;

public final class ResultTreeType
extends Type {
    private final String _methodName;

    protected ResultTreeType() {
        this._methodName = null;
    }

    public ResultTreeType(String methodName) {
        this._methodName = methodName;
    }

    @Override
    public String toString() {
        return "result-tree";
    }

    @Override
    public boolean identicalTo(Type other) {
        return other instanceof ResultTreeType;
    }

    @Override
    public String toSignature() {
        return "Lorg/apache/xalan/xsltc/DOM;";
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return Util.getJCRefType(this.toSignature());
    }

    public String getMethodName() {
        return this._methodName;
    }

    @Override
    public boolean implementedAsMethod() {
        return this._methodName != null;
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        } else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        } else if (type == Type.NodeSet) {
            this.translateTo(classGen, methodGen, (NodeSetType)type);
        } else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        } else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (ObjectType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(POP);
        il.append(ICONST_1);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._methodName == null) {
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getStringValue", "()Ljava/lang/String;");
            il.append(new INVOKEINTERFACE(index, 1));
        } else {
            String className = classGen.getClassName();
            int current = methodGen.getLocalIndex("current");
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
                il.append(new CHECKCAST(cpg.addClass(className)));
            }
            il.append(DUP);
            il.append(new GETFIELD(cpg.addFieldref(className, "_dom", "Lorg/apache/xalan/xsltc/DOM;")));
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.StringValueHandler", "<init>", "()V");
            il.append(new NEW(cpg.addClass("org.apache.xalan.xsltc.runtime.StringValueHandler")));
            il.append(DUP);
            il.append(DUP);
            il.append(new INVOKESPECIAL(index));
            LocalVariableGen handler = methodGen.addLocalVariable("rt_to_string_handler", Util.getJCRefType("Lorg/apache/xalan/xsltc/runtime/StringValueHandler;"), null, null);
            handler.setStart(il.append(new ASTORE(handler.getIndex())));
            index = cpg.addMethodref(className, this._methodName, "(Lorg/apache/xalan/xsltc/DOM;" + TRANSLET_OUTPUT_SIG + ")V");
            il.append(new INVOKEVIRTUAL(index));
            handler.setEnd(il.append(new ALOAD(handler.getIndex())));
            index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;");
            il.append(new INVOKEVIRTUAL(index));
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
        this.translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._methodName == null) {
            il.append(NOP);
        } else {
            String className = classGen.getClassName();
            int current = methodGen.getLocalIndex("current");
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
                il.append(new CHECKCAST(cpg.addClass(className)));
            }
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadDOM());
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getResultTreeFrag", "(IZ)Lorg/apache/xalan/xsltc/DOM;");
            il.append(new PUSH(cpg, 32));
            il.append(new PUSH(cpg, false));
            il.append(new INVOKEINTERFACE(index, 3));
            il.append(DUP);
            LocalVariableGen newDom = methodGen.addLocalVariable("rt_to_reference_dom", Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), null, null);
            il.append(new CHECKCAST(cpg.addClass("Lorg/apache/xalan/xsltc/DOM;")));
            newDom.setStart(il.append(new ASTORE(newDom.getIndex())));
            index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getOutputDomBuilder", "()" + TRANSLET_OUTPUT_SIG);
            il.append(new INVOKEINTERFACE(index, 1));
            il.append(DUP);
            il.append(DUP);
            LocalVariableGen domBuilder = methodGen.addLocalVariable("rt_to_reference_handler", Util.getJCRefType(TRANSLET_OUTPUT_SIG), null, null);
            domBuilder.setStart(il.append(new ASTORE(domBuilder.getIndex())));
            index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "startDocument", "()V");
            il.append(new INVOKEINTERFACE(index, 1));
            index = cpg.addMethodref(className, this._methodName, "(Lorg/apache/xalan/xsltc/DOM;" + TRANSLET_OUTPUT_SIG + ")V");
            il.append(new INVOKEVIRTUAL(index));
            domBuilder.setEnd(il.append(new ALOAD(domBuilder.getIndex())));
            index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "endDocument", "()V");
            il.append(new INVOKEINTERFACE(index, 1));
            newDom.setEnd(il.append(new ALOAD(newDom.getIndex())));
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeSetType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(DUP);
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
        int mapping = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "setupMapping", "([Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
        il.append(new INVOKEINTERFACE(mapping, 5));
        il.append(DUP);
        int iter = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getIterator", "()Lorg/apache/xml/dtm/DTMAxisIterator;");
        il.append(new INVOKEINTERFACE(iter, 1));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
        methodGen.getInstructionList().append(NOP);
    }

    @Override
    public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        this.translateTo(classGen, methodGen, Type.Boolean);
        return new FlowList(il.append(new IFEQ(null)));
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        String className = clazz.getName();
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (className.equals("org.w3c.dom.Node")) {
            this.translateTo(classGen, methodGen, Type.NodeSet);
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "makeNode", "(Lorg/apache/xml/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
            il.append(new INVOKEINTERFACE(index, 2));
        } else if (className.equals("org.w3c.dom.NodeList")) {
            this.translateTo(classGen, methodGen, Type.NodeSet);
            int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "makeNodeList", "(Lorg/apache/xml/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
            il.append(new INVOKEINTERFACE(index, 2));
        } else if (className.equals("java.lang.Object")) {
            il.append(NOP);
        } else if (className.equals("java.lang.String")) {
            this.translateTo(classGen, methodGen, Type.String);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)className);
            classGen.getParser().reportError(2, err);
        }
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
        return "org.apache.xalan.xsltc.DOM";
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

