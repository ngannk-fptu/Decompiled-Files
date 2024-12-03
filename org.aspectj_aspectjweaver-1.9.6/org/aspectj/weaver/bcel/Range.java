/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.Utility;

abstract class Range
implements InstructionTargeter {
    protected InstructionList body;
    protected InstructionHandle start;
    protected InstructionHandle end;
    static final Where InsideBefore = new Where("insideBefore");
    static final Where InsideAfter = new Where("insideAfter");
    static final Where OutsideBefore = new Where("outsideBefore");
    static final Where OutsideAfter = new Where("outsideAfter");
    public static final Instruction RANGEINSTRUCTION = InstructionConstants.IMPDEP1;

    protected Range(InstructionList il) {
        this.body = il;
    }

    final InstructionList getBody() {
        return this.body;
    }

    final InstructionHandle getStart() {
        return this.start;
    }

    final InstructionHandle getEnd() {
        return this.end;
    }

    boolean isEmpty() {
        for (InstructionHandle ih = this.start; ih != this.end; ih = ih.getNext()) {
            if (Range.isRangeHandle(ih)) continue;
            return false;
        }
        return true;
    }

    static InstructionHandle getRealStart(InstructionHandle ih) {
        while (Range.isRangeHandle(ih)) {
            ih = ih.getNext();
        }
        return ih;
    }

    InstructionHandle getRealStart() {
        return Range.getRealStart(this.start);
    }

    static InstructionHandle getRealEnd(InstructionHandle ih) {
        while (Range.isRangeHandle(ih)) {
            ih = ih.getPrev();
        }
        return ih;
    }

    InstructionHandle getRealEnd() {
        return Range.getRealEnd(this.end);
    }

    InstructionHandle getRealNext() {
        return Range.getRealStart(this.end);
    }

    InstructionHandle insert(Instruction i, Where where) {
        InstructionList il = new InstructionList();
        InstructionHandle ret = il.insert(i);
        this.insert(il, where);
        return ret;
    }

    void insert(InstructionList freshIl, Where where) {
        InstructionHandle h = where == InsideBefore || where == OutsideBefore ? this.getStart() : this.getEnd();
        if (where == InsideBefore || where == OutsideAfter) {
            this.body.append(h, freshIl);
        } else {
            InstructionHandle newStart = this.body.insert(h, freshIl);
            if (where == OutsideBefore) {
                BcelShadow.retargetAllBranches(h, newStart);
            }
        }
    }

    InstructionHandle append(Instruction i) {
        return this.insert(i, InsideAfter);
    }

    void append(InstructionList i) {
        this.insert(i, InsideAfter);
    }

    private static void setLineNumberFromNext(InstructionHandle ih) {
        int lineNumber = Utility.getSourceLine(ih.getNext());
        if (lineNumber != -1) {
            Utility.setSourceLine(ih, lineNumber);
        }
    }

    static InstructionHandle genStart(InstructionList body) {
        InstructionHandle ih = body.insert(RANGEINSTRUCTION);
        Range.setLineNumberFromNext(ih);
        return ih;
    }

    static InstructionHandle genEnd(InstructionList body) {
        return body.append(RANGEINSTRUCTION);
    }

    static InstructionHandle genStart(InstructionList body, InstructionHandle ih) {
        if (ih == null) {
            return Range.genStart(body);
        }
        InstructionHandle freshIh = body.insert(ih, RANGEINSTRUCTION);
        Range.setLineNumberFromNext(freshIh);
        return freshIh;
    }

    static InstructionHandle genEnd(InstructionList body, InstructionHandle ih) {
        if (ih == null) {
            return Range.genEnd(body);
        }
        return body.append(ih, RANGEINSTRUCTION);
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return false;
    }

    @Override
    public final void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
        throw new RuntimeException("Ranges must be updated with an enclosing instructionList");
    }

    protected void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih, InstructionList new_il) {
        old_ih.removeTargeter(this);
        if (new_ih != null) {
            new_ih.addTargeter(this);
        }
        this.body = new_il;
        if (old_ih == this.start) {
            this.start = new_ih;
        }
        if (old_ih == this.end) {
            this.end = new_ih;
        }
    }

    public static final boolean isRangeHandle(InstructionHandle ih) {
        if (ih == null) {
            return false;
        }
        return ih.getInstruction() == RANGEINSTRUCTION;
    }

    protected static final Range getRange(InstructionHandle ih) {
        Range ret = null;
        for (InstructionTargeter targeter : ih.getTargeters()) {
            Range r;
            if (!(targeter instanceof Range) || (r = (Range)targeter).getStart() != ih && r.getEnd() != ih) continue;
            if (ret != null) {
                throw new BCException("multiple ranges on same range handle: " + ret + ",  " + targeter);
            }
            ret = r;
        }
        if (ret == null) {
            throw new BCException("shouldn't happen");
        }
        return ret;
    }

    static class Where {
        private String name;

        public Where(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

