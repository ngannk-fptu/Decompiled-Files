/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.Visitor;

public class InstructionHandle {
    public static final InstructionHandle[] EMPTY_ARRAY = new InstructionHandle[0];
    static final InstructionTargeter[] EMPTY_INSTRUCTION_TARGETER_ARRAY = new InstructionTargeter[0];
    private InstructionHandle next;
    private InstructionHandle prev;
    private Instruction instruction;
    @Deprecated
    protected int i_position = -1;
    private Set<InstructionTargeter> targeters;
    private Map<Object, Object> attributes;

    static InstructionHandle getInstructionHandle(Instruction i) {
        return new InstructionHandle(i);
    }

    protected InstructionHandle(Instruction i) {
        this.setInstruction(i);
    }

    public void accept(Visitor v) {
        this.instruction.accept(v);
    }

    public void addAttribute(Object key, Object attr) {
        if (this.attributes == null) {
            this.attributes = new HashMap<Object, Object>(3);
        }
        this.attributes.put(key, attr);
    }

    @Deprecated
    protected void addHandle() {
    }

    public void addTargeter(InstructionTargeter t) {
        if (this.targeters == null) {
            this.targeters = new HashSet<InstructionTargeter>();
        }
        this.targeters.add(t);
    }

    void dispose() {
        this.prev = null;
        this.next = null;
        this.instruction.dispose();
        this.instruction = null;
        this.i_position = -1;
        this.attributes = null;
        this.removeAllTargeters();
    }

    public Object getAttribute(Object key) {
        if (this.attributes != null) {
            return this.attributes.get(key);
        }
        return null;
    }

    public Collection<Object> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new HashMap<Object, Object>(3);
        }
        return this.attributes.values();
    }

    public final Instruction getInstruction() {
        return this.instruction;
    }

    public final InstructionHandle getNext() {
        return this.next;
    }

    public int getPosition() {
        return this.i_position;
    }

    public final InstructionHandle getPrev() {
        return this.prev;
    }

    public InstructionTargeter[] getTargeters() {
        if (!this.hasTargeters()) {
            return EMPTY_INSTRUCTION_TARGETER_ARRAY;
        }
        InstructionTargeter[] t = new InstructionTargeter[this.targeters.size()];
        this.targeters.toArray(t);
        return t;
    }

    public boolean hasTargeters() {
        return this.targeters != null && !this.targeters.isEmpty();
    }

    public void removeAllTargeters() {
        if (this.targeters != null) {
            this.targeters.clear();
        }
    }

    public void removeAttribute(Object key) {
        if (this.attributes != null) {
            this.attributes.remove(key);
        }
    }

    public void removeTargeter(InstructionTargeter t) {
        if (this.targeters != null) {
            this.targeters.remove(t);
        }
    }

    public void setInstruction(Instruction i) {
        if (i == null) {
            throw new ClassGenException("Assigning null to handle");
        }
        if (this.getClass() != BranchHandle.class && i instanceof BranchInstruction) {
            throw new ClassGenException("Assigning branch instruction " + i + " to plain handle");
        }
        if (this.instruction != null) {
            this.instruction.dispose();
        }
        this.instruction = i;
    }

    final InstructionHandle setNext(InstructionHandle next) {
        this.next = next;
        return next;
    }

    void setPosition(int pos) {
        this.i_position = pos;
    }

    final InstructionHandle setPrev(InstructionHandle prev) {
        this.prev = prev;
        return prev;
    }

    public Instruction swapInstruction(Instruction i) {
        Instruction oldInstruction = this.instruction;
        this.instruction = i;
        return oldInstruction;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean verbose) {
        return Utility.format(this.i_position, 4, false, ' ') + ": " + this.instruction.toString(verbose);
    }

    protected int updatePosition(int offset, int maxOffset) {
        this.i_position += offset;
        return 0;
    }
}

