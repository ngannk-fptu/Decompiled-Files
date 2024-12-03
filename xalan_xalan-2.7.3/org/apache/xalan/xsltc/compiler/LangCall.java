/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.FilterGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class LangCall
extends FunctionCall {
    private Expression _lang = this.argument(0);
    private Type _langType;

    public LangCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this._langType = this._lang.typeCheck(stable);
        if (!(this._langType instanceof StringType)) {
            this._lang = new CastExpr(this._lang, Type.String);
        }
        return Type.Boolean;
    }

    @Override
    public Type getType() {
        return Type.Boolean;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int tst = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "testLanguage", "(Ljava/lang/String;Lorg/apache/xalan/xsltc/DOM;I)Z");
        this._lang.translate(classGen, methodGen);
        il.append(methodGen.loadDOM());
        if (classGen instanceof FilterGenerator) {
            il.append(new ILOAD(1));
        } else {
            il.append(methodGen.loadContextNode());
        }
        il.append(new INVOKESTATIC(tst));
    }
}

