/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.AncestorPattern;
import org.apache.xalan.xsltc.compiler.LocationPathPattern;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.RelativePathPattern;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class AbsolutePathPattern
extends LocationPathPattern {
    private final RelativePathPattern _left;

    public AbsolutePathPattern(RelativePathPattern left) {
        this._left = left;
        if (left != null) {
            left.setParent(this);
        }
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        if (this._left != null) {
            this._left.setParser(parser);
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return this._left == null ? Type.Root : this._left.typeCheck(stable);
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public StepPattern getKernelPattern() {
        return this._left != null ? this._left.getKernelPattern() : null;
    }

    @Override
    public void reduceKernelPattern() {
        this._left.reduceKernelPattern();
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._left != null) {
            if (this._left instanceof StepPattern) {
                LocalVariableGen local = methodGen.addLocalVariable2("apptmp", Util.getJCRefType("I"), null);
                il.append(DUP);
                local.setStart(il.append(new ISTORE(local.getIndex())));
                this._left.translate(classGen, methodGen);
                il.append(methodGen.loadDOM());
                local.setEnd(il.append(new ILOAD(local.getIndex())));
                methodGen.removeLocalVariable(local);
            } else {
                this._left.translate(classGen, methodGen);
            }
        }
        int getParent = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getParent", "(I)I");
        int getType = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getExpandedTypeID", "(I)I");
        InstructionHandle begin = il.append(methodGen.loadDOM());
        il.append(SWAP);
        il.append(new INVOKEINTERFACE(getParent, 2));
        if (this._left instanceof AncestorPattern) {
            il.append(methodGen.loadDOM());
            il.append(SWAP);
        }
        il.append(new INVOKEINTERFACE(getType, 2));
        il.append(new PUSH(cpg, 9));
        BranchHandle skip = il.append(new IF_ICMPEQ(null));
        this._falseList.add(il.append(new GOTO_W(null)));
        skip.setTarget(il.append(NOP));
        if (this._left != null) {
            this._left.backPatchTrueList(begin);
            if (this._left instanceof AncestorPattern) {
                AncestorPattern ancestor = (AncestorPattern)this._left;
                this._falseList.backPatch(ancestor.getLoopHandle());
            }
            this._falseList.append(this._left._falseList);
        }
    }

    @Override
    public String toString() {
        return "absolutePathPattern(" + (this._left != null ? this._left.toString() : ")");
    }
}

