/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.Range;

public final class ExceptionRange
extends Range {
    private InstructionHandle handler;
    private final UnresolvedType exceptionType;
    private final int priority;
    private volatile int hashCode = 0;

    public ExceptionRange(InstructionList body, UnresolvedType exceptionType, int priority) {
        super(body);
        this.exceptionType = exceptionType;
        this.priority = priority;
    }

    public ExceptionRange(InstructionList body, UnresolvedType exceptionType, boolean insideExisting) {
        this(body, exceptionType, insideExisting ? Integer.MAX_VALUE : -1);
    }

    public void associateWithTargets(InstructionHandle start, InstructionHandle end, InstructionHandle handler) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        start.addTargeter(this);
        end.addTargeter(this);
        handler.addTargeter(this);
    }

    public InstructionHandle getHandler() {
        return this.handler;
    }

    public UnresolvedType getCatchType() {
        return this.exceptionType;
    }

    public int getPriority() {
        return this.priority;
    }

    public String toString() {
        String str = this.exceptionType == null ? "finally" : "catch " + this.exceptionType;
        return str;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ExceptionRange)) {
            return false;
        }
        ExceptionRange o = (ExceptionRange)other;
        return o.getStart() == this.getStart() && o.getEnd() == this.getEnd() && o.handler == this.handler && (o.exceptionType == null ? this.exceptionType == null : o.exceptionType.equals(this.exceptionType)) && o.priority == this.priority;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int ret = 17;
            ret = 37 * ret + this.getStart().hashCode();
            ret = 37 * ret + this.getEnd().hashCode();
            ret = 37 * ret + this.handler.hashCode();
            ret = 37 * ret + (this.exceptionType == null ? 0 : this.exceptionType.hashCode());
            this.hashCode = ret = 37 * ret + this.priority;
        }
        return this.hashCode;
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh, InstructionList newBody) {
        super.updateTarget(oldIh, newIh, newBody);
        if (oldIh == this.handler) {
            this.handler = newIh;
        }
    }

    public static boolean isExceptionStart(InstructionHandle ih) {
        if (!ExceptionRange.isRangeHandle(ih)) {
            return false;
        }
        Range r = ExceptionRange.getRange(ih);
        if (!(r instanceof ExceptionRange)) {
            return false;
        }
        ExceptionRange er = (ExceptionRange)r;
        return er.getStart() == ih;
    }

    public static boolean isExceptionEnd(InstructionHandle ih) {
        if (!ExceptionRange.isRangeHandle(ih)) {
            return false;
        }
        Range r = ExceptionRange.getRange(ih);
        if (!(r instanceof ExceptionRange)) {
            return false;
        }
        ExceptionRange er = (ExceptionRange)r;
        return er.getEnd() == ih;
    }
}

