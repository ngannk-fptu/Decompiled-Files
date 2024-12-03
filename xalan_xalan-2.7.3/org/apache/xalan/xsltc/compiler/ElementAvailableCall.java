/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class ElementAvailableCall
extends FunctionCall {
    public ElementAvailableCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this.argument() instanceof LiteralExpr) {
            this._type = Type.Boolean;
            return this._type;
        }
        ErrorMsg err = new ErrorMsg("NEED_LITERAL_ERR", (Object)"element-available", this);
        throw new TypeCheckError(err);
    }

    @Override
    public Object evaluateAtCompileTime() {
        return this.getResult() ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean getResult() {
        try {
            LiteralExpr arg = (LiteralExpr)this.argument();
            String qname = arg.getValue();
            int index = qname.indexOf(58);
            String localName = index > 0 ? qname.substring(index + 1) : qname;
            return this.getParser().elementSupported(arg.getNamespace(), localName);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        boolean result = this.getResult();
        methodGen.getInstructionList().append(new PUSH(cpg, result));
    }
}

