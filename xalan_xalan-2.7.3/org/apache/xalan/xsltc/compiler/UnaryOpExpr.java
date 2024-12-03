/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class UnaryOpExpr
extends Expression {
    private Expression _left;

    public UnaryOpExpr(Expression left) {
        this._left = left;
        this._left.setParent(this);
    }

    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall();
    }

    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall();
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type tleft = this._left.typeCheck(stable);
        MethodType ptype = this.lookupPrimop(stable, "u-", new MethodType(Type.Void, tleft));
        if (ptype != null) {
            Type arg1 = (Type)ptype.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            this._type = ptype.resultType();
            return this._type;
        }
        throw new TypeCheckError(this);
    }

    @Override
    public String toString() {
        return "u-(" + this._left + ')';
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        this._left.translate(classGen, methodGen);
        il.append(this._type.NEG());
    }
}

