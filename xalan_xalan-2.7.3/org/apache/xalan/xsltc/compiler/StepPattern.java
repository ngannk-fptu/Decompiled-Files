/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Predicate;
import org.apache.xalan.xsltc.compiler.RelativePathPattern;
import org.apache.xalan.xsltc.compiler.Step;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.dtm.Axis;

class StepPattern
extends RelativePathPattern {
    private static final int NO_CONTEXT = 0;
    private static final int SIMPLE_CONTEXT = 1;
    private static final int GENERAL_CONTEXT = 2;
    protected final int _axis;
    protected final int _nodeType;
    protected Vector _predicates;
    private Step _step = null;
    private boolean _isEpsilon = false;
    private int _contextCase;
    private double _priority = Double.MAX_VALUE;

    public StepPattern(int axis, int nodeType, Vector predicates) {
        this._axis = axis;
        this._nodeType = nodeType;
        this._predicates = predicates;
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        if (this._predicates != null) {
            int n = this._predicates.size();
            for (int i = 0; i < n; ++i) {
                Predicate exp = (Predicate)this._predicates.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }

    public int getNodeType() {
        return this._nodeType;
    }

    public void setPriority(double priority) {
        this._priority = priority;
    }

    @Override
    public StepPattern getKernelPattern() {
        return this;
    }

    @Override
    public boolean isWildcard() {
        return this._isEpsilon && !this.hasPredicates();
    }

    public StepPattern setPredicates(Vector predicates) {
        this._predicates = predicates;
        return this;
    }

    protected boolean hasPredicates() {
        return this._predicates != null && this._predicates.size() > 0;
    }

    @Override
    public double getDefaultPriority() {
        if (this._priority != Double.MAX_VALUE) {
            return this._priority;
        }
        if (this.hasPredicates()) {
            return 0.5;
        }
        switch (this._nodeType) {
            case -1: {
                return -0.5;
            }
            case 0: {
                return 0.0;
            }
        }
        return this._nodeType >= 14 ? 0.0 : -0.5;
    }

    @Override
    public int getAxis() {
        return this._axis;
    }

    @Override
    public void reduceKernelPattern() {
        this._isEpsilon = true;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("stepPattern(\"");
        buffer.append(Axis.getNames(this._axis)).append("\", ").append(this._isEpsilon ? "epsilon{" + Integer.toString(this._nodeType) + "}" : Integer.toString(this._nodeType));
        if (this._predicates != null) {
            buffer.append(", ").append(this._predicates.toString());
        }
        return buffer.append(')').toString();
    }

    private int analyzeCases() {
        boolean noContext = true;
        int n = this._predicates.size();
        for (int i = 0; i < n && noContext; ++i) {
            Predicate pred = (Predicate)this._predicates.elementAt(i);
            if (!pred.isNthPositionFilter() && !pred.hasPositionCall() && !pred.hasLastCall()) continue;
            noContext = false;
        }
        if (noContext) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return 2;
    }

    private String getNextFieldName() {
        return "__step_pattern_iter_" + this.getXSLTC().nextStepPatternSerial();
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this.hasPredicates()) {
            Predicate pred;
            int n = this._predicates.size();
            for (int i = 0; i < n; ++i) {
                pred = (Predicate)this._predicates.elementAt(i);
                pred.typeCheck(stable);
            }
            this._contextCase = this.analyzeCases();
            Step step = null;
            if (this._contextCase == 1) {
                pred = (Predicate)this._predicates.elementAt(0);
                if (pred.isNthPositionFilter()) {
                    this._contextCase = 2;
                    step = new Step(this._axis, this._nodeType, this._predicates);
                } else {
                    step = new Step(this._axis, this._nodeType, null);
                }
            } else if (this._contextCase == 2) {
                int len = this._predicates.size();
                for (int i = 0; i < len; ++i) {
                    ((Predicate)this._predicates.elementAt(i)).dontOptimize();
                }
                step = new Step(this._axis, this._nodeType, this._predicates);
            }
            if (step != null) {
                step.setParser(this.getParser());
                step.typeCheck(stable);
                this._step = step;
            }
        }
        return this._axis == 3 ? Type.Element : Type.Attribute;
    }

    private void translateKernel(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._nodeType == 1) {
            int check = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "isElement", "(I)Z");
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(new INVOKEINTERFACE(check, 2));
            BranchHandle icmp = il.append(new IFNE(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(NOP));
        } else if (this._nodeType == 2) {
            int check = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "isAttribute", "(I)Z");
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(new INVOKEINTERFACE(check, 2));
            BranchHandle icmp = il.append(new IFNE(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(NOP));
        } else {
            int getEType = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getExpandedTypeID", "(I)I");
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(new INVOKEINTERFACE(getEType, 2));
            il.append(new PUSH(cpg, this._nodeType));
            BranchHandle icmp = il.append(new IF_ICMPEQ(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(NOP));
        }
    }

    private void translateNoContext(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadCurrentNode());
        il.append(SWAP);
        il.append(methodGen.storeCurrentNode());
        if (!this._isEpsilon) {
            il.append(methodGen.loadCurrentNode());
            this.translateKernel(classGen, methodGen);
        }
        int n = this._predicates.size();
        for (int i = 0; i < n; ++i) {
            Predicate pred = (Predicate)this._predicates.elementAt(i);
            Expression exp = pred.getExpr();
            exp.translateDesynthesized(classGen, methodGen);
            this._trueList.append(exp._trueList);
            this._falseList.append(exp._falseList);
        }
        InstructionHandle restore = il.append(methodGen.storeCurrentNode());
        this.backPatchTrueList(restore);
        BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeCurrentNode());
        this.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(NOP));
    }

    private void translateSimpleContext(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        LocalVariableGen match = methodGen.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
        match.setStart(il.append(new ISTORE(match.getIndex())));
        if (!this._isEpsilon) {
            il.append(new ILOAD(match.getIndex()));
            this.translateKernel(classGen, methodGen);
        }
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        int index = cpg.addMethodref("org.apache.xalan.xsltc.dom.MatchingIterator", "<init>", "(ILorg/apache/xml/dtm/DTMAxisIterator;)V");
        this._step.translate(classGen, methodGen);
        LocalVariableGen stepIteratorTemp = methodGen.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        stepIteratorTemp.setStart(il.append(new ASTORE(stepIteratorTemp.getIndex())));
        il.append(new NEW(cpg.addClass("org.apache.xalan.xsltc.dom.MatchingIterator")));
        il.append(DUP);
        il.append(new ILOAD(match.getIndex()));
        stepIteratorTemp.setEnd(il.append(new ALOAD(stepIteratorTemp.getIndex())));
        il.append(new INVOKESPECIAL(index));
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(match.getIndex()));
        index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(index, 2));
        il.append(methodGen.setStartNode());
        il.append(methodGen.storeIterator());
        match.setEnd(il.append(new ILOAD(match.getIndex())));
        il.append(methodGen.storeCurrentNode());
        Predicate pred = (Predicate)this._predicates.elementAt(0);
        Expression exp = pred.getExpr();
        exp.translateDesynthesized(classGen, methodGen);
        InstructionHandle restore = il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
        exp.backPatchTrueList(restore);
        BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
        exp.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(NOP));
    }

    private void translateGeneralContext(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int iteratorIndex = 0;
        BranchHandle ifBlock = null;
        String iteratorName = this.getNextFieldName();
        LocalVariableGen node = methodGen.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
        node.setStart(il.append(new ISTORE(node.getIndex())));
        LocalVariableGen iter = methodGen.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        if (!classGen.isExternal()) {
            Field iterator = new Field(2, cpg.addUtf8(iteratorName), cpg.addUtf8("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, cpg.getConstantPool());
            classGen.addField(iterator);
            iteratorIndex = cpg.addFieldref(classGen.getClassName(), iteratorName, "Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(classGen.loadTranslet());
            il.append(new GETFIELD(iteratorIndex));
            il.append(DUP);
            iter.setStart(il.append(new ASTORE(iter.getIndex())));
            ifBlock = il.append(new IFNONNULL(null));
            il.append(classGen.loadTranslet());
        }
        this._step.translate(classGen, methodGen);
        InstructionHandle iterStore = il.append(new ASTORE(iter.getIndex()));
        if (!classGen.isExternal()) {
            il.append(new ALOAD(iter.getIndex()));
            il.append(new PUTFIELD(iteratorIndex));
            ifBlock.setTarget(il.append(NOP));
        } else {
            iter.setStart(iterStore);
        }
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(node.getIndex()));
        int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(index, 2));
        il.append(new ALOAD(iter.getIndex()));
        il.append(SWAP);
        il.append(methodGen.setStartNode());
        LocalVariableGen node2 = methodGen.addLocalVariable("step_pattern_tmp3", Util.getJCRefType("I"), null, null);
        BranchHandle skipNext = il.append(new GOTO(null));
        InstructionHandle next = il.append(new ALOAD(iter.getIndex()));
        node2.setStart(next);
        InstructionHandle begin = il.append(methodGen.nextNode());
        il.append(DUP);
        il.append(new ISTORE(node2.getIndex()));
        this._falseList.add(il.append(new IFLT(null)));
        il.append(new ILOAD(node2.getIndex()));
        il.append(new ILOAD(node.getIndex()));
        iter.setEnd(il.append(new IF_ICMPLT(next)));
        node2.setEnd(il.append(new ILOAD(node2.getIndex())));
        node.setEnd(il.append(new ILOAD(node.getIndex())));
        this._falseList.add(il.append(new IF_ICMPNE(null)));
        skipNext.setTarget(begin);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this.hasPredicates()) {
            switch (this._contextCase) {
                case 0: {
                    this.translateNoContext(classGen, methodGen);
                    break;
                }
                case 1: {
                    this.translateSimpleContext(classGen, methodGen);
                    break;
                }
                default: {
                    this.translateGeneralContext(classGen, methodGen);
                    break;
                }
            }
        } else if (this.isWildcard()) {
            il.append(POP);
        } else {
            this.translateKernel(classGen, methodGen);
        }
    }
}

