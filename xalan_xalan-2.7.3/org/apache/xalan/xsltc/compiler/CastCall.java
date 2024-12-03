/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class CastCall
extends FunctionCall {
    private String _className;
    private Expression _right;

    public CastCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this.argumentCount() != 2) {
            throw new TypeCheckError(new ErrorMsg("ILLEGAL_ARG_ERR", (Object)this.getName(), this));
        }
        Expression exp = this.argument(0);
        if (!(exp instanceof LiteralExpr)) {
            throw new TypeCheckError(new ErrorMsg("NEED_LITERAL_ERR", (Object)this.getName(), this));
        }
        this._className = ((LiteralExpr)exp).getValue();
        this._type = Type.newObjectType(this._className);
        this._right = this.argument(1);
        Type tright = this._right.typeCheck(stable);
        if (tright != Type.Reference && !(tright instanceof ObjectType)) {
            throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", tright, this._type, this));
        }
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this._right.translate(classGen, methodGen);
        il.append(new CHECKCAST(cpg.addClass(this._className)));
    }
}

