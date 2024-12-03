/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class DocumentCall
extends FunctionCall {
    private Expression _arg1 = null;
    private Expression _arg2 = null;
    private Type _arg1Type;

    public DocumentCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        int ac = this.argumentCount();
        if (ac < 1 || ac > 2) {
            ErrorMsg msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        if (this.getStylesheet() == null) {
            ErrorMsg msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        this._arg1 = this.argument(0);
        if (this._arg1 == null) {
            ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        this._arg1Type = this._arg1.typeCheck(stable);
        if (this._arg1Type != Type.NodeSet && this._arg1Type != Type.String) {
            this._arg1 = new CastExpr(this._arg1, Type.String);
        }
        if (ac == 2) {
            this._arg2 = this.argument(1);
            if (this._arg2 == null) {
                ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                throw new TypeCheckError(msg);
            }
            Type arg2Type = this._arg2.typeCheck(stable);
            if (arg2Type.identicalTo(Type.Node)) {
                this._arg2 = new CastExpr(this._arg2, Type.NodeSet);
            } else if (!arg2Type.identicalTo(Type.NodeSet)) {
                ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                throw new TypeCheckError(msg);
            }
        }
        this._type = Type.NodeSet;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int ac = this.argumentCount();
        int domField = cpg.addFieldref(classGen.getClassName(), "_dom", "Lorg/apache/xalan/xsltc/DOM;");
        String docParamList = null;
        docParamList = ac == 1 ? "(Ljava/lang/Object;Ljava/lang/String;Lorg/apache/xalan/xsltc/runtime/AbstractTranslet;Lorg/apache/xalan/xsltc/DOM;)Lorg/apache/xml/dtm/DTMAxisIterator;" : "(Ljava/lang/Object;Lorg/apache/xml/dtm/DTMAxisIterator;Ljava/lang/String;Lorg/apache/xalan/xsltc/runtime/AbstractTranslet;Lorg/apache/xalan/xsltc/DOM;)Lorg/apache/xml/dtm/DTMAxisIterator;";
        int docIdx = cpg.addMethodref("org.apache.xalan.xsltc.dom.LoadDocument", "documentF", docParamList);
        this._arg1.translate(classGen, methodGen);
        if (this._arg1Type == Type.NodeSet) {
            this._arg1.startIterator(classGen, methodGen);
        }
        if (ac == 2) {
            this._arg2.translate(classGen, methodGen);
            this._arg2.startIterator(classGen, methodGen);
        }
        il.append(new PUSH(cpg, this.getStylesheet().getSystemId()));
        il.append(classGen.loadTranslet());
        il.append(DUP);
        il.append(new GETFIELD(domField));
        il.append(new INVOKESTATIC(docIdx));
    }
}

