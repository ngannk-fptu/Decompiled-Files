/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.VariableRefBase;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodType;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

abstract class Expression
extends SyntaxTreeNode {
    protected Type _type;
    protected FlowList _trueList = new FlowList();
    protected FlowList _falseList = new FlowList();

    Expression() {
    }

    public Type getType() {
        return this._type;
    }

    public abstract String toString();

    public boolean hasPositionCall() {
        return false;
    }

    public boolean hasLastCall() {
        return false;
    }

    public Object evaluateAtCompileTime() {
        return null;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return this.typeCheckContents(stable);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ErrorMsg msg = new ErrorMsg("NOT_IMPLEMENTED_ERR", this.getClass(), this);
        this.getParser().reportError(2, msg);
    }

    public final InstructionList compile(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList save = methodGen.getInstructionList();
        InstructionList result = new InstructionList();
        methodGen.setInstructionList(result);
        this.translate(classGen, methodGen);
        methodGen.setInstructionList(save);
        return result;
    }

    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        this.translate(classGen, methodGen);
        if (this._type instanceof BooleanType) {
            this.desynthesize(classGen, methodGen);
        }
    }

    public void startIterator(ClassGenerator classGen, MethodGenerator methodGen) {
        if (!(this._type instanceof NodeSetType)) {
            return;
        }
        Expression expr = this;
        if (expr instanceof CastExpr) {
            expr = ((CastExpr)expr).getExpr();
        }
        if (!(expr instanceof VariableRefBase)) {
            InstructionList il = methodGen.getInstructionList();
            il.append(methodGen.loadContextNode());
            il.append(methodGen.setStartNode());
        }
    }

    public void synthesize(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this._trueList.backPatch(il.append(ICONST_1));
        BranchHandle truec = il.append(new GOTO_W(null));
        this._falseList.backPatch(il.append(ICONST_0));
        truec.setTarget(il.append(NOP));
    }

    public void desynthesize(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        this._falseList.add(il.append(new IFEQ(null)));
    }

    public FlowList getFalseList() {
        return this._falseList;
    }

    public FlowList getTrueList() {
        return this._trueList;
    }

    public void backPatchFalseList(InstructionHandle ih) {
        this._falseList.backPatch(ih);
    }

    public void backPatchTrueList(InstructionHandle ih) {
        this._trueList.backPatch(ih);
    }

    public MethodType lookupPrimop(SymbolTable stable, String op, MethodType ctype) {
        MethodType result = null;
        Vector primop = stable.lookupPrimop(op);
        if (primop != null) {
            int n = primop.size();
            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < n; ++i) {
                int distance;
                MethodType ptype = (MethodType)primop.elementAt(i);
                if (ptype.argsCount() != ctype.argsCount()) continue;
                if (result == null) {
                    result = ptype;
                }
                if ((distance = ctype.distanceTo(ptype)) >= minDistance) continue;
                minDistance = distance;
                result = ptype;
            }
        }
        return result;
    }
}

