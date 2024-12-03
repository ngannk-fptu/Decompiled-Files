/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.RelativePathPattern;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class AncestorPattern
extends RelativePathPattern {
    private final Pattern _left;
    private final RelativePathPattern _right;
    private InstructionHandle _loop;

    public AncestorPattern(RelativePathPattern right) {
        this(null, right);
    }

    public AncestorPattern(Pattern left, RelativePathPattern right) {
        this._left = left;
        this._right = right;
        this._right.setParent(this);
        if (left != null) {
            left.setParent(this);
        }
    }

    public InstructionHandle getLoopHandle() {
        return this._loop;
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        if (this._left != null) {
            this._left.setParser(parser);
        }
        this._right.setParser(parser);
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public StepPattern getKernelPattern() {
        return this._right.getKernelPattern();
    }

    @Override
    public void reduceKernelPattern() {
        this._right.reduceKernelPattern();
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._left != null) {
            this._left.typeCheck(stable);
        }
        return this._right.typeCheck(stable);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        LocalVariableGen local = methodGen.addLocalVariable2("app", Util.getJCRefType("I"), il.getEnd());
        ILOAD loadLocal = new ILOAD(local.getIndex());
        ISTORE storeLocal = new ISTORE(local.getIndex());
        if (this._right instanceof StepPattern) {
            il.append(DUP);
            il.append(storeLocal);
            this._right.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            il.append(loadLocal);
        } else {
            this._right.translate(classGen, methodGen);
            if (this._right instanceof AncestorPattern) {
                il.append(methodGen.loadDOM());
                il.append(SWAP);
            }
        }
        if (this._left != null) {
            int getParent = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getParent", "(I)I");
            InstructionHandle parent = il.append(new INVOKEINTERFACE(getParent, 2));
            il.append(DUP);
            il.append(storeLocal);
            this._falseList.add(il.append(new IFLT(null)));
            il.append(loadLocal);
            this._left.translate(classGen, methodGen);
            SyntaxTreeNode p = this.getParent();
            if (p != null && !(p instanceof Instruction) && !(p instanceof TopLevelElement)) {
                il.append(loadLocal);
            }
            BranchHandle exit = il.append(new GOTO(null));
            this._loop = il.append(methodGen.loadDOM());
            il.append(loadLocal);
            local.setEnd(this._loop);
            il.append(new GOTO(parent));
            exit.setTarget(il.append(NOP));
            this._left.backPatchFalseList(this._loop);
            this._trueList.append(this._left._trueList);
        } else {
            il.append(POP2);
        }
        if (this._right instanceof AncestorPattern) {
            AncestorPattern ancestor = (AncestorPattern)this._right;
            this._falseList.backPatch(ancestor.getLoopHandle());
        }
        this._trueList.append(this._right._trueList);
        this._falseList.append(this._right._falseList);
    }

    @Override
    public String toString() {
        return "AncestorPattern(" + this._left + ", " + this._right + ')';
    }
}

