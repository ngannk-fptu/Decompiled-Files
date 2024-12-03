/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class KeyCall
extends FunctionCall {
    private Expression _name;
    private Expression _value;
    private Type _valueType;
    private QName _resolvedQName = null;

    public KeyCall(QName fname, Vector arguments) {
        super(fname, arguments);
        switch (this.argumentCount()) {
            case 1: {
                this._name = null;
                this._value = this.argument(0);
                break;
            }
            case 2: {
                this._name = this.argument(0);
                this._value = this.argument(1);
                break;
            }
            default: {
                this._value = null;
                this._name = null;
            }
        }
    }

    public void addParentDependency() {
        SyntaxTreeNode node;
        if (this._resolvedQName == null) {
            return;
        }
        for (node = this; node != null && !(node instanceof TopLevelElement); node = node.getParent()) {
        }
        TopLevelElement parent = (TopLevelElement)node;
        if (parent != null) {
            parent.addDependency(this.getSymbolTable().getKey(this._resolvedQName));
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type returnType = super.typeCheck(stable);
        if (this._name != null) {
            Type nameType = this._name.typeCheck(stable);
            if (this._name instanceof LiteralExpr) {
                LiteralExpr literal = (LiteralExpr)this._name;
                this._resolvedQName = this.getParser().getQNameIgnoreDefaultNs(literal.getValue());
            } else if (!(nameType instanceof StringType)) {
                this._name = new CastExpr(this._name, Type.String);
            }
        }
        this._valueType = this._value.typeCheck(stable);
        if (this._valueType != Type.NodeSet && this._valueType != Type.Reference && this._valueType != Type.String) {
            this._value = new CastExpr(this._value, Type.String);
            this._valueType = this._value.typeCheck(stable);
        }
        this.addParentDependency();
        return returnType;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int getKeyIndex = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lorg/apache/xalan/xsltc/dom/KeyIndex;");
        int keyDom = cpg.addMethodref("org/apache/xalan/xsltc/dom/KeyIndex", "setDom", "(Lorg/apache/xalan/xsltc/DOM;)V");
        int getKeyIterator = cpg.addMethodref("org/apache/xalan/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + this._valueType.toSignature() + "Z)" + "Lorg/apache/xalan/xsltc/dom/KeyIndex$KeyIndexIterator;");
        il.append(classGen.loadTranslet());
        if (this._name == null) {
            il.append(new PUSH(cpg, "##id"));
        } else if (this._resolvedQName != null) {
            il.append(new PUSH(cpg, this._resolvedQName.toString()));
        } else {
            this._name.translate(classGen, methodGen);
        }
        il.append(new INVOKEVIRTUAL(getKeyIndex));
        il.append(DUP);
        il.append(methodGen.loadDOM());
        il.append(new INVOKEVIRTUAL(keyDom));
        this._value.translate(classGen, methodGen);
        il.append(this._name != null ? ICONST_1 : ICONST_0);
        il.append(new INVOKEVIRTUAL(getKeyIterator));
    }
}

