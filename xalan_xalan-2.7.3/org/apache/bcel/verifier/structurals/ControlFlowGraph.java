/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.StructuralCodeConstraintException;
import org.apache.bcel.verifier.structurals.ExceptionHandler;
import org.apache.bcel.verifier.structurals.ExceptionHandlers;
import org.apache.bcel.verifier.structurals.ExecutionVisitor;
import org.apache.bcel.verifier.structurals.Frame;
import org.apache.bcel.verifier.structurals.InstConstraintVisitor;
import org.apache.bcel.verifier.structurals.InstructionContext;
import org.apache.bcel.verifier.structurals.LocalVariables;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.apache.bcel.verifier.structurals.Subroutine;
import org.apache.bcel.verifier.structurals.Subroutines;

public class ControlFlowGraph {
    private final Subroutines subroutines;
    private final ExceptionHandlers exceptionhandlers;
    private final Map<InstructionHandle, InstructionContext> instructionContexts = new HashMap<InstructionHandle, InstructionContext>();

    public ControlFlowGraph(MethodGen methodGen) {
        this(methodGen, true);
    }

    public ControlFlowGraph(MethodGen methodGen, boolean enableJustIceCheck) {
        InstructionHandle[] instructionhandles;
        this.subroutines = new Subroutines(methodGen, enableJustIceCheck);
        this.exceptionhandlers = new ExceptionHandlers(methodGen);
        for (InstructionHandle instructionhandle : instructionhandles = methodGen.getInstructionList().getInstructionHandles()) {
            this.instructionContexts.put(instructionhandle, new InstructionContextImpl(instructionhandle));
        }
    }

    public InstructionContext contextOf(InstructionHandle inst) {
        InstructionContext ic = this.instructionContexts.get(inst);
        if (ic == null) {
            throw new AssertionViolatedException("InstructionContext requested for an InstructionHandle that's not known!");
        }
        return ic;
    }

    public InstructionContext[] contextsOf(InstructionHandle[] insts) {
        InstructionContext[] ret = new InstructionContext[insts.length];
        Arrays.setAll(ret, i -> this.contextOf(insts[i]));
        return ret;
    }

    public InstructionContext[] getInstructionContexts() {
        InstructionContext[] ret = new InstructionContext[this.instructionContexts.size()];
        return this.instructionContexts.values().toArray(ret);
    }

    public boolean isDead(InstructionHandle i) {
        return this.subroutines.subroutineOf(i) == null;
    }

    private class InstructionContextImpl
    implements InstructionContext {
        private int TAG;
        private final InstructionHandle instruction;
        private final Map<InstructionContext, Frame> inFrames;
        private final Map<InstructionContext, Frame> outFrames;
        private List<InstructionContext> executionPredecessors;

        public InstructionContextImpl(InstructionHandle inst) {
            if (inst == null) {
                throw new AssertionViolatedException("Cannot instantiate InstructionContextImpl from NULL.");
            }
            this.instruction = inst;
            this.inFrames = new HashMap<InstructionContext, Frame>();
            this.outFrames = new HashMap<InstructionContext, Frame>();
        }

        private InstructionHandle[] _getSuccessors() {
            InstructionHandle[] single = new InstructionHandle[1];
            Instruction inst = this.getInstruction().getInstruction();
            if (inst instanceof RET) {
                Subroutine s = ControlFlowGraph.this.subroutines.subroutineOf(this.getInstruction());
                if (s == null) {
                    throw new AssertionViolatedException("Asking for successors of a RET in dead code?!");
                }
                InstructionHandle[] jsrs = s.getEnteringJsrInstructions();
                InstructionHandle[] ret = new InstructionHandle[jsrs.length];
                Arrays.setAll(ret, i -> jsrs[i].getNext());
                return ret;
            }
            if (inst instanceof ReturnInstruction || inst instanceof ATHROW) {
                return InstructionHandle.EMPTY_ARRAY;
            }
            if (inst instanceof JsrInstruction) {
                single[0] = ((JsrInstruction)inst).getTarget();
                return single;
            }
            if (inst instanceof GotoInstruction) {
                single[0] = ((GotoInstruction)inst).getTarget();
                return single;
            }
            if (inst instanceof BranchInstruction) {
                if (inst instanceof Select) {
                    InstructionHandle[] matchTargets = ((Select)inst).getTargets();
                    InstructionHandle[] ret = new InstructionHandle[matchTargets.length + 1];
                    ret[0] = ((Select)inst).getTarget();
                    System.arraycopy(matchTargets, 0, ret, 1, matchTargets.length);
                    return ret;
                }
                InstructionHandle[] pair = new InstructionHandle[]{this.getInstruction().getNext(), ((BranchInstruction)inst).getTarget()};
                return pair;
            }
            single[0] = this.getInstruction().getNext();
            return single;
        }

        @Override
        public boolean execute(Frame inFrame, ArrayList<InstructionContext> execPreds, InstConstraintVisitor icv, ExecutionVisitor ev) {
            List clone;
            this.executionPredecessors = clone = (List)execPreds.clone();
            if (this.lastExecutionJSR() == null && ControlFlowGraph.this.subroutines.subroutineOf(this.getInstruction()) != ControlFlowGraph.this.subroutines.getTopLevel() || this.lastExecutionJSR() != null && ControlFlowGraph.this.subroutines.subroutineOf(this.getInstruction()) == ControlFlowGraph.this.subroutines.getTopLevel()) {
                throw new AssertionViolatedException("Huh?! Am I '" + this + "' part of a subroutine or not?");
            }
            Frame inF = this.inFrames.get(this.lastExecutionJSR());
            if (inF == null) {
                this.inFrames.put(this.lastExecutionJSR(), inFrame);
                inF = inFrame;
            } else if (inF.equals(inFrame) || !this.mergeInFrames(inFrame)) {
                return false;
            }
            Frame workingFrame = inF.getClone();
            try {
                icv.setFrame(workingFrame);
                this.getInstruction().accept(icv);
            }
            catch (StructuralCodeConstraintException ce) {
                ce.extendMessage("", "\nInstructionHandle: " + this.getInstruction() + "\n");
                ce.extendMessage("", "\nExecution Frame:\n" + workingFrame);
                this.extendMessageWithFlow(ce);
                throw ce;
            }
            ev.setFrame(workingFrame);
            this.getInstruction().accept(ev);
            this.outFrames.put(this.lastExecutionJSR(), workingFrame);
            return true;
        }

        private void extendMessageWithFlow(StructuralCodeConstraintException e) {
            String s = "Execution flow:\n";
            e.extendMessage("", "Execution flow:\n" + this.getExecutionChain());
        }

        @Override
        public ExceptionHandler[] getExceptionHandlers() {
            return ControlFlowGraph.this.exceptionhandlers.getExceptionHandlers(this.getInstruction());
        }

        private String getExecutionChain() {
            StringBuilder s = new StringBuilder(this.toString());
            for (int i = this.executionPredecessors.size() - 1; i >= 0; --i) {
                s.insert(0, this.executionPredecessors.get(i) + "\n");
            }
            return s.toString();
        }

        @Override
        public Frame getInFrame() {
            InstructionContextImpl jsr = this.lastExecutionJSR();
            Frame org = this.inFrames.get(jsr);
            if (org == null) {
                throw new AssertionViolatedException("inFrame not set! This:\n" + this + "\nInFrames: '" + this.inFrames + "'.");
            }
            return org.getClone();
        }

        @Override
        public InstructionHandle getInstruction() {
            return this.instruction;
        }

        @Override
        public Frame getOutFrame(ArrayList<InstructionContext> execChain) {
            this.executionPredecessors = execChain;
            InstructionContextImpl jsr = this.lastExecutionJSR();
            Frame org = this.outFrames.get(jsr);
            if (org == null) {
                throw new AssertionViolatedException("outFrame not set! This:\n" + this + "\nExecutionChain: " + this.getExecutionChain() + "\nOutFrames: '" + this.outFrames + "'.");
            }
            return org.getClone();
        }

        @Override
        public InstructionContext[] getSuccessors() {
            return ControlFlowGraph.this.contextsOf(this._getSuccessors());
        }

        @Override
        public int getTag() {
            return this.TAG;
        }

        private InstructionContextImpl lastExecutionJSR() {
            int size = this.executionPredecessors.size();
            int retcount = 0;
            for (int i = size - 1; i >= 0; --i) {
                InstructionContextImpl current = (InstructionContextImpl)this.executionPredecessors.get(i);
                Instruction currentlast = current.getInstruction().getInstruction();
                if (currentlast instanceof RET) {
                    ++retcount;
                }
                if (!(currentlast instanceof JsrInstruction) || --retcount != -1) continue;
                return current;
            }
            return null;
        }

        private boolean mergeInFrames(Frame inFrame) {
            Frame inF = this.inFrames.get(this.lastExecutionJSR());
            OperandStack oldstack = inF.getStack().getClone();
            LocalVariables oldlocals = inF.getLocals().getClone();
            try {
                inF.getStack().merge(inFrame.getStack());
                inF.getLocals().merge(inFrame.getLocals());
            }
            catch (StructuralCodeConstraintException sce) {
                this.extendMessageWithFlow(sce);
                throw sce;
            }
            return !oldstack.equals(inF.getStack()) || !oldlocals.equals(inF.getLocals());
        }

        @Override
        public void setTag(int tag) {
            this.TAG = tag;
        }

        public String toString() {
            return this.getInstruction().toString(false) + "\t[InstructionContext]";
        }
    }
}

