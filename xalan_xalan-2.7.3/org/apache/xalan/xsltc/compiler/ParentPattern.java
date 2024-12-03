/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.xalan.xsltc.compiler.AncestorPattern;
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

final class ParentPattern
extends RelativePathPattern {
    private final Pattern _left;
    private final RelativePathPattern _right;

    public ParentPattern(Pattern left, RelativePathPattern right) {
        this._left = left;
        this._left.setParent(this);
        this._right = right;
        this._right.setParent(this);
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
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
        this._left.typeCheck(stable);
        return this._right.typeCheck(stable);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        LocalVariableGen local = methodGen.addLocalVariable2("ppt", Util.getJCRefType("I"), null);
        ILOAD loadLocal = new ILOAD(local.getIndex());
        ISTORE storeLocal = new ISTORE(local.getIndex());
        if (this._right.isWildcard()) {
            il.append(methodGen.loadDOM());
            il.append(SWAP);
        } else if (this._right instanceof StepPattern) {
            il.append(DUP);
            local.setStart(il.append(storeLocal));
            this._right.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            local.setEnd(il.append(loadLocal));
        } else {
            this._right.translate(classGen, methodGen);
            if (this._right instanceof AncestorPattern) {
                il.append(methodGen.loadDOM());
                il.append(SWAP);
            }
        }
        int getParent = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(getParent, 2));
        SyntaxTreeNode p = this.getParent();
        if (p == null || p instanceof Instruction || p instanceof TopLevelElement) {
            this._left.translate(classGen, methodGen);
        } else {
            il.append(DUP);
            InstructionHandle storeInst = il.append(storeLocal);
            if (local.getStart() == null) {
                local.setStart(storeInst);
            }
            this._left.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            local.setEnd(il.append(loadLocal));
        }
        methodGen.removeLocalVariable(local);
        if (this._right instanceof AncestorPattern) {
            AncestorPattern ancestor = (AncestorPattern)this._right;
            this._left.backPatchFalseList(ancestor.getLoopHandle());
        }
        this._trueList.append(this._right._trueList.append(this._left._trueList));
        this._falseList.append(this._right._falseList.append(this._left._falseList));
    }

    @Override
    public String toString() {
        return "Parent(" + this._left + ", " + this._right + ')';
    }
}

