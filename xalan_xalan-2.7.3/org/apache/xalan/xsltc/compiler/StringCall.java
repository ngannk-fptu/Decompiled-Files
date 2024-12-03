/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class StringCall
extends FunctionCall {
    public StringCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        int argc = this.argumentCount();
        if (argc > 1) {
            ErrorMsg err = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(err);
        }
        if (argc > 0) {
            this.argument().typeCheck(stable);
        }
        this._type = Type.String;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Type targ;
        InstructionList il = methodGen.getInstructionList();
        if (this.argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
            targ = Type.Node;
        } else {
            Expression arg = this.argument();
            arg.translate(classGen, methodGen);
            arg.startIterator(classGen, methodGen);
            targ = arg.getType();
        }
        if (!targ.identicalTo(Type.String)) {
            targ.translateTo(classGen, methodGen, Type.String);
        }
    }
}

