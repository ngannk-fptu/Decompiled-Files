/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public class CompileStack
implements Opcodes {
    private boolean clear = true;
    private VariableScope scope;
    private Label continueLabel;
    private Label breakLabel;
    private Map stackVariables = new HashMap();
    private int currentVariableIndex = 1;
    private int nextVariableIndex = 1;
    private final LinkedList temporaryVariables = new LinkedList();
    private final LinkedList usedVariables = new LinkedList();
    private Map superBlockNamedLabels = new HashMap();
    private Map currentBlockNamedLabels = new HashMap();
    private LinkedList<BlockRecorder> finallyBlocks = new LinkedList();
    private LinkedList<BlockRecorder> visitedBlocks = new LinkedList();
    private Label thisStartLabel;
    private Label thisEndLabel;
    private final LinkedList stateStack = new LinkedList();
    private LinkedList<Boolean> implicitThisStack = new LinkedList();
    private LinkedList<Boolean> lhsStack = new LinkedList();
    private int localVariableOffset;
    private final Map namedLoopBreakLabel;
    private final Map namedLoopContinueLabel;
    private String className;
    private LinkedList<ExceptionTableEntry> typedExceptions;
    private LinkedList<ExceptionTableEntry> untypedExceptions;
    private boolean lhs;
    private boolean implicitThis;
    private WriterController controller;
    private boolean inSpecialConstructorCall;

    public CompileStack(WriterController wc) {
        this.implicitThisStack.add(false);
        this.lhsStack.add(false);
        this.namedLoopBreakLabel = new HashMap();
        this.namedLoopContinueLabel = new HashMap();
        this.typedExceptions = new LinkedList();
        this.untypedExceptions = new LinkedList();
        this.controller = wc;
    }

    public void pushState() {
        this.stateStack.add(new StateStackElement());
        this.stackVariables = new HashMap(this.stackVariables);
        this.finallyBlocks = new LinkedList<BlockRecorder>(this.finallyBlocks);
    }

    private void popState() {
        if (this.stateStack.isEmpty()) {
            throw new GroovyBugError("Tried to do a pop on the compile stack without push.");
        }
        StateStackElement element = (StateStackElement)this.stateStack.removeLast();
        this.scope = element.scope;
        this.continueLabel = element.continueLabel;
        this.breakLabel = element.breakLabel;
        this.stackVariables = element.stackVariables;
        this.finallyBlocks = element.finallyBlocks;
        this.inSpecialConstructorCall = element.inSpecialConstructorCall;
    }

    public Label getContinueLabel() {
        return this.continueLabel;
    }

    public Label getBreakLabel() {
        return this.breakLabel;
    }

    public void removeVar(int tempIndex) {
        BytecodeVariable head = (BytecodeVariable)this.temporaryVariables.removeFirst();
        if (head.getIndex() != tempIndex) {
            this.temporaryVariables.addFirst(head);
            MethodNode methodNode = this.controller.getMethodNode();
            if (methodNode == null) {
                methodNode = this.controller.getConstructorNode();
            }
            throw new GroovyBugError("In method " + (methodNode != null ? methodNode.getText() : "<unknown>") + ", CompileStack#removeVar: tried to remove a temporary variable with index " + tempIndex + " in wrong order. Current temporary variables=" + this.temporaryVariables);
        }
    }

    private void setEndLabels() {
        Label endLabel = new Label();
        this.controller.getMethodVisitor().visitLabel(endLabel);
        for (BytecodeVariable var : this.stackVariables.values()) {
            var.setEndLabel(endLabel);
        }
        this.thisEndLabel = endLabel;
    }

    public void pop() {
        this.setEndLabels();
        this.popState();
    }

    public VariableScope getScope() {
        return this.scope;
    }

    public int defineTemporaryVariable(Variable var, boolean store) {
        return this.defineTemporaryVariable(var.getName(), var.getType(), store);
    }

    public BytecodeVariable getVariable(String variableName) {
        return this.getVariable(variableName, true);
    }

    public BytecodeVariable getVariable(String variableName, boolean mustExist) {
        if (variableName.equals("this")) {
            return BytecodeVariable.THIS_VARIABLE;
        }
        if (variableName.equals("super")) {
            return BytecodeVariable.SUPER_VARIABLE;
        }
        BytecodeVariable v = (BytecodeVariable)this.stackVariables.get(variableName);
        if (v == null && mustExist) {
            throw new GroovyBugError("tried to get a variable with the name " + variableName + " as stack variable, but a variable with this name was not created");
        }
        return v;
    }

    public int defineTemporaryVariable(String name, boolean store) {
        return this.defineTemporaryVariable(name, ClassHelper.DYNAMIC_TYPE, store);
    }

    public int defineTemporaryVariable(String name, ClassNode node, boolean store) {
        BytecodeVariable answer = this.defineVar(name, node, false, false);
        this.temporaryVariables.addFirst(answer);
        this.usedVariables.removeLast();
        if (store) {
            this.controller.getOperandStack().storeVar(answer);
        }
        return answer.getIndex();
    }

    private void resetVariableIndex(boolean isStatic) {
        this.temporaryVariables.clear();
        if (!isStatic) {
            this.currentVariableIndex = 1;
            this.nextVariableIndex = 1;
        } else {
            this.currentVariableIndex = 0;
            this.nextVariableIndex = 0;
        }
    }

    public void clear() {
        if (this.stateStack.size() > 1) {
            int size = this.stateStack.size() - 1;
            throw new GroovyBugError("the compile stack contains " + size + " more push instruction" + (size == 1 ? "" : "s") + " than pops.");
        }
        if (this.lhsStack.size() > 1) {
            int size = this.lhsStack.size() - 1;
            throw new GroovyBugError("lhs stack is supposed to be empty, but has " + size + " elements left.");
        }
        if (this.implicitThisStack.size() > 1) {
            int size = this.implicitThisStack.size() - 1;
            throw new GroovyBugError("implicit 'this' stack is supposed to be empty, but has " + size + " elements left.");
        }
        this.clear = true;
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (this.thisEndLabel == null) {
            this.setEndLabels();
        }
        if (!this.scope.isInStaticContext()) {
            mv.visitLocalVariable("this", this.className, null, this.thisStartLabel, this.thisEndLabel, 0);
        }
        for (BytecodeVariable v : this.usedVariables) {
            ClassNode t = v.getType();
            if (v.isHolder()) {
                t = ClassHelper.REFERENCE_TYPE;
            }
            String type = BytecodeHelper.getTypeDescription(t);
            Label start = v.getStartLabel();
            Label end = v.getEndLabel();
            mv.visitLocalVariable(v.getName(), type, null, start, end, v.getIndex());
        }
        for (ExceptionTableEntry ep : this.typedExceptions) {
            mv.visitTryCatchBlock(ep.start, ep.end, ep.goal, ep.sig);
        }
        for (ExceptionTableEntry ep : this.untypedExceptions) {
            mv.visitTryCatchBlock(ep.start, ep.end, ep.goal, ep.sig);
        }
        this.pop();
        this.typedExceptions.clear();
        this.untypedExceptions.clear();
        this.stackVariables.clear();
        this.usedVariables.clear();
        this.scope = null;
        this.finallyBlocks.clear();
        this.resetVariableIndex(false);
        this.superBlockNamedLabels.clear();
        this.currentBlockNamedLabels.clear();
        this.namedLoopBreakLabel.clear();
        this.namedLoopContinueLabel.clear();
        this.continueLabel = null;
        this.breakLabel = null;
        this.thisStartLabel = null;
        this.thisEndLabel = null;
        mv = null;
    }

    public void addExceptionBlock(Label start, Label end, Label goal, String sig) {
        ExceptionTableEntry ep = new ExceptionTableEntry();
        ep.start = start;
        ep.end = end;
        ep.sig = sig;
        ep.goal = goal;
        if (sig == null) {
            this.untypedExceptions.add(ep);
        } else {
            this.typedExceptions.add(ep);
        }
    }

    public void init(VariableScope el, Parameter[] parameters) {
        if (!this.clear) {
            throw new GroovyBugError("CompileStack#init called without calling clear before");
        }
        this.clear = false;
        this.pushVariableScope(el);
        this.defineMethodVariables(parameters, el.isInStaticContext());
        this.className = BytecodeHelper.getTypeDescription(this.controller.getClassNode());
    }

    public void pushVariableScope(VariableScope el) {
        this.pushState();
        this.scope = el;
        this.superBlockNamedLabels = new HashMap(this.superBlockNamedLabels);
        this.superBlockNamedLabels.putAll(this.currentBlockNamedLabels);
        this.currentBlockNamedLabels = new HashMap();
    }

    public void pushLoop(VariableScope el, String labelName) {
        this.pushVariableScope(el);
        this.continueLabel = new Label();
        this.breakLabel = new Label();
        if (labelName != null) {
            this.initLoopLabels(labelName);
        }
    }

    public void pushLoop(VariableScope el, List<String> labelNames) {
        this.pushVariableScope(el);
        this.continueLabel = new Label();
        this.breakLabel = new Label();
        if (labelNames != null) {
            for (String labelName : labelNames) {
                this.initLoopLabels(labelName);
            }
        }
    }

    private void initLoopLabels(String labelName) {
        this.namedLoopBreakLabel.put(labelName, this.breakLabel);
        this.namedLoopContinueLabel.put(labelName, this.continueLabel);
    }

    public void pushLoop(String labelName) {
        this.pushState();
        this.continueLabel = new Label();
        this.breakLabel = new Label();
        this.initLoopLabels(labelName);
    }

    public void pushLoop(List<String> labelNames) {
        this.pushState();
        this.continueLabel = new Label();
        this.breakLabel = new Label();
        if (labelNames != null) {
            for (String labelName : labelNames) {
                this.initLoopLabels(labelName);
            }
        }
    }

    public Label getNamedBreakLabel(String name) {
        Label label = this.getBreakLabel();
        Label endLabel = null;
        if (name != null) {
            endLabel = (Label)this.namedLoopBreakLabel.get(name);
        }
        if (endLabel != null) {
            label = endLabel;
        }
        return label;
    }

    public Label getNamedContinueLabel(String name) {
        Label label = this.getLabel(name);
        Label endLabel = null;
        if (name != null) {
            endLabel = (Label)this.namedLoopContinueLabel.get(name);
        }
        if (endLabel != null) {
            label = endLabel;
        }
        return label;
    }

    public Label pushSwitch() {
        this.pushState();
        this.breakLabel = new Label();
        return this.breakLabel;
    }

    public void pushBooleanExpression() {
        this.pushState();
    }

    private BytecodeVariable defineVar(String name, ClassNode type, boolean holder, boolean useReferenceDirectly) {
        int prevCurrent = this.currentVariableIndex;
        this.makeNextVariableID(type, useReferenceDirectly);
        int index = this.currentVariableIndex;
        if (holder && !useReferenceDirectly) {
            index = this.localVariableOffset++;
        }
        BytecodeVariable answer = new BytecodeVariable(index, type, name, prevCurrent);
        this.usedVariables.add(answer);
        answer.setHolder(holder);
        return answer;
    }

    private void makeLocalVariablesOffset(Parameter[] paras, boolean isInStaticContext) {
        this.resetVariableIndex(isInStaticContext);
        for (Parameter para : paras) {
            this.makeNextVariableID(para.getType(), false);
        }
        this.localVariableOffset = this.nextVariableIndex;
        this.resetVariableIndex(isInStaticContext);
    }

    private void defineMethodVariables(Parameter[] paras, boolean isInStaticContext) {
        Label startLabel;
        this.thisStartLabel = startLabel = new Label();
        this.controller.getMethodVisitor().visitLabel(startLabel);
        this.makeLocalVariablesOffset(paras, isInStaticContext);
        for (Parameter para : paras) {
            BytecodeVariable answer;
            String name = para.getName();
            ClassNode type = para.getType();
            if (para.isClosureSharedVariable()) {
                boolean useExistingReference = para.getNodeMetaData(ClosureWriter.UseExistingReference.class) != null;
                answer = this.defineVar(name, para.getOriginType(), true, useExistingReference);
                answer.setStartLabel(startLabel);
                if (!useExistingReference) {
                    this.controller.getOperandStack().load(type, this.currentVariableIndex);
                    this.controller.getOperandStack().box();
                    Label newStart = new Label();
                    this.controller.getMethodVisitor().visitLabel(newStart);
                    BytecodeVariable var = new BytecodeVariable(this.currentVariableIndex, para.getOriginType(), name, this.currentVariableIndex);
                    var.setStartLabel(startLabel);
                    var.setEndLabel(newStart);
                    this.usedVariables.add(var);
                    answer.setStartLabel(newStart);
                    this.createReference(answer);
                }
            } else {
                answer = this.defineVar(name, type, false, false);
                answer.setStartLabel(startLabel);
            }
            this.stackVariables.put(name, answer);
        }
        this.nextVariableIndex = this.localVariableOffset;
    }

    private void createReference(BytecodeVariable reference) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitTypeInsn(187, "groovy/lang/Reference");
        mv.visitInsn(90);
        mv.visitInsn(95);
        mv.visitMethodInsn(183, "groovy/lang/Reference", "<init>", "(Ljava/lang/Object;)V", false);
        mv.visitVarInsn(58, reference.getIndex());
    }

    private static void pushInitValue(ClassNode type, MethodVisitor mv) {
        if (ClassHelper.isPrimitiveType(type)) {
            if (type == ClassHelper.long_TYPE) {
                mv.visitInsn(9);
            } else if (type == ClassHelper.double_TYPE) {
                mv.visitInsn(14);
            } else if (type == ClassHelper.float_TYPE) {
                mv.visitInsn(11);
            } else {
                mv.visitLdcInsn(0);
            }
        } else {
            mv.visitInsn(1);
        }
    }

    public BytecodeVariable defineVariable(Variable v, boolean initFromStack) {
        return this.defineVariable(v, v.getOriginType(), initFromStack);
    }

    public BytecodeVariable defineVariable(Variable v, ClassNode variableType, boolean initFromStack) {
        String name = v.getName();
        BytecodeVariable answer = this.defineVar(name, variableType, v.isClosureSharedVariable(), v.isClosureSharedVariable());
        this.stackVariables.put(name, answer);
        MethodVisitor mv = this.controller.getMethodVisitor();
        Label startLabel = new Label();
        answer.setStartLabel(startLabel);
        ClassNode type = answer.getType().redirect();
        OperandStack operandStack = this.controller.getOperandStack();
        if (!initFromStack) {
            if (ClassHelper.isPrimitiveType(v.getOriginType()) && ClassHelper.getWrapper(v.getOriginType()) == variableType) {
                CompileStack.pushInitValue(v.getOriginType(), mv);
                operandStack.push(v.getOriginType());
                operandStack.box();
                operandStack.remove(1);
            } else {
                CompileStack.pushInitValue(type, mv);
            }
        }
        operandStack.push(answer.getType());
        if (answer.isHolder()) {
            operandStack.box();
            operandStack.remove(1);
            this.createReference(answer);
        } else {
            operandStack.storeVar(answer);
        }
        mv.visitLabel(startLabel);
        return answer;
    }

    public boolean containsVariable(String name) {
        return this.stackVariables.containsKey(name);
    }

    private void makeNextVariableID(ClassNode type, boolean useReferenceDirectly) {
        this.currentVariableIndex = this.nextVariableIndex;
        if (!(type != ClassHelper.long_TYPE && type != ClassHelper.double_TYPE || useReferenceDirectly)) {
            ++this.nextVariableIndex;
        }
        ++this.nextVariableIndex;
    }

    public Label getLabel(String name) {
        if (name == null) {
            return null;
        }
        Label l = (Label)this.superBlockNamedLabels.get(name);
        if (l == null) {
            l = this.createLocalLabel(name);
        }
        return l;
    }

    public Label createLocalLabel(String name) {
        Label l = (Label)this.currentBlockNamedLabels.get(name);
        if (l == null) {
            l = new Label();
            this.currentBlockNamedLabels.put(name, l);
        }
        return l;
    }

    public void applyFinallyBlocks(Label label, boolean isBreakLabel) {
        StateStackElement result = null;
        ListIterator iter = this.stateStack.listIterator(this.stateStack.size());
        while (iter.hasPrevious()) {
            StateStackElement element = (StateStackElement)iter.previous();
            if (element.currentBlockNamedLabels.values().contains(label)) continue;
            if (isBreakLabel && element.breakLabel != label) {
                result = element;
                break;
            }
            if (isBreakLabel || element.continueLabel == label) continue;
            result = element;
            break;
        }
        LinkedList<BlockRecorder> blocksToRemove = result == null ? Collections.EMPTY_LIST : result.finallyBlocks;
        LinkedList<BlockRecorder> blocks = new LinkedList<BlockRecorder>(this.finallyBlocks);
        blocks.removeAll(blocksToRemove);
        this.applyBlockRecorder(blocks);
    }

    private void applyBlockRecorder(List<BlockRecorder> blocks) {
        if (blocks.isEmpty() || blocks.size() == this.visitedBlocks.size()) {
            return;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        Label newStart = new Label();
        for (BlockRecorder fb : blocks) {
            if (this.visitedBlocks.contains(fb)) continue;
            Label end = new Label();
            mv.visitInsn(0);
            mv.visitLabel(end);
            fb.closeRange(end);
            fb.excludedStatement.run();
            fb.startRange(newStart);
        }
        mv.visitInsn(0);
        mv.visitLabel(newStart);
    }

    public void applyBlockRecorder() {
        this.applyBlockRecorder(this.finallyBlocks);
    }

    public boolean hasBlockRecorder() {
        return !this.finallyBlocks.isEmpty();
    }

    public void pushBlockRecorder(BlockRecorder recorder) {
        this.pushState();
        this.finallyBlocks.addFirst(recorder);
    }

    public void pushBlockRecorderVisit(BlockRecorder finallyBlock) {
        this.visitedBlocks.add(finallyBlock);
    }

    public void popBlockRecorderVisit(BlockRecorder finallyBlock) {
        this.visitedBlocks.remove(finallyBlock);
    }

    public void writeExceptionTable(BlockRecorder block, Label goal, String sig) {
        if (block.isEmpty) {
            return;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        for (LabelRange range : block.ranges) {
            mv.visitTryCatchBlock(range.start, range.end, goal, sig);
        }
    }

    public boolean isLHS() {
        return this.lhs;
    }

    public void pushLHS(boolean lhs) {
        this.lhsStack.add(lhs);
        this.lhs = lhs;
    }

    public void popLHS() {
        this.lhsStack.removeLast();
        this.lhs = this.lhsStack.getLast();
    }

    public void pushImplicitThis(boolean implicitThis) {
        this.implicitThisStack.add(implicitThis);
        this.implicitThis = implicitThis;
    }

    public boolean isImplicitThis() {
        return this.implicitThis;
    }

    public void popImplicitThis() {
        this.implicitThisStack.removeLast();
        this.implicitThis = this.implicitThisStack.getLast();
    }

    public boolean isInSpecialConstructorCall() {
        return this.inSpecialConstructorCall;
    }

    public void pushInSpecialConstructorCall() {
        this.pushState();
        this.inSpecialConstructorCall = true;
    }

    private class StateStackElement {
        final VariableScope scope;
        final Label continueLabel;
        final Label breakLabel;
        final Map stackVariables;
        final Map currentBlockNamedLabels;
        final LinkedList<BlockRecorder> finallyBlocks;
        final boolean inSpecialConstructorCall;

        StateStackElement() {
            this.scope = CompileStack.this.scope;
            this.continueLabel = CompileStack.this.continueLabel;
            this.breakLabel = CompileStack.this.breakLabel;
            this.stackVariables = CompileStack.this.stackVariables;
            this.currentBlockNamedLabels = CompileStack.this.currentBlockNamedLabels;
            this.finallyBlocks = CompileStack.this.finallyBlocks;
            this.inSpecialConstructorCall = CompileStack.this.inSpecialConstructorCall;
        }
    }

    private static class ExceptionTableEntry {
        Label start;
        Label end;
        Label goal;
        String sig;

        private ExceptionTableEntry() {
        }
    }

    public static class BlockRecorder {
        private boolean isEmpty = true;
        public Runnable excludedStatement;
        public LinkedList<LabelRange> ranges = new LinkedList();

        public BlockRecorder() {
        }

        public BlockRecorder(Runnable excludedStatement) {
            this();
            this.excludedStatement = excludedStatement;
        }

        public void startRange(Label start) {
            LabelRange range = new LabelRange();
            range.start = start;
            this.ranges.add(range);
            this.isEmpty = false;
        }

        public void closeRange(Label end) {
            this.ranges.getLast().end = end;
        }
    }

    protected static class LabelRange {
        public Label start;
        public Label end;

        protected LabelRange() {
        }
    }
}

