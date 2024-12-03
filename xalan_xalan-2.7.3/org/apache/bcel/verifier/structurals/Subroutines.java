/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.StructuralCodeConstraintException;
import org.apache.bcel.verifier.structurals.Subroutine;

public class Subroutines {
    private final Map<InstructionHandle, Subroutine> subroutines = new HashMap<InstructionHandle, Subroutine>();
    public final Subroutine TOPLEVEL;

    private static InstructionHandle[] getSuccessors(InstructionHandle instruction) {
        InstructionHandle[] single = new InstructionHandle[1];
        Instruction inst = instruction.getInstruction();
        if (inst instanceof RET || inst instanceof ReturnInstruction || inst instanceof ATHROW) {
            return InstructionHandle.EMPTY_ARRAY;
        }
        if (inst instanceof JsrInstruction) {
            single[0] = instruction.getNext();
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
            InstructionHandle[] pair = new InstructionHandle[]{instruction.getNext(), ((BranchInstruction)inst).getTarget()};
            return pair;
        }
        single[0] = instruction.getNext();
        return single;
    }

    public Subroutines(MethodGen mg) {
        this(mg, true);
    }

    public Subroutines(MethodGen mg, boolean enableJustIceCheck) {
        Object[] leader;
        Instruction inst;
        InstructionHandle[] all = mg.getInstructionList().getInstructionHandles();
        CodeExceptionGen[] handlers = mg.getExceptionHandlers();
        this.TOPLEVEL = new SubroutineImpl();
        HashSet<InstructionHandle> subLeaders = new HashSet<InstructionHandle>();
        for (InstructionHandle instructionHandle : all) {
            inst = instructionHandle.getInstruction();
            if (!(inst instanceof JsrInstruction)) continue;
            subLeaders.add(((JsrInstruction)inst).getTarget());
        }
        for (InstructionHandle astore : subLeaders) {
            SubroutineImpl sr = new SubroutineImpl();
            sr.setLocalVariable(((ASTORE)astore.getInstruction()).getIndex());
            this.subroutines.put(astore, sr);
        }
        this.subroutines.put(all[0], this.TOPLEVEL);
        subLeaders.add(all[0]);
        for (InstructionHandle instructionHandle : all) {
            inst = instructionHandle.getInstruction();
            if (!(inst instanceof JsrInstruction)) continue;
            leader = ((JsrInstruction)inst).getTarget();
            ((SubroutineImpl)this.getSubroutine((InstructionHandle)leader)).addEnteringJsrInstruction(instructionHandle);
        }
        HashSet<InstructionHandle> instructionsAssigned = new HashSet<InstructionHandle>();
        HashMap<InstructionHandle, ColourConstants> colors = new HashMap<InstructionHandle, ColourConstants>();
        ArrayList<InstructionHandle> qList = new ArrayList<InstructionHandle>();
        for (InstructionHandle actual : subLeaders) {
            int n;
            leader = all;
            int n2 = leader.length;
            for (n = 0; n < n2; ++n) {
                InstructionHandle element = leader[n];
                colors.put(element, ColourConstants.WHITE);
            }
            colors.put(actual, ColourConstants.GRAY);
            qList.clear();
            qList.add(actual);
            if (actual == all[0]) {
                leader = handlers;
                n2 = leader.length;
                for (n = 0; n < n2; ++n) {
                    Object handler = leader[n];
                    colors.put(((CodeExceptionGen)handler).getHandlerPC(), ColourConstants.GRAY);
                    qList.add(((CodeExceptionGen)handler).getHandlerPC());
                }
            }
            while (!qList.isEmpty()) {
                InstructionHandle[] successors;
                InstructionHandle u = (InstructionHandle)qList.remove(0);
                for (InstructionHandle successor : successors = Subroutines.getSuccessors(u)) {
                    if (colors.get(successor) != ColourConstants.WHITE) continue;
                    colors.put(successor, ColourConstants.GRAY);
                    qList.add(successor);
                }
                colors.put(u, ColourConstants.BLACK);
            }
            InstructionHandle[] instructionHandleArray = all;
            int successors = instructionHandleArray.length;
            for (n = 0; n < successors; ++n) {
                InstructionHandle element = instructionHandleArray[n];
                if (colors.get(element) != ColourConstants.BLACK) continue;
                ((SubroutineImpl)(actual == all[0] ? this.getTopLevel() : this.getSubroutine(actual))).addInstruction(element);
                if (instructionsAssigned.contains(element)) {
                    throw new StructuralCodeConstraintException("Instruction '" + element + "' is part of more than one subroutine (or of the top level and a subroutine).");
                }
                instructionsAssigned.add(element);
            }
            if (actual == all[0]) continue;
            ((SubroutineImpl)this.getSubroutine(actual)).setLeavingRET();
        }
        if (enableJustIceCheck) {
            for (CodeExceptionGen handler : handlers) {
                for (InstructionHandle protectedIh = handler.getStartPC(); protectedIh != handler.getEndPC().getNext(); protectedIh = protectedIh.getNext()) {
                    for (Subroutine sub : this.subroutines.values()) {
                        if (sub == this.subroutines.get(all[0]) || !sub.contains(protectedIh)) continue;
                        throw new StructuralCodeConstraintException("Subroutine instruction '" + protectedIh + "' is protected by an exception handler, '" + handler + "'. This is forbidden by the JustIce verifier due to its clear definition of subroutines.");
                    }
                }
            }
        }
        this.noRecursiveCalls(this.getTopLevel(), new HashSet<Integer>());
    }

    public Subroutine getSubroutine(InstructionHandle leader) {
        Subroutine ret = this.subroutines.get(leader);
        if (ret == null) {
            throw new AssertionViolatedException("Subroutine requested for an InstructionHandle that is not a leader of a subroutine.");
        }
        if (ret == this.TOPLEVEL) {
            throw new AssertionViolatedException("TOPLEVEL special subroutine requested; use getTopLevel().");
        }
        return ret;
    }

    public Subroutine getTopLevel() {
        return this.TOPLEVEL;
    }

    private void noRecursiveCalls(Subroutine sub, Set<Integer> set) {
        Subroutine[] subs;
        for (Subroutine sub2 : subs = sub.subSubs()) {
            int index = ((RET)sub2.getLeavingRET().getInstruction()).getIndex();
            if (!set.add(index)) {
                SubroutineImpl si = (SubroutineImpl)sub2;
                throw new StructuralCodeConstraintException("Subroutine with local variable '" + si.localVariable + "', JSRs '" + si.theJSRs + "', RET '" + si.theRET + "' is called by a subroutine which uses the same local variable index as itself; maybe even a recursive call? JustIce's clean definition of a subroutine forbids both.");
            }
            this.noRecursiveCalls(sub2, set);
            set.remove(index);
        }
    }

    public Subroutine subroutineOf(InstructionHandle any) {
        for (Subroutine s : this.subroutines.values()) {
            if (!s.contains(any)) continue;
            return s;
        }
        System.err.println("DEBUG: Please verify '" + any.toString(true) + "' lies in dead code.");
        return null;
    }

    public String toString() {
        return "---\n" + this.subroutines + "\n---\n";
    }

    private class SubroutineImpl
    implements Subroutine {
        private static final int UNSET = -1;
        private final SubroutineImpl[] EMPTY_ARRAY = new SubroutineImpl[0];
        private int localVariable = -1;
        private final Set<InstructionHandle> instructions = new HashSet<InstructionHandle>();
        private final Set<InstructionHandle> theJSRs = new HashSet<InstructionHandle>();
        private InstructionHandle theRET;

        public void addEnteringJsrInstruction(InstructionHandle jsrInst) {
            if (jsrInst == null || !(jsrInst.getInstruction() instanceof JsrInstruction)) {
                throw new AssertionViolatedException("Expecting JsrInstruction InstructionHandle.");
            }
            if (this.localVariable == -1) {
                throw new AssertionViolatedException("Set the localVariable first!");
            }
            if (this.localVariable != ((ASTORE)((JsrInstruction)jsrInst.getInstruction()).getTarget().getInstruction()).getIndex()) {
                throw new AssertionViolatedException("Setting a wrong JsrInstruction.");
            }
            this.theJSRs.add(jsrInst);
        }

        void addInstruction(InstructionHandle ih) {
            if (this.theRET != null) {
                throw new AssertionViolatedException("All instructions must have been added before invoking setLeavingRET().");
            }
            this.instructions.add(ih);
        }

        @Override
        public boolean contains(InstructionHandle inst) {
            return this.instructions.contains(inst);
        }

        @Override
        public int[] getAccessedLocalsIndices() {
            HashSet<Integer> acc = new HashSet<Integer>();
            if (this.theRET == null && this != Subroutines.this.getTopLevel()) {
                throw new AssertionViolatedException("This subroutine object must be built up completely before calculating accessed locals.");
            }
            for (InstructionHandle ih : this.instructions) {
                if (!(ih.getInstruction() instanceof LocalVariableInstruction) && !(ih.getInstruction() instanceof RET)) continue;
                int idx = ((IndexedInstruction)((Object)ih.getInstruction())).getIndex();
                acc.add(idx);
                try {
                    int s;
                    if (!(ih.getInstruction() instanceof LocalVariableInstruction) || (s = ((LocalVariableInstruction)ih.getInstruction()).getType(null).getSize()) != 2) continue;
                    acc.add(idx + 1);
                }
                catch (RuntimeException re) {
                    throw new AssertionViolatedException("BCEL did not like NULL as a ConstantPoolGen object.", re);
                }
            }
            int[] ret = new int[acc.size()];
            int j = -1;
            for (Integer accessedLocal : acc) {
                ret[++j] = accessedLocal;
            }
            return ret;
        }

        @Override
        public InstructionHandle[] getEnteringJsrInstructions() {
            if (this == Subroutines.this.getTopLevel()) {
                throw new AssertionViolatedException("getLeavingRET() called on top level pseudo-subroutine.");
            }
            return this.theJSRs.toArray(InstructionHandle.EMPTY_ARRAY);
        }

        @Override
        public InstructionHandle[] getInstructions() {
            return this.instructions.toArray(InstructionHandle.EMPTY_ARRAY);
        }

        @Override
        public InstructionHandle getLeavingRET() {
            if (this == Subroutines.this.getTopLevel()) {
                throw new AssertionViolatedException("getLeavingRET() called on top level pseudo-subroutine.");
            }
            return this.theRET;
        }

        @Override
        public int[] getRecursivelyAccessedLocalsIndices() {
            int[] lvs;
            HashSet<Integer> s = new HashSet<Integer>();
            for (int lv : lvs = this.getAccessedLocalsIndices()) {
                s.add(lv);
            }
            this.getRecursivelyAccessedLocalsIndicesHelper(s, this.subSubs());
            int[] ret = new int[s.size()];
            int j = -1;
            for (Integer index : s) {
                ret[++j] = index;
            }
            return ret;
        }

        private void getRecursivelyAccessedLocalsIndicesHelper(Set<Integer> set, Subroutine[] subs) {
            for (Subroutine sub : subs) {
                int[] lvs;
                for (int lv : lvs = sub.getAccessedLocalsIndices()) {
                    set.add(lv);
                }
                if (sub.subSubs().length == 0) continue;
                this.getRecursivelyAccessedLocalsIndicesHelper(set, sub.subSubs());
            }
        }

        void setLeavingRET() {
            if (this.localVariable == -1) {
                throw new AssertionViolatedException("setLeavingRET() called for top-level 'subroutine' or forgot to set local variable first.");
            }
            InstructionHandle ret = null;
            for (InstructionHandle actual : this.instructions) {
                if (!(actual.getInstruction() instanceof RET)) continue;
                if (ret != null) {
                    throw new StructuralCodeConstraintException("Subroutine with more then one RET detected: '" + ret + "' and '" + actual + "'.");
                }
                ret = actual;
            }
            if (ret == null) {
                throw new StructuralCodeConstraintException("Subroutine without a RET detected.");
            }
            if (((RET)ret.getInstruction()).getIndex() != this.localVariable) {
                throw new StructuralCodeConstraintException("Subroutine uses '" + ret + "' which does not match the correct local variable '" + this.localVariable + "'.");
            }
            this.theRET = ret;
        }

        void setLocalVariable(int i) {
            if (this.localVariable != -1) {
                throw new AssertionViolatedException("localVariable set twice.");
            }
            this.localVariable = i;
        }

        @Override
        public Subroutine[] subSubs() {
            HashSet<Subroutine> h = new HashSet<Subroutine>();
            for (InstructionHandle ih : this.instructions) {
                Instruction inst = ih.getInstruction();
                if (!(inst instanceof JsrInstruction)) continue;
                InstructionHandle targ = ((JsrInstruction)inst).getTarget();
                h.add(Subroutines.this.getSubroutine(targ));
            }
            return h.toArray(this.EMPTY_ARRAY);
        }

        public String toString() {
            int[] alv;
            StringBuilder ret = new StringBuilder();
            ret.append("Subroutine: Local variable is '").append(this.localVariable);
            ret.append("', JSRs are '").append(this.theJSRs);
            ret.append("', RET is '").append(this.theRET);
            ret.append("', Instructions: '").append(this.instructions).append("'.");
            ret.append(" Accessed local variable slots: '");
            for (int element : alv = this.getAccessedLocalsIndices()) {
                ret.append(element);
                ret.append(" ");
            }
            ret.append("'.");
            ret.append(" Recursively (via subsub...routines) accessed local variable slots: '");
            for (int element : alv = this.getRecursivelyAccessedLocalsIndices()) {
                ret.append(element);
                ret.append(" ");
            }
            ret.append("'.");
            return ret.toString();
        }
    }

    private static enum ColourConstants {
        WHITE,
        GRAY,
        BLACK;

    }
}

