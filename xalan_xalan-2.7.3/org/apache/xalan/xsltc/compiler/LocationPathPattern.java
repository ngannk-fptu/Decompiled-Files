/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

public abstract class LocationPathPattern
extends Pattern {
    private Template _template;
    private int _importPrecedence;
    private double _priority = Double.NaN;
    private int _position = 0;

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
    }

    public void setTemplate(Template template) {
        this._template = template;
        this._priority = template.getPriority();
        this._importPrecedence = template.getImportPrecedence();
        this._position = template.getPosition();
    }

    @Override
    public Template getTemplate() {
        return this._template;
    }

    @Override
    public final double getPriority() {
        return Double.isNaN(this._priority) ? this.getDefaultPriority() : this._priority;
    }

    public double getDefaultPriority() {
        return 0.5;
    }

    public boolean noSmallerThan(LocationPathPattern other) {
        if (this._importPrecedence > other._importPrecedence) {
            return true;
        }
        if (this._importPrecedence == other._importPrecedence) {
            if (this._priority > other._priority) {
                return true;
            }
            if (this._priority == other._priority && this._position > other._position) {
                return true;
            }
        }
        return false;
    }

    public abstract StepPattern getKernelPattern();

    public abstract void reduceKernelPattern();

    public abstract boolean isWildcard();

    public int getAxis() {
        StepPattern sp = this.getKernelPattern();
        return sp != null ? sp.getAxis() : 3;
    }

    @Override
    public String toString() {
        return "root()";
    }
}

