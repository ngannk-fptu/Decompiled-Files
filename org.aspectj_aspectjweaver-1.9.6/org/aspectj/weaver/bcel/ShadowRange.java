/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionLV;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.apache.bcel.generic.RET;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Range;
import org.aspectj.weaver.bcel.Utility;

final class ShadowRange
extends Range {
    private BcelShadow shadow;

    public ShadowRange(InstructionList body) {
        super(body);
    }

    protected void associateWithTargets(InstructionHandle start, InstructionHandle end) {
        this.start = start;
        this.end = end;
        start.addTargeter(this);
        end.addTargeter(this);
    }

    public void associateWithShadow(BcelShadow shadow) {
        this.shadow = shadow;
        shadow.setRange(this);
    }

    public Shadow.Kind getKind() {
        return this.shadow.getKind();
    }

    public String toString() {
        return this.shadow.toString();
    }

    void extractInstructionsInto(LazyMethodGen freshMethod, IntMap remap, boolean addReturn) {
        InstructionHandle oldIh;
        LazyMethodGen.assertGoodBody(this.getBody(), this.toString());
        freshMethod.assertGoodBody();
        InstructionList freshBody = freshMethod.getBody();
        for (oldIh = this.start.getNext(); oldIh != this.end; oldIh = oldIh.getNext()) {
            int freshIndex;
            InstructionHandle freshIh;
            Instruction freshI;
            Instruction oldI = oldIh.getInstruction();
            Instruction instruction = freshI = oldI == RANGEINSTRUCTION ? oldI : Utility.copyInstruction(oldI);
            if (freshI instanceof InstructionBranch) {
                InstructionBranch oldBranch = (InstructionBranch)oldI;
                InstructionBranch freshBranch = (InstructionBranch)freshI;
                InstructionHandle oldTarget = oldBranch.getTarget();
                oldTarget.removeTargeter(oldBranch);
                oldTarget.addTargeter(freshBranch);
                if (freshBranch instanceof InstructionSelect) {
                    InstructionSelect oldSelect = (InstructionSelect)oldI;
                    InstructionSelect freshSelect = (InstructionSelect)freshI;
                    InstructionHandle[] oldTargets = freshSelect.getTargets();
                    for (int k = oldTargets.length - 1; k >= 0; --k) {
                        oldTargets[k].removeTargeter(oldSelect);
                        oldTargets[k].addTargeter(freshSelect);
                    }
                }
                freshIh = freshBody.append(freshBranch);
            } else {
                freshIh = freshBody.append(freshI);
            }
            for (InstructionTargeter source : oldIh.getTargetersCopy()) {
                if (source instanceof LocalVariableTag) {
                    Shadow.Kind kind = this.getKind();
                    if (kind == Shadow.AdviceExecution || kind == Shadow.ConstructorExecution || kind == Shadow.MethodExecution || kind == Shadow.PreInitialization || kind == Shadow.Initialization || kind == Shadow.StaticInitialization) {
                        LocalVariableTag sourceLocalVariableTag = (LocalVariableTag)source;
                        if (sourceLocalVariableTag.getSlot() == 0 && sourceLocalVariableTag.getName().equals("this")) {
                            sourceLocalVariableTag.setName("ajc$this");
                        }
                        source.updateTarget(oldIh, freshIh);
                        continue;
                    }
                    source.updateTarget(oldIh, null);
                    continue;
                }
                if (source instanceof Range) {
                    ((Range)source).updateTarget(oldIh, freshIh, freshBody);
                    continue;
                }
                source.updateTarget(oldIh, freshIh);
            }
            if (!freshI.isLocalVariableInstruction() && !(freshI instanceof RET)) continue;
            int oldIndex = freshI.getIndex();
            if (!remap.hasKey(oldIndex)) {
                freshIndex = freshMethod.allocateLocal(2);
                remap.put(oldIndex, freshIndex);
            } else {
                freshIndex = remap.get(oldIndex);
            }
            if (freshI instanceof RET) {
                freshI.setIndex(freshIndex);
                continue;
            }
            freshI = ((InstructionLV)freshI).setIndexAndCopyIfNecessary(freshIndex);
            freshIh.setInstruction(freshI);
        }
        for (InstructionHandle newIh = freshBody.getStart(); newIh != freshBody.getEnd(); newIh = newIh.getNext()) {
            for (InstructionTargeter source : newIh.getTargeters()) {
                LocalVariableTag lvt;
                if (!(source instanceof LocalVariableTag) || (lvt = (LocalVariableTag)source).isRemapped() || !remap.hasKey(lvt.getSlot())) continue;
                lvt.updateSlot(remap.get(lvt.getSlot()));
            }
        }
        try {
            oldIh = this.start.getNext();
            while (oldIh != this.end) {
                InstructionHandle next = oldIh.getNext();
                this.body.delete(oldIh);
                oldIh = next;
            }
        }
        catch (TargetLostException e) {
            throw new BCException("shouldn't have gotten a target lost");
        }
        InstructionHandle ret = null;
        if (addReturn) {
            ret = freshBody.append(InstructionFactory.createReturn(freshMethod.getReturnType()));
        }
        for (InstructionTargeter t : this.end.getTargetersCopy()) {
            if (t == this) continue;
            if (!addReturn) {
                throw new BCException("range has target, but we aren't adding a return");
            }
            t.updateTarget(this.end, ret);
        }
        LazyMethodGen.assertGoodBody(this.getBody(), this.toString());
        freshMethod.assertGoodBody();
    }

    public BcelShadow getShadow() {
        return this.shadow;
    }
}

