/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Predicate;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class ProcessingInstructionPattern
extends StepPattern {
    private String _name = null;
    private boolean _typeChecked = false;

    public ProcessingInstructionPattern(String name) {
        super(3, 7, null);
        this._name = name;
    }

    @Override
    public double getDefaultPriority() {
        return this._name != null ? 0.0 : -0.5;
    }

    @Override
    public String toString() {
        if (this._predicates == null) {
            return "processing-instruction(" + this._name + ")";
        }
        return "processing-instruction(" + this._name + ")" + this._predicates;
    }

    @Override
    public void reduceKernelPattern() {
        this._typeChecked = true;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this.hasPredicates()) {
            int n = this._predicates.size();
            for (int i = 0; i < n; ++i) {
                Predicate pred = (Predicate)this._predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }
        return Type.NodeSet;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int gname = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
        int cmp = cpg.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
        il.append(methodGen.loadCurrentNode());
        il.append(SWAP);
        il.append(methodGen.storeCurrentNode());
        if (!this._typeChecked) {
            il.append(methodGen.loadCurrentNode());
            int getType = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getExpandedTypeID", "(I)I");
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKEINTERFACE(getType, 2));
            il.append(new PUSH(cpg, 7));
            this._falseList.add(il.append(new IF_ICMPEQ(null)));
        }
        il.append(new PUSH(cpg, this._name));
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(gname, 2));
        il.append(new INVOKEVIRTUAL(cmp));
        this._falseList.add(il.append(new IFEQ(null)));
        if (this.hasPredicates()) {
            int n = this._predicates.size();
            for (int i = 0; i < n; ++i) {
                Predicate pred = (Predicate)this._predicates.elementAt(i);
                Expression exp = pred.getExpr();
                exp.translateDesynthesized(classGen, methodGen);
                this._trueList.append(exp._trueList);
                this._falseList.append(exp._falseList);
            }
        }
        InstructionHandle restore = il.append(methodGen.storeCurrentNode());
        this.backPatchTrueList(restore);
        BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeCurrentNode());
        this.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(NOP));
    }
}

