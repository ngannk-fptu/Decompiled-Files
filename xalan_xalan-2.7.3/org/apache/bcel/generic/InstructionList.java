/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.generic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionListObserver;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.util.ByteSequence;
import org.apache.commons.lang3.ArrayUtils;

public class InstructionList
implements Iterable<InstructionHandle> {
    private InstructionHandle start;
    private InstructionHandle end;
    private int length;
    private int[] bytePositions;
    private List<InstructionListObserver> observers;

    public static InstructionHandle findHandle(InstructionHandle[] ihs, int[] pos, int count, int target) {
        int l = 0;
        int r = count - 1;
        do {
            int i;
            int j;
            if ((j = pos[i = l + r >>> 1]) == target) {
                return ihs[i];
            }
            if (target < j) {
                r = i - 1;
                continue;
            }
            l = i + 1;
        } while (l <= r);
        return null;
    }

    public InstructionList() {
    }

    public InstructionList(BranchInstruction i) {
        this.append(i);
    }

    public InstructionList(byte[] code) {
        int[] pos;
        InstructionHandle[] ihs;
        int count = 0;
        try (ByteSequence bytes = new ByteSequence(code);){
            ihs = new InstructionHandle[code.length];
            pos = new int[code.length];
            while (bytes.available() > 0) {
                int off;
                pos[count] = off = bytes.getIndex();
                Instruction i = Instruction.readInstruction(bytes);
                InstructionHandle ih = i instanceof BranchInstruction ? this.append((BranchInstruction)i) : this.append(i);
                ih.setPosition(off);
                ihs[count] = ih;
                ++count;
            }
        }
        catch (IOException e) {
            throw new ClassGenException(e.toString(), e);
        }
        this.bytePositions = Arrays.copyOf(pos, count);
        for (int i = 0; i < count; ++i) {
            if (!(ihs[i] instanceof BranchHandle)) continue;
            BranchInstruction bi = (BranchInstruction)ihs[i].getInstruction();
            int target = bi.getPosition() + bi.getIndex();
            InstructionHandle ih = InstructionList.findHandle(ihs, pos, count, target);
            if (ih == null) {
                throw new ClassGenException("Couldn't find target for branch: " + bi);
            }
            bi.setTarget(ih);
            if (!(bi instanceof Select)) continue;
            Select s = (Select)bi;
            int[] indices = s.getIndices();
            for (int j = 0; j < indices.length; ++j) {
                target = bi.getPosition() + indices[j];
                ih = InstructionList.findHandle(ihs, pos, count, target);
                if (ih == null) {
                    throw new ClassGenException("Couldn't find target for switch: " + bi);
                }
                s.setTarget(j, ih);
            }
        }
    }

    public InstructionList(CompoundInstruction c) {
        this.append(c.getInstructionList());
    }

    public InstructionList(Instruction i) {
        this.append(i);
    }

    public void addObserver(InstructionListObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList<InstructionListObserver>();
        }
        this.observers.add(o);
    }

    public BranchHandle append(BranchInstruction i) {
        BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.append(ih);
        return ih;
    }

    public InstructionHandle append(CompoundInstruction c) {
        return this.append(c.getInstructionList());
    }

    public InstructionHandle append(Instruction i) {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.append(ih);
        return ih;
    }

    public InstructionHandle append(Instruction i, CompoundInstruction c) {
        return this.append(i, c.getInstructionList());
    }

    public InstructionHandle append(Instruction i, Instruction j) {
        return this.append(i, new InstructionList(j));
    }

    public InstructionHandle append(Instruction i, InstructionList il) {
        InstructionHandle ih = this.findInstruction2(i);
        if (ih == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.append(ih, il);
    }

    private void append(InstructionHandle ih) {
        if (this.isEmpty()) {
            this.start = this.end = ih;
            ih.setNext(ih.setPrev(null));
        } else {
            this.end.setNext(ih);
            ih.setPrev(this.end);
            ih.setNext(null);
            this.end = ih;
        }
        ++this.length;
    }

    public BranchHandle append(InstructionHandle ih, BranchInstruction i) {
        BranchHandle bh = BranchHandle.getBranchHandle(i);
        InstructionList il = new InstructionList();
        il.append(bh);
        this.append(ih, il);
        return bh;
    }

    public InstructionHandle append(InstructionHandle ih, CompoundInstruction c) {
        return this.append(ih, c.getInstructionList());
    }

    public InstructionHandle append(InstructionHandle ih, Instruction i) {
        return this.append(ih, new InstructionList(i));
    }

    public InstructionHandle append(InstructionHandle ih, InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Appending null InstructionList");
        }
        if (il.isEmpty()) {
            return ih;
        }
        InstructionHandle next = ih.getNext();
        InstructionHandle ret = il.start;
        ih.setNext(il.start);
        il.start.setPrev(ih);
        il.end.setNext(next);
        if (next != null) {
            next.setPrev(il.end);
        } else {
            this.end = il.end;
        }
        this.length += il.length;
        il.clear();
        return ret;
    }

    public InstructionHandle append(InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Appending null InstructionList");
        }
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

    private void clear() {
        this.end = null;
        this.start = null;
        this.length = 0;
    }

    public boolean contains(Instruction i) {
        return this.findInstruction1(i) != null;
    }

    public boolean contains(InstructionHandle i) {
        if (i == null) {
            return false;
        }
        for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
            if (ih != i) continue;
            return true;
        }
        return false;
    }

    public InstructionList copy() {
        InstructionHandle ih;
        HashMap<InstructionHandle, InstructionHandle> map = new HashMap<InstructionHandle, InstructionHandle>();
        InstructionList il = new InstructionList();
        for (ih = this.start; ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            Instruction c = i.copy();
            if (c instanceof BranchInstruction) {
                map.put(ih, il.append((BranchInstruction)c));
                continue;
            }
            map.put(ih, il.append(c));
        }
        ih = this.start;
        InstructionHandle ch = il.start;
        while (ih != null) {
            Instruction i = ih.getInstruction();
            Instruction c = ch.getInstruction();
            if (i instanceof BranchInstruction) {
                BranchInstruction bi = (BranchInstruction)i;
                BranchInstruction bc = (BranchInstruction)c;
                InstructionHandle itarget = bi.getTarget();
                bc.setTarget((InstructionHandle)map.get(itarget));
                if (bi instanceof Select) {
                    InstructionHandle[] itargets = ((Select)bi).getTargets();
                    InstructionHandle[] ctargets = ((Select)bc).getTargets();
                    for (int j = 0; j < itargets.length; ++j) {
                        ctargets[j] = (InstructionHandle)map.get(itargets[j]);
                    }
                }
            }
            ih = ih.getNext();
            ch = ch.getNext();
        }
        return il;
    }

    public void delete(Instruction i) throws TargetLostException {
        InstructionHandle ih = this.findInstruction1(i);
        if (ih == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        this.delete(ih);
    }

    public void delete(Instruction from, Instruction to) throws TargetLostException {
        InstructionHandle fromIh = this.findInstruction1(from);
        if (fromIh == null) {
            throw new ClassGenException("Instruction " + from + " is not contained in this list.");
        }
        InstructionHandle toIh = this.findInstruction2(to);
        if (toIh == null) {
            throw new ClassGenException("Instruction " + to + " is not contained in this list.");
        }
        this.delete(fromIh, toIh);
    }

    public void delete(InstructionHandle ih) throws TargetLostException {
        this.remove(ih.getPrev(), ih.getNext());
    }

    public void delete(InstructionHandle from, InstructionHandle to) throws TargetLostException {
        this.remove(from.getPrev(), to.getNext());
    }

    public void dispose() {
        for (InstructionHandle ih = this.end; ih != null; ih = ih.getPrev()) {
            ih.dispose();
        }
        this.clear();
    }

    public InstructionHandle findHandle(int pos) {
        int[] positions = this.bytePositions;
        InstructionHandle ih = this.start;
        for (int i = 0; i < this.length; ++i) {
            if (positions[i] == pos) {
                return ih;
            }
            ih = ih.getNext();
        }
        return null;
    }

    private InstructionHandle findInstruction1(Instruction i) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
            if (ih.getInstruction() != i) continue;
            return ih;
        }
        return null;
    }

    private InstructionHandle findInstruction2(Instruction i) {
        for (InstructionHandle ih = this.end; ih != null; ih = ih.getPrev()) {
            if (ih.getInstruction() != i) continue;
            return ih;
        }
        return null;
    }

    public byte[] getByteCode() {
        this.setPositions();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();
                i.dump(out);
            }
            out.flush();
        }
        catch (IOException e) {
            System.err.println(e);
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return b.toByteArray();
    }

    public InstructionHandle getEnd() {
        return this.end;
    }

    public InstructionHandle[] getInstructionHandles() {
        InstructionHandle[] ihs = new InstructionHandle[this.length];
        InstructionHandle ih = this.start;
        for (int i = 0; i < this.length; ++i) {
            ihs[i] = ih;
            ih = ih.getNext();
        }
        return ihs;
    }

    public int[] getInstructionPositions() {
        return this.bytePositions;
    }

    public Instruction[] getInstructions() {
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        try (ByteSequence bytes = new ByteSequence(this.getByteCode());){
            while (bytes.available() > 0) {
                instructions.add(Instruction.readInstruction(bytes));
            }
        }
        catch (IOException e) {
            throw new ClassGenException(e.toString(), e);
        }
        return instructions.toArray(Instruction.EMPTY_ARRAY);
    }

    public int getLength() {
        return this.length;
    }

    public InstructionHandle getStart() {
        return this.start;
    }

    public BranchHandle insert(BranchInstruction i) {
        BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.insert(ih);
        return ih;
    }

    public InstructionHandle insert(CompoundInstruction c) {
        return this.insert(c.getInstructionList());
    }

    public InstructionHandle insert(Instruction i) {
        InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.insert(ih);
        return ih;
    }

    public InstructionHandle insert(Instruction i, CompoundInstruction c) {
        return this.insert(i, c.getInstructionList());
    }

    public InstructionHandle insert(Instruction i, Instruction j) {
        return this.insert(i, new InstructionList(j));
    }

    public InstructionHandle insert(Instruction i, InstructionList il) {
        InstructionHandle ih = this.findInstruction1(i);
        if (ih == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.insert(ih, il);
    }

    private void insert(InstructionHandle ih) {
        if (this.isEmpty()) {
            this.start = this.end = ih;
            ih.setNext(ih.setPrev(null));
        } else {
            this.start.setPrev(ih);
            ih.setNext(this.start);
            ih.setPrev(null);
            this.start = ih;
        }
        ++this.length;
    }

    public BranchHandle insert(InstructionHandle ih, BranchInstruction i) {
        BranchHandle bh = BranchHandle.getBranchHandle(i);
        InstructionList il = new InstructionList();
        il.append(bh);
        this.insert(ih, il);
        return bh;
    }

    public InstructionHandle insert(InstructionHandle ih, CompoundInstruction c) {
        return this.insert(ih, c.getInstructionList());
    }

    public InstructionHandle insert(InstructionHandle ih, Instruction i) {
        return this.insert(ih, new InstructionList(i));
    }

    public InstructionHandle insert(InstructionHandle ih, InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Inserting null InstructionList");
        }
        if (il.isEmpty()) {
            return ih;
        }
        InstructionHandle prev = ih.getPrev();
        InstructionHandle ret = il.start;
        ih.setPrev(il.end);
        il.end.setNext(ih);
        il.start.setPrev(prev);
        if (prev != null) {
            prev.setNext(il.start);
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

    public boolean isEmpty() {
        return this.start == null;
    }

    @Override
    public Iterator<InstructionHandle> iterator() {
        return new Iterator<InstructionHandle>(){
            private InstructionHandle ih;
            {
                this.ih = InstructionList.this.start;
            }

            @Override
            public boolean hasNext() {
                return this.ih != null;
            }

            @Override
            public InstructionHandle next() throws NoSuchElementException {
                if (this.ih == null) {
                    throw new NoSuchElementException();
                }
                InstructionHandle i = this.ih;
                this.ih = this.ih.getNext();
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void move(InstructionHandle ih, InstructionHandle target) {
        this.move(ih, ih, target);
    }

    public void move(InstructionHandle start, InstructionHandle end, InstructionHandle target) {
        if (start == null || end == null) {
            throw new ClassGenException("Invalid null handle: From " + start + " to " + end);
        }
        if (target == start || target == end) {
            throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
        }
        for (InstructionHandle ih = start; ih != end.getNext(); ih = ih.getNext()) {
            if (ih == null) {
                throw new ClassGenException("Invalid range: From " + start + " to " + end);
            }
            if (ih != target) continue;
            throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
        }
        InstructionHandle prev = start.getPrev();
        InstructionHandle next = end.getNext();
        if (prev != null) {
            prev.setNext(next);
        } else {
            this.start = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            this.end = prev;
        }
        start.setPrev(end.setNext(null));
        if (target == null) {
            if (this.start != null) {
                this.start.setPrev(end);
            }
            end.setNext(this.start);
            this.start = start;
        } else {
            next = target.getNext();
            target.setNext(start);
            start.setPrev(target);
            end.setNext(next);
            if (next != null) {
                next.setPrev(end);
            } else {
                this.end = end;
            }
        }
    }

    public void redirectBranches(InstructionHandle oldTarget, InstructionHandle newTarget) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            if (!(i instanceof BranchInstruction)) continue;
            BranchInstruction b = (BranchInstruction)i;
            InstructionHandle target = b.getTarget();
            if (target == oldTarget) {
                b.setTarget(newTarget);
            }
            if (!(b instanceof Select)) continue;
            InstructionHandle[] targets = ((Select)b).getTargets();
            for (int j = 0; j < targets.length; ++j) {
                if (targets[j] != oldTarget) continue;
                ((Select)b).setTarget(j, newTarget);
            }
        }
    }

    public void redirectExceptionHandlers(CodeExceptionGen[] exceptions, InstructionHandle oldTarget, InstructionHandle newTarget) {
        for (CodeExceptionGen exception : exceptions) {
            if (exception.getStartPC() == oldTarget) {
                exception.setStartPC(newTarget);
            }
            if (exception.getEndPC() == oldTarget) {
                exception.setEndPC(newTarget);
            }
            if (exception.getHandlerPC() != oldTarget) continue;
            exception.setHandlerPC(newTarget);
        }
    }

    public void redirectLocalVariables(LocalVariableGen[] lg, InstructionHandle oldTarget, InstructionHandle newTarget) {
        for (LocalVariableGen element : lg) {
            InstructionHandle start = element.getStart();
            InstructionHandle end = element.getEnd();
            if (start == oldTarget) {
                element.setStart(newTarget);
            }
            if (end != oldTarget) continue;
            element.setEnd(newTarget);
        }
    }

    private void remove(InstructionHandle prev, InstructionHandle next) throws TargetLostException {
        InstructionHandle last;
        InstructionHandle first;
        if (prev == null && next == null) {
            first = this.start;
            last = this.end;
            this.end = null;
            this.start = null;
        } else {
            if (prev == null) {
                first = this.start;
                this.start = next;
            } else {
                first = prev.getNext();
                prev.setNext(next);
            }
            if (next == null) {
                last = this.end;
                this.end = prev;
            } else {
                last = next.getPrev();
                next.setPrev(prev);
            }
        }
        first.setPrev(null);
        last.setNext(null);
        ArrayList<InstructionHandle> targetList = new ArrayList<InstructionHandle>();
        for (InstructionHandle ih = first; ih != null; ih = ih.getNext()) {
            ih.getInstruction().dispose();
        }
        StringBuilder buf = new StringBuilder("{ ");
        InstructionHandle ih = first;
        while (ih != null) {
            next = ih.getNext();
            --this.length;
            if (ih.hasTargeters()) {
                targetList.add(ih);
                buf.append(ih.toString(true)).append(" ");
                ih.setNext(ih.setPrev(null));
            } else {
                ih.dispose();
            }
            ih = next;
        }
        buf.append("}");
        if (!targetList.isEmpty()) {
            throw new TargetLostException(targetList.toArray(InstructionHandle.EMPTY_ARRAY), buf.toString());
        }
    }

    public void removeObserver(InstructionListObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }

    public void replaceConstantPool(ConstantPoolGen oldCp, ConstantPoolGen newCp) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            if (!(i instanceof CPInstruction)) continue;
            CPInstruction ci = (CPInstruction)i;
            Constant c = oldCp.getConstant(ci.getIndex());
            ci.setIndex(newCp.addConstant(c, oldCp));
        }
    }

    public void setPositions() {
        this.setPositions(false);
    }

    public void setPositions(boolean check) {
        Instruction i;
        InstructionHandle ih;
        int maxAdditionalBytes = 0;
        int additionalBytes = 0;
        int index = 0;
        int count = 0;
        int[] pos = new int[this.length];
        if (check) {
            for (ih = this.start; ih != null; ih = ih.getNext()) {
                i = ih.getInstruction();
                if (!(i instanceof BranchInstruction)) continue;
                Instruction inst = ((BranchInstruction)i).getTarget().getInstruction();
                if (!this.contains(inst)) {
                    throw new ClassGenException("Branch target of " + Const.getOpcodeName(i.getOpcode()) + ":" + inst + " not in instruction list");
                }
                if (i instanceof Select) {
                    InstructionHandle[] targets;
                    for (InstructionHandle target : targets = ((Select)i).getTargets()) {
                        inst = target.getInstruction();
                        if (this.contains(inst)) continue;
                        throw new ClassGenException("Branch target of " + Const.getOpcodeName(i.getOpcode()) + ":" + inst + " not in instruction list");
                    }
                }
                if (ih instanceof BranchHandle) continue;
                throw new ClassGenException("Branch instruction " + Const.getOpcodeName(i.getOpcode()) + ":" + inst + " not contained in BranchHandle.");
            }
        }
        for (ih = this.start; ih != null; ih = ih.getNext()) {
            i = ih.getInstruction();
            ih.setPosition(index);
            pos[count++] = index;
            switch (i.getOpcode()) {
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
        }
        for (ih = this.start; ih != null; ih = ih.getNext()) {
            additionalBytes += ih.updatePosition(additionalBytes, maxAdditionalBytes);
        }
        count = 0;
        index = 0;
        for (ih = this.start; ih != null; ih = ih.getNext()) {
            i = ih.getInstruction();
            ih.setPosition(index);
            pos[count++] = index;
            index += i.getLength();
        }
        this.bytePositions = new int[count];
        System.arraycopy(pos, 0, this.bytePositions, 0, count);
    }

    public int size() {
        return this.length;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean verbose) {
        StringBuilder buf = new StringBuilder();
        for (InstructionHandle ih = this.start; ih != null; ih = ih.getNext()) {
            buf.append(ih.toString(verbose)).append("\n");
        }
        return buf.toString();
    }

    public void update() {
        if (this.observers != null) {
            for (InstructionListObserver observer : this.observers) {
                observer.notify(this);
            }
        }
    }
}

