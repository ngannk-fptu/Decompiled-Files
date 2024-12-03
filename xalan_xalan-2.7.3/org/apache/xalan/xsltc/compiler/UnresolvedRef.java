/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.ParameterRef;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.VariableRef;
import org.apache.xalan.xsltc.compiler.VariableRefBase;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class UnresolvedRef
extends VariableRefBase {
    private QName _variableName = null;
    private VariableRefBase _ref = null;

    public UnresolvedRef(QName name) {
        this._variableName = name;
    }

    public QName getName() {
        return this._variableName;
    }

    private ErrorMsg reportError() {
        ErrorMsg err = new ErrorMsg("VARIABLE_UNDEF_ERR", (Object)this._variableName, this);
        this.getParser().reportError(3, err);
        return err;
    }

    private VariableRefBase resolve(Parser parser, SymbolTable stable) {
        VariableBase ref = parser.lookupVariable(this._variableName);
        if (ref == null) {
            ref = (VariableBase)stable.lookupName(this._variableName);
        }
        if (ref == null) {
            this.reportError();
            return null;
        }
        this._variable = ref;
        this.addParentDependency();
        if (ref instanceof Variable) {
            return new VariableRef((Variable)ref);
        }
        if (ref instanceof Param) {
            return new ParameterRef((Param)ref);
        }
        return null;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._ref != null) {
            String name = this._variableName.toString();
            ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", (Object)name, this);
        }
        if ((this._ref = this.resolve(this.getParser(), stable)) != null) {
            this._type = this._ref.typeCheck(stable);
            return this._type;
        }
        throw new TypeCheckError(this.reportError());
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        if (this._ref != null) {
            this._ref.translate(classGen, methodGen);
        } else {
            this.reportError();
        }
    }

    @Override
    public String toString() {
        return "unresolved-ref()";
    }
}

