/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.NotCall;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class LogicalExpr
extends Expression {
    public static final int OR = 0;
    public static final int AND = 1;
    private final int _op;
    private Expression _left;
    private Expression _right;
    private static final String[] Ops = new String[]{"or", "and"};

    public LogicalExpr(int op, Expression left, Expression right) {
        this._op = op;
        this._left = left;
        this._left.setParent(this);
        this._right = right;
        this._right.setParent(this);
    }

    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall() || this._right.hasPositionCall();
    }

    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall() || this._right.hasLastCall();
    }

    @Override
    public Object evaluateAtCompileTime() {
        Object leftb = this._left.evaluateAtCompileTime();
        Object rightb = this._right.evaluateAtCompileTime();
        if (leftb == null || rightb == null) {
            return null;
        }
        if (this._op == 1) {
            return leftb == Boolean.TRUE && rightb == Boolean.TRUE ? Boolean.TRUE : Boolean.FALSE;
        }
        return leftb == Boolean.TRUE || rightb == Boolean.TRUE ? Boolean.TRUE : Boolean.FALSE;
    }

    public int getOp() {
        return this._op;
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
        this._right.setParser(parser);
    }

    @Override
    public String toString() {
        return Ops[this._op] + '(' + this._left + ", " + this._right + ')';
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type tright;
        Type tleft = this._left.typeCheck(stable);
        MethodType wantType = new MethodType(Type.Void, tleft, tright = this._right.typeCheck(stable));
        MethodType haveType = this.lookupPrimop(stable, Ops[this._op], wantType);
        if (haveType != null) {
            Type arg2;
            Type arg1 = (Type)haveType.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            if (!(arg2 = (Type)haveType.argsType().elementAt(1)).identicalTo(tright)) {
                this._right = new CastExpr(this._right, arg1);
            }
            this._type = haveType.resultType();
            return this._type;
        }
        throw new TypeCheckError(this);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        this.translateDesynthesized(classGen, methodGen);
        this.synthesize(classGen, methodGen);
    }

    @Override
    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        SyntaxTreeNode parent = this.getParent();
        if (this._op == 1) {
            this._left.translateDesynthesized(classGen, methodGen);
            InstructionHandle middle = il.append(NOP);
            this._right.translateDesynthesized(classGen, methodGen);
            InstructionHandle after = il.append(NOP);
            this._falseList.append(this._right._falseList.append(this._left._falseList));
            if (this._left instanceof LogicalExpr && ((LogicalExpr)this._left).getOp() == 0) {
                this._left.backPatchTrueList(middle);
            } else if (this._left instanceof NotCall) {
                this._left.backPatchTrueList(middle);
            } else {
                this._trueList.append(this._left._trueList);
            }
            if (this._right instanceof LogicalExpr && ((LogicalExpr)this._right).getOp() == 0) {
                this._right.backPatchTrueList(after);
            } else if (this._right instanceof NotCall) {
                this._right.backPatchTrueList(after);
            } else {
                this._trueList.append(this._right._trueList);
            }
        } else {
            this._left.translateDesynthesized(classGen, methodGen);
            BranchHandle ih = il.append(new GOTO(null));
            this._right.translateDesynthesized(classGen, methodGen);
            this._left._trueList.backPatch(ih);
            this._left._falseList.backPatch(ih.getNext());
            this._falseList.append(this._right._falseList);
            this._trueList.add(ih).append(this._right._trueList);
        }
    }
}

