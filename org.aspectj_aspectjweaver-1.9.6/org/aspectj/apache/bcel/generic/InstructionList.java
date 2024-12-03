/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.BranchHandle;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.CodeExceptionGen;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.LocalVariableGen;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.aspectj.apache.bcel.util.ByteSequence;

public class InstructionList
implements Serializable {
    private InstructionHandle start = null;
    private InstructionHandle end = null;
    private int length = 0;
    private int[] positions;

    public InstructionList() {
    }

    public InstructionList(Instruction i) {
        this.append(i);
    }

    public boolean isEmpty() {
        return this.start == null;
    }

    public static InstructionHandle findHandle(InstructionHandle[] ihs, int[] pos, int count, int target) {
        return InstructionList.findHandle(ihs, pos, count, target, false);
    }

    public static InstructionHandle findHandle(InstructionHandle[] ihs, int[] pos, int count, int target, boolean returnClosestIfNoExactMatch) {
        int i;
        int l = 0;
        int r = count - 1;
        do {
            int j;
            if ((j = pos[i = (l + r) / 2]) == target) {
                return ihs[i];
            }
            if (target < j) {
                r = i - 1;
                continue;
            }
            l = i + 1;
        } while (l <= r);
        if (returnClosestIfNoExactMatch) {
            i = (l + r) / 2;
            if (i < 0) {
                i = 0;
            }
            return ihs[i];
        }
        return null;
    }

    public InstructionHandle findHandle(int pos) {
        InstructionHandle[] ihs = this.getInstructionHandles();
        return InstructionList.findHandle(ihs, this.positions, this.length, pos);
    }

    public InstructionHandle[] getInstructionsAsArray() {
        return this.getInstructionHandles();
    }

    public InstructionHandle findHandle(int pos, InstructionHandle[] instructionArray) {
        return InstructionList.findHandle(instructionArray, this.positions, this.length, pos);
    }

    public InstructionHandle findHandle(int pos, InstructionHandle[] instructionArray, boolean useClosestApproximationIfNoExactFound) {
        return InstructionList.findHandle(instructionArray, this.positions, this.length, pos, useClosestApproximationIfNoExactFound);
    }

    public InstructionList(byte[] code) {
        ByteSequence bytes = new ByteSequence(code);
        InstructionHandle[] ihs = new InstructionHandle[code.length];
        int[] pos = new int[code.length];
        int count = 0;
        try {
            while (bytes.available() > 0) {
                int off;
                pos[count] = off = bytes.getIndex();
                Instruction i = Instruction.readInstruction(bytes);
                InstructionHandle ih = i instanceof InstructionBranch ? this.append((InstructionBranch)i) : this.append(i);
                ih.setPosition(off);
                ihs[count] = ih;
                ++count;
            }
        }
        catch (IOException e) {
            throw new ClassGenException(e.toString());
        }
        this.positions = new int[count];
        System.arraycopy(pos, 0, this.positions, 0, count);
        for (int i = 0; i < count; ++i) {
            if (!(ihs[i] instanceof BranchHandle)) continue;
            InstructionBranch bi = (InstructionBranch)ihs[i].instruction;
            int target = bi.positionOfThisInstruction + bi.getIndex();
            InstructionHandle ih = InstructionList.findHandle(ihs, pos, count, target);
            if (ih == null) {
                throw new ClassGenException("Couldn't find target for branch: " + bi);
            }
            bi.setTarget(ih);
            if (!(bi instanceof InstructionSelect)) continue;
            InstructionSelect s = (InstructionSelect)bi;
            int[] indices = s.getIndices();
            for (int j = 0; j < indices.length; ++j) {
                target = bi.positionOfThisInstruction + indices[j];
                ih = InstructionList.findHandle(ihs, pos, count, target);
                if (ih == null) {
                    throw new ClassGenException("Couldn't find target for switch: " + bi);
                }
                s.setTarget(j, ih);
            }
        }
    }

    public InstructionHandle append(InstructionHandle appendTo, InstructionList appendee) {
        assert (appendee != null);
        if (appendee.isEmpty()) {
            return appendTo;
        }
        InstructionHandle next = appendTo.next;
        InstructionHandle ret = appendee.start;
        appendTo.next = appendee.start;
        appendee.start.prev = appendTo;
        appendee.end.next = next;
        if (next != null) {
            next.prev = appendee.end;
        } else {
            this.end = appendee.end;
        }
        this.length += appendee.length;
        appendee.clear();
        return ret;
    }

    public InstructionHandle append(Instruction i, InstructionList il) {
        InstructionHandle ih = this.findInstruction2(i);
        if (ih == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.append(ih, il);
    }

    public InstructionHandle append(InstructionList il) {
        assert (il != null);
        if (il.isEmpty()) {
            return null;
        }
        if (this.isEmpty()) {
            this.start = il.start;
            this.end = il.end;
            this.length = il.length;
            il.clear();
            return this.start;
        }
        return this.append(this.end, il);
    }

    private void append(InstructionHandle ih) {
        if (this.isEmpty()) {
            this.start = this.end = ih;
            ih.prev = null;
            ih.next = null;
        } else {
            this.end.next = ih;
            ih.prev = this.end;
            ih.next = null;
            this.end = ih;
        }
        ++this.length;
    }

    public InstructionHandle append(Instruction i) {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.append(ih);
        return ih;
    }

    public InstructionHandle appendDUP() {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(InstructionConstants.DUP);
        this.append(ih);
        return ih;
    }

    public InstructionHandle appendNOP() {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(InstructionConstants.NOP);
        this.append(ih);
        return ih;
    }

    public InstructionHandle appendPOP() {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(InstructionConstants.POP);
        this.append(ih);
        return ih;
    }

    public BranchHandle append(InstructionBranch i) {
        BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.append(ih);
        return ih;
    }

    public InstructionHandle append(Instruction i, Instruction j) {
        return this.append(i, new InstructionList(j));
    }

    public InstructionHandle append(InstructionHandle ih, Instruction i) {
        return this.append(ih, new InstructionList(i));
    }

    public BranchHandle append(InstructionHandle ih, InstructionBranch i) {
        BranchHandle bh = BranchHandle.getBranchHandle(i);
        InstructionList il = new InstructionList();
        il.append(bh);
        this.append(ih, il);
        return bh;
    }

    public InstructionHandle insert(InstructionHandle ih, InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Inserting null InstructionList");
        }
        if (il.isEmpty()) {
            return ih;
        }
        InstructionHandle prev = ih.prev;
        InstructionHandle ret = il.start;
        ih.prev = il.end;
        il.end.next = ih;
        il.start.prev = prev;
        if (prev != null) {
            prev.next = il.start;
        } else {
            this.start = il.start;
        }
        this.length += il.length;
        il.clear();
        return ret;
    }

    public InstructionHandle insert(InstructionList il) {
        if (this.isEmpty()) {
            this.append(il);
            return this.start;
        }
        return this.insert(this.start, il);
    }

    private void insert(InstructionHandle ih) {
        if (this.isEmpty()) {
            this.start = this.end = ih;
            ih.prev = null;
            ih.next = null;
        } else {
            this.start.prev = ih;
            ih.next = this.start;
            ih.prev = null;
            this.start = ih;
        }
        ++this.length;
    }

    public InstructionHandle insert(Instruction i, InstructionList il) {
        InstructionHandle ih = this.findInstruction1(i);
        if (ih == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.insert(ih, il);
    }

    public InstructionHandle insert(Instruction i) {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.insert(ih);
        return ih;
    }

    public BranchHandle insert(InstructionBranch i) {
        BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.insert(ih);
        return ih;
    }

    public InstructionHandle insert(Instruction i, Instruction j) {
        return this.insert(i, new InstructionList(j));
    }

    public InstructionHandle insert(InstructionHandle ih, Instruction i) {
        return this.insert(ih, new InstructionList(i));
    }

    public BranchHandle insert(InstructionHandle ih, InstructionBranch i) {
        BranchHandle bh = BranchHandle.getBranchHandle(i);
        InstructionList il = new InstructionList();
        il.append(bh);
        this.insert(ih, il);
        return bh;
    }

    public void move(InstructionHandle start, InstructionHandle end, InstructionHandle target) {
        if (start == null || end == null) {
            throw new ClassGenException("Invalid null handle: From " + start + " to " + end);
        }
        if (target == start || target == end) {
            throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
        }
        InstructionHandle ih = start;
        while (ih != end.next) {
            if (ih == null) {
                throw new ClassGenException("Invalid range: From " + start + " to " + end);
            }
            if (ih == target) {
                throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
            }
            ih = ih.next;
        }
        InstructionHandle prev = start.prev;
        InstructionHandle next = end.next;
        if (prev != null) {
            prev.next = next;
        } else {
            this.start = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            this.end = prev;
        }
        end.next = null;
        start.prev = null;
        if (target == null) {
            end.next = this.start;
            this.start = start;
        } else {
            next = target.next;
            target.next = start;
            start.prev = target;
            end.next = next;
            if (next != null) {
                next.prev = end;
            }
        }
    }

    public void move(InstructionHandle ih, InstructionHandle target) {
        this.move(ih, ih, target);
    }

    private void remove(InstructionHandle prev, InstructionHandle next, boolean careAboutLostTargeters) throws TargetLostException {
        InstructionHandle first;
        InstructionHandle last;
        if (prev == null && next == null) {
            first = last = this.start;
            this.end = null;
            this.start = null;
        } else {
            if (prev == null) {
                first = this.start;
                this.start = next;
            } else {
                first = prev.next;
                prev.next = next;
            }
            if (next == null) {
                last = this.end;
                this.end = prev;
            } else {
                last = next.prev;
                next.prev = prev;
            }
        }
        first.prev = null;
        last.next = null;
        if (!careAboutLostTargeters) {
            return;
        }
        ArrayList<InstructionHandle> target_vec = new ArrayList<InstructionHandle>();
        InstructionHandle ih = first;
        while (ih != null) {
            ih.getInstruction().dispose();
            ih = ih.next;
        }
        StringBuffer buf = new StringBuffer("{ ");
        InstructionHandle ih2 = first;
        while (ih2 != null) {
            next = ih2.next;
            --this.length;
            Set<InstructionTargeter> targeters = ih2.getTargeters();
            boolean isOK = false;
            for (InstructionTargeter instructionTargeter : targeters) {
                if (instructionTargeter.getClass().getName().endsWith("ShadowRange") || instructionTargeter.getClass().getName().endsWith("ExceptionRange") || instructionTargeter.getClass().getName().endsWith("LineNumberTag")) {
                    isOK = true;
                    continue;
                }
                System.out.println(instructionTargeter.getClass());
            }
            if (!isOK) {
                target_vec.add(ih2);
                buf.append(ih2.toString(true) + " ");
                ih2.prev = null;
                ih2.next = null;
            } else {
                ih2.dispose();
            }
            ih2 = next;
        }
        buf.append("}");
        if (!target_vec.isEmpty()) {
            InstructionHandle[] targeted = new InstructionHandle[target_vec.size()];
            target_vec.toArray(targeted);
            throw new TargetLostException(targeted, buf.toString());
        }
    }

    public void delete(InstructionHandle ih) throws TargetLostException {
        this.remove(ih.prev, ih.next, false);
    }

    public void delete(InstructionHandle from, InstructionHandle to) throws TargetLostException {
        this.remove(from.prev, to.next, false);
    }

    public void delete(Instruction from, Instruction to) throws TargetLostException {
        InstructionHandle from_ih = this.findInstruction1(from);
        if (from_ih == null) {
            throw new ClassGenException("Instruction " + from + " is not contained in this list.");
        }
        InstructionHandle to_ih = this.findInstruction2(to);
        if (to_ih == null) {
            throw new ClassGenException("Instruction " + to + " is not contained in this list.");
        }
        this.delete(from_ih, to_ih);
    }

    private InstructionHandle findInstruction1(Instruction i) {
        InstructionHandle ih = this.start;
        while (ih != null) {
            if (ih.instruction == i) {
                return ih;
            }
            ih = ih.next;
        }
        return null;
    }

    private InstructionHandle findInstruction2(Instruction i) {
        InstructionHandle ih = this.end;
        while (ih != null) {
            if (ih.instruction == i) {
                return ih;
            }
            ih = ih.prev;
        }
        return null;
    }

    public boolean contains(InstructionHandle i) {
        if (i == null) {
            return false;
        }
        InstructionHandle ih = this.start;
        while (ih != null) {
            if (ih == i) {
                return true;
            }
            ih = ih.next;
        }
        return false;
    }

    public boolean contains(Instruction i) {
        return this.findInstruction1(i) != null;
    }

    public void setPositions() {
        this.setPositions(false);
    }

    public void setPositions(boolean check) {
        int maxAdditionalBytes = 0;
        int index = 0;
        int count = 0;
        int[] pos = new int[this.length];
        if (check) {
            this.checkInstructionList();
        }
        InstructionHandle ih = this.start;
        while (ih != null) {
            Instruction i = ih.instruction;
            ih.setPosition(index);
            pos[count++] = index;
            switch (i.opcode) {
                case 167: 
                case 168: {
                    maxAdditionalBytes += 2;
                    break;
                }
                case 170: 
                case 171: {
                    maxAdditionalBytes += 3;
                }
            }
            index += i.getLength();
            ih = ih.next;
        }
        boolean nonZeroOffset = false;
        int offset = 0;
        InstructionHandle ih2 = this.start;
        while (ih2 != null) {
            if (ih2 instanceof BranchHandle && (offset += ((BranchHandle)ih2).updatePosition(offset, maxAdditionalBytes)) != 0) {
                nonZeroOffset = true;
            }
            ih2 = ih2.next;
        }
        if (nonZeroOffset) {
            count = 0;
            index = 0;
            ih2 = this.start;
            while (ih2 != null) {
                Instruction i = ih2.instruction;
                ih2.setPosition(index);
                pos[count++] = index;
                index += i.getLength();
                ih2 = ih2.next;
            }
        }
        this.positions = new int[count];
        System.arraycopy(pos, 0, this.positions, 0, count);
    }

    private void checkInstructionList() {
        InstructionHandle ih = this.start;
        while (ih != null) {
            Instruction i = ih.instruction;
            if (i instanceof InstructionBranch) {
                Instruction inst = ((InstructionBranch)i).getTarget().instruction;
                if (!this.contains(inst)) {
                    throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not in instruction list");
                }
                if (i instanceof InstructionSelect) {
                    InstructionHandle[] targets = ((InstructionSelect)i).getTargets();
                    for (int j = 0; j < targets.length; ++j) {
                        inst = targets[j].instruction;
                        if (this.contains(inst)) continue;
                        throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not in instruction list");
                    }
                }
                if (!(ih instanceof BranchHandle)) {
                    throw new ClassGenException("Branch instruction " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not contained in BranchHandle.");
                }
            }
            ih = ih.next;
        }
    }

    public byte[] getByteCode() {
        this.setPositions();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            InstructionHandle ih = this.start;
            while (ih != null) {
                Instruction i = ih.instruction;
                i.dump(out);
                ih = ih.next;
            }
        }
        catch (IOException e) {
            System.err.println(e);
            return null;
        }
        byte[] byteCode = b.toByteArray();
        if (byteCode.length > 65536) {
            throw new ClassGenException("Code size too big: " + byteCode.length);
        }
        return byteCode;
    }

    public Instruction[] getInstructions() {
        ByteSequence bytes = new ByteSequence(this.getByteCode());
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        try {
            while (bytes.available() > 0) {
                instructions.add(Instruction.readInstruction(bytes));
            }
        }
        catch (IOException e) {
            throw new ClassGenException(e.toString());
        }
        Instruction[] result = new Instruction[instructions.size()];
        instructions.toArray(result);
        return result;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean verbose) {
        StringBuffer buf = new StringBuffer();
        InstructionHandle ih = this.start;
        while (ih != null) {
            buf.append(ih.toString(verbose) + "\n");
            ih = ih.next;
        }
        return buf.toString();
    }

    public Iterator iterator() {
        return new Iterator(){
            private InstructionHandle ih;
            {
                this.ih = InstructionList.this.start;
            }

            public Object next() {
                InstructionHandle i = this.ih;
                this.ih = this.ih.next;
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return this.ih != null;
            }
        };
    }

    public InstructionHandle[] getInstructionHandles() {
        InstructionHandle[] ihs = new InstructionHandle[this.length];
        InstructionHandle ih = this.start;
        for (int i = 0; i < this.length; ++i) {
            ihs[i] = ih;
            ih = ih.next;
        }
        return ihs;
    }

    public int[] getInstructionPositions() {
        return this.positions;
    }

    public InstructionList copy() {
        HashMap<InstructionHandle, InstructionHandle> map = new HashMap<InstructionHandle, InstructionHandle>();
        InstructionList il = new InstructionList();
        InstructionHandle ih = this.start;
        while (ih != null) {
            Instruction i = ih.instruction;
            Instruction c = i.copy();
            if (c instanceof InstructionBranch) {
                map.put(ih, il.append((InstructionBranch)c));
            } else {
                map.put(ih, il.append(c));
            }
            ih = ih.next;
        }
        ih = this.start;
        InstructionHandle ch = il.start;
        while (ih != null) {
            Instruction i = ih.instruction;
            Instruction c = ch.instruction;
            if (i instanceof InstructionBranch) {
                InstructionBranch bi = (InstructionBranch)i;
                InstructionBranch bc = (InstructionBranch)c;
                InstructionHandle itarget = bi.getTarget();
                bc.setTarget((InstructionHandle)map.get(itarget));
                if (bi instanceof InstructionSelect) {
                    InstructionHandle[] itargets = ((InstructionSelect)bi).getTargets();
                    InstructionHandle[] ctargets = ((InstructionSelect)bc).getTargets();
                    for (int j = 0; j < itargets.length; ++j) {
                        ctargets[j] = (InstructionHandle)map.get(itargets[j]);
                    }
                }
            }
            ih = ih.next;
            ch = ch.next;
        }
        return il;
    }

    public void replaceConstantPool(ConstantPool old_cp, ConstantPool new_cp) {
        InstructionHandle ih = this.start;
        while (ih != null) {
            Instruction i = ih.instruction;
            if (i.isConstantPoolInstruction()) {
                InstructionCP ci = (InstructionCP)i;
                Constant c = old_cp.getConstant(ci.getIndex());
                ci.setIndex(new_cp.addConstant(c, old_cp));
            }
            ih = ih.next;
        }
    }

    private void clear() {
        this.end = null;
        this.start = null;
        this.length = 0;
    }

    public void dispose() {
        InstructionHandle ih = this.end;
        while (ih != null) {
            ih.dispose();
            ih = ih.prev;
        }
        this.clear();
    }

    public InstructionHandle getStart() {
        return this.start;
    }

    public InstructionHandle getEnd() {
        return this.end;
    }

    public int getLength() {
        return this.length;
    }

    public int size() {
        return this.length;
    }

    public void redirectBranches(InstructionHandle old_target, InstructionHandle new_target) {
        InstructionHandle ih = this.start;
        while (ih != null) {
            Instruction i = ih.getInstruction();
            if (i instanceof InstructionBranch) {
                InstructionBranch b = (InstructionBranch)i;
                InstructionHandle target = b.getTarget();
                if (target == old_target) {
                    b.setTarget(new_target);
                }
                if (b instanceof InstructionSelect) {
                    InstructionHandle[] targets = ((InstructionSelect)b).getTargets();
                    for (int j = 0; j < targets.length; ++j) {
                        if (targets[j] != old_target) continue;
                        ((InstructionSelect)b).setTarget(j, new_target);
                    }
                }
            }
            ih = ih.next;
        }
    }

    public void redirectLocalVariables(LocalVariableGen[] lg, InstructionHandle old_target, InstructionHandle new_target) {
        for (int i = 0; i < lg.length; ++i) {
            InstructionHandle start = lg[i].getStart();
            InstructionHandle end = lg[i].getEnd();
            if (start == old_target) {
                lg[i].setStart(new_target);
            }
            if (end != old_target) continue;
            lg[i].setEnd(new_target);
        }
    }

    public void redirectExceptionHandlers(CodeExceptionGen[] exceptions, InstructionHandle old_target, InstructionHandle new_target) {
        for (int i = 0; i < exceptions.length; ++i) {
            if (exceptions[i].getStartPC() == old_target) {
                exceptions[i].setStartPC(new_target);
            }
            if (exceptions[i].getEndPC() == old_target) {
                exceptions[i].setEndPC(new_target);
            }
            if (exceptions[i].getHandlerPC() != old_target) continue;
            exceptions[i].setHandlerPC(new_target);
        }
    }
}

