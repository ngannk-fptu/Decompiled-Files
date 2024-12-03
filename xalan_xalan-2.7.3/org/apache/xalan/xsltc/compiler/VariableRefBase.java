/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Closure;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

class VariableRefBase
extends Expression {
    protected VariableBase _variable;
    protected Closure _closure = null;

    public VariableRefBase(VariableBase variable) {
        this._variable = variable;
        variable.addReference(this);
    }

    public VariableRefBase() {
        this._variable = null;
    }

    public VariableBase getVariable() {
        return this._variable;
    }

    public void addParentDependency() {
        SyntaxTreeNode node;
        for (node = this; node != null && !(node instanceof TopLevelElement); node = node.getParent()) {
        }
        TopLevelElement parent = (TopLevelElement)node;
        if (parent != null) {
            VariableBase var = this._variable;
            if (this._variable._ignore) {
                if (this._variable instanceof Variable) {
                    var = parent.getSymbolTable().lookupVariable(this._variable._name);
                } else if (this._variable instanceof Param) {
                    var = parent.getSymbolTable().lookupParam(this._variable._name);
                }
            }
            parent.addDependency(var);
        }
    }

    public boolean equals(Object obj) {
        try {
            return this._variable == ((VariableRefBase)obj)._variable;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "variable-ref(" + this._variable.getName() + '/' + this._variable.getType() + ')';
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._type != null) {
            return this._type;
        }
        if (this._variable.isLocal()) {
            SyntaxTreeNode node = this.getParent();
            do {
                if (!(node instanceof Closure)) continue;
                this._closure = (Closure)((Object)node);
                break;
            } while (!(node instanceof TopLevelElement) && (node = node.getParent()) != null);
            if (this._closure != null) {
                this._closure.addVariable(this);
            }
        }
        this._type = this._variable.getType();
        if (this._type == null) {
            this._variable.typeCheck(stable);
            this._type = this._variable.getType();
        }
        this.addParentDependency();
        return this._type;
    }
}

