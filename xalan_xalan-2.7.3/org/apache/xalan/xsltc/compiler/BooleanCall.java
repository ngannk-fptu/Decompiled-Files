/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class BooleanCall
extends FunctionCall {
    private Expression _arg = this.argument(0);

    public BooleanCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this._arg.typeCheck(stable);
        this._type = Type.Boolean;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        this._arg.translate(classGen, methodGen);
        Type targ = this._arg.getType();
        if (!targ.identicalTo(Type.Boolean)) {
            this._arg.startIterator(classGen, methodGen);
            targ.translateTo(classGen, methodGen, Type.Boolean);
        }
    }
}

