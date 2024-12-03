/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
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

final class StartsWithCall
extends FunctionCall {
    private Expression _base = null;
    private Expression _token = null;

    public StartsWithCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this.argumentCount() != 2) {
            ErrorMsg err = new ErrorMsg("ILLEGAL_ARG_ERR", (Object)this.getName(), this);
            throw new TypeCheckError(err);
        }
        this._base = this.argument(0);
        Type baseType = this._base.typeCheck(stable);
        if (baseType != Type.String) {
            this._base = new CastExpr(this._base, Type.String);
        }
        this._token = this.argument(1);
        Type tokenType = this._token.typeCheck(stable);
        if (tokenType != Type.String) {
            this._token = new CastExpr(this._token, Type.String);
        }
        this._type = Type.Boolean;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this._base.translate(classGen, methodGen);
        this._token.translate(classGen, methodGen);
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.String", "startsWith", "(Ljava/lang/String;)Z")));
    }
}

