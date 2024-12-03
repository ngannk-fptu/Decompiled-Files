/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class If
extends Instruction {
    private Expression _test;
    private boolean _ignore = false;

    If() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("If");
        this.indent(indent + 4);
        System.out.print("test ");
        Util.println(this._test.toString());
        this.displayContents(indent + 4);
    }

    @Override
    public void parseContents(Parser parser) {
        this._test = parser.parseExpression(this, "test", null);
        if (this._test.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "test");
            return;
        }
        Object result = this._test.evaluateAtCompileTime();
        if (result != null && result instanceof Boolean) {
            this._ignore = (Boolean)result == false;
        }
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (!(this._test.typeCheck(stable) instanceof BooleanType)) {
            this._test = new CastExpr(this._test, Type.Boolean);
        }
        if (!this._ignore) {
            this.typeCheckContents(stable);
        }
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        this._test.translateDesynthesized(classGen, methodGen);
        InstructionHandle truec = il.getEnd();
        if (!this._ignore) {
            this.translateContents(classGen, methodGen);
        }
        this._test.backPatchFalseList(il.append(NOP));
        this._test.backPatchTrueList(truec.getNext());
    }
}

