/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class ConditionalFlowInfo
extends FlowInfo {
    public FlowInfo initsWhenTrue;
    public FlowInfo initsWhenFalse;

    ConditionalFlowInfo(FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {
        this.initsWhenTrue = initsWhenTrue;
        this.initsWhenFalse = initsWhenFalse;
        this.tagBits = initsWhenTrue.tagBits & initsWhenFalse.tagBits & 3;
    }

    @Override
    public FlowInfo addInitializationsFrom(FlowInfo otherInits) {
        this.initsWhenTrue.addInitializationsFrom(otherInits);
        this.initsWhenFalse.addInitializationsFrom(otherInits);
        return this;
    }

    @Override
    public FlowInfo addNullInfoFrom(FlowInfo otherInits) {
        this.initsWhenTrue.addNullInfoFrom(otherInits);
        this.initsWhenFalse.addNullInfoFrom(otherInits);
        return this;
    }

    @Override
    public FlowInfo addPotentialInitializationsFrom(FlowInfo otherInits) {
        this.initsWhenTrue.addPotentialInitializationsFrom(otherInits);
        this.initsWhenFalse.addPotentialInitializationsFrom(otherInits);
        return this;
    }

    @Override
    public FlowInfo asNegatedCondition() {
        FlowInfo extra = this.initsWhenTrue;
        this.initsWhenTrue = this.initsWhenFalse;
        this.initsWhenFalse = extra;
        return this;
    }

    @Override
    public FlowInfo copy() {
        return new ConditionalFlowInfo(this.initsWhenTrue.copy(), this.initsWhenFalse.copy());
    }

    @Override
    public FlowInfo initsWhenFalse() {
        return this.initsWhenFalse;
    }

    @Override
    public FlowInfo initsWhenTrue() {
        return this.initsWhenTrue;
    }

    @Override
    public boolean isDefinitelyAssigned(FieldBinding field) {
        return this.initsWhenTrue.isDefinitelyAssigned(field) && this.initsWhenFalse.isDefinitelyAssigned(field);
    }

    @Override
    public boolean isDefinitelyAssigned(LocalVariableBinding local) {
        return this.initsWhenTrue.isDefinitelyAssigned(local) && this.initsWhenFalse.isDefinitelyAssigned(local);
    }

    @Override
    public boolean isDefinitelyNonNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isDefinitelyNonNull(local) && this.initsWhenFalse.isDefinitelyNonNull(local);
    }

    @Override
    public boolean isDefinitelyNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isDefinitelyNull(local) && this.initsWhenFalse.isDefinitelyNull(local);
    }

    @Override
    public boolean isDefinitelyUnknown(LocalVariableBinding local) {
        return this.initsWhenTrue.isDefinitelyUnknown(local) && this.initsWhenFalse.isDefinitelyUnknown(local);
    }

    @Override
    public boolean hasNullInfoFor(LocalVariableBinding local) {
        return this.initsWhenTrue.hasNullInfoFor(local) || this.initsWhenFalse.hasNullInfoFor(local);
    }

    @Override
    public boolean isPotentiallyAssigned(FieldBinding field) {
        return this.initsWhenTrue.isPotentiallyAssigned(field) || this.initsWhenFalse.isPotentiallyAssigned(field);
    }

    @Override
    public boolean isPotentiallyAssigned(LocalVariableBinding local) {
        return this.initsWhenTrue.isPotentiallyAssigned(local) || this.initsWhenFalse.isPotentiallyAssigned(local);
    }

    @Override
    public boolean isPotentiallyNonNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isPotentiallyNonNull(local) || this.initsWhenFalse.isPotentiallyNonNull(local);
    }

    @Override
    public boolean isPotentiallyNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isPotentiallyNull(local) || this.initsWhenFalse.isPotentiallyNull(local);
    }

    @Override
    public boolean isPotentiallyUnknown(LocalVariableBinding local) {
        return this.initsWhenTrue.isPotentiallyUnknown(local) || this.initsWhenFalse.isPotentiallyUnknown(local);
    }

    @Override
    public boolean isProtectedNonNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isProtectedNonNull(local) && this.initsWhenFalse.isProtectedNonNull(local);
    }

    @Override
    public boolean isProtectedNull(LocalVariableBinding local) {
        return this.initsWhenTrue.isProtectedNull(local) && this.initsWhenFalse.isProtectedNull(local);
    }

    @Override
    public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
        this.initsWhenTrue.markAsComparedEqualToNonNull(local);
        this.initsWhenFalse.markAsComparedEqualToNonNull(local);
    }

    @Override
    public void markAsComparedEqualToNull(LocalVariableBinding local) {
        this.initsWhenTrue.markAsComparedEqualToNull(local);
        this.initsWhenFalse.markAsComparedEqualToNull(local);
    }

    @Override
    public void markAsDefinitelyAssigned(FieldBinding field) {
        this.initsWhenTrue.markAsDefinitelyAssigned(field);
        this.initsWhenFalse.markAsDefinitelyAssigned(field);
    }

    @Override
    public void markAsDefinitelyAssigned(LocalVariableBinding local) {
        this.initsWhenTrue.markAsDefinitelyAssigned(local);
        this.initsWhenFalse.markAsDefinitelyAssigned(local);
    }

    @Override
    public void markAsDefinitelyNonNull(LocalVariableBinding local) {
        this.initsWhenTrue.markAsDefinitelyNonNull(local);
        this.initsWhenFalse.markAsDefinitelyNonNull(local);
    }

    @Override
    public void markAsDefinitelyNull(LocalVariableBinding local) {
        this.initsWhenTrue.markAsDefinitelyNull(local);
        this.initsWhenFalse.markAsDefinitelyNull(local);
    }

    @Override
    public void resetNullInfo(LocalVariableBinding local) {
        this.initsWhenTrue.resetNullInfo(local);
        this.initsWhenFalse.resetNullInfo(local);
    }

    @Override
    public void markPotentiallyNullBit(LocalVariableBinding local) {
        this.initsWhenTrue.markPotentiallyNullBit(local);
        this.initsWhenFalse.markPotentiallyNullBit(local);
    }

    @Override
    public void markPotentiallyNonNullBit(LocalVariableBinding local) {
        this.initsWhenTrue.markPotentiallyNonNullBit(local);
        this.initsWhenFalse.markPotentiallyNonNullBit(local);
    }

    @Override
    public void markAsDefinitelyUnknown(LocalVariableBinding local) {
        this.initsWhenTrue.markAsDefinitelyUnknown(local);
        this.initsWhenFalse.markAsDefinitelyUnknown(local);
    }

    @Override
    public void markPotentiallyUnknownBit(LocalVariableBinding local) {
        this.initsWhenTrue.markPotentiallyUnknownBit(local);
        this.initsWhenFalse.markPotentiallyUnknownBit(local);
    }

    @Override
    public FlowInfo setReachMode(int reachMode) {
        this.tagBits = reachMode == 0 ? (this.tagBits &= 0xFFFFFFFC) : (this.tagBits |= reachMode);
        this.initsWhenTrue.setReachMode(reachMode);
        this.initsWhenFalse.setReachMode(reachMode);
        return this;
    }

    @Override
    public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits) {
        return this.unconditionalInits().mergedWith(otherInits);
    }

    @Override
    public UnconditionalFlowInfo mergeDefiniteInitsWith(UnconditionalFlowInfo otherInits) {
        return this.unconditionalInits().mergeDefiniteInitsWith(otherInits);
    }

    @Override
    public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
        return this.unconditionalInitsWithoutSideEffect().nullInfoLessUnconditionalCopy();
    }

    @Override
    public String toString() {
        return "FlowInfo<true: " + this.initsWhenTrue.toString() + ", false: " + this.initsWhenFalse.toString() + ">";
    }

    @Override
    public FlowInfo safeInitsWhenTrue() {
        return this.initsWhenTrue;
    }

    @Override
    public UnconditionalFlowInfo unconditionalCopy() {
        return this.initsWhenTrue.unconditionalCopy().mergedWith(this.initsWhenFalse.unconditionalInits());
    }

    @Override
    public UnconditionalFlowInfo unconditionalFieldLessCopy() {
        return this.initsWhenTrue.unconditionalFieldLessCopy().mergedWith(this.initsWhenFalse.unconditionalFieldLessCopy());
    }

    @Override
    public UnconditionalFlowInfo unconditionalInits() {
        return this.initsWhenTrue.unconditionalInits().mergedWith(this.initsWhenFalse.unconditionalInits());
    }

    @Override
    public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
        return this.initsWhenTrue.unconditionalCopy().mergedWith(this.initsWhenFalse.unconditionalInits());
    }

    @Override
    public void resetAssignmentInfo(LocalVariableBinding local) {
        this.initsWhenTrue.resetAssignmentInfo(local);
        this.initsWhenFalse.resetAssignmentInfo(local);
    }
}

