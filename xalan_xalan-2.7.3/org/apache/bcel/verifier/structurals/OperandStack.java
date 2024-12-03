/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.ArrayList;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.StructuralCodeConstraintException;
import org.apache.bcel.verifier.structurals.UninitializedObjectType;

public class OperandStack
implements Cloneable {
    private ArrayList<Type> stack = new ArrayList();
    private final int maxStack;

    public OperandStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public OperandStack(int maxStack, ObjectType obj) {
        this.maxStack = maxStack;
        this.push(obj);
    }

    public void clear() {
        this.stack = new ArrayList();
    }

    public Object clone() {
        ArrayList clone;
        OperandStack newstack = new OperandStack(this.maxStack);
        newstack.stack = clone = (ArrayList)this.stack.clone();
        return newstack;
    }

    public boolean equals(Object o) {
        if (!(o instanceof OperandStack)) {
            return false;
        }
        OperandStack s = (OperandStack)o;
        return this.stack.equals(s.stack);
    }

    public OperandStack getClone() {
        return (OperandStack)this.clone();
    }

    public int hashCode() {
        return this.stack.hashCode();
    }

    public void initializeObject(UninitializedObjectType u) {
        for (int i = 0; i < this.stack.size(); ++i) {
            if (this.stack.get(i) != u) continue;
            this.stack.set(i, u.getInitialized());
        }
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public int maxStack() {
        return this.maxStack;
    }

    public void merge(OperandStack s) {
        try {
            if (this.slotsUsed() != s.slotsUsed() || this.size() != s.size()) {
                throw new StructuralCodeConstraintException("Cannot merge stacks of different size:\nOperandStack A:\n" + this + "\nOperandStack B:\n" + s);
            }
            for (int i = 0; i < this.size(); ++i) {
                if (!(this.stack.get(i) instanceof UninitializedObjectType) && s.stack.get(i) instanceof UninitializedObjectType) {
                    throw new StructuralCodeConstraintException("Backwards branch with an uninitialized object on the stack detected.");
                }
                if (!this.stack.get(i).equals(s.stack.get(i)) && this.stack.get(i) instanceof UninitializedObjectType && !(s.stack.get(i) instanceof UninitializedObjectType)) {
                    throw new StructuralCodeConstraintException("Backwards branch with an uninitialized object on the stack detected.");
                }
                if (this.stack.get(i) instanceof UninitializedObjectType && !(s.stack.get(i) instanceof UninitializedObjectType)) {
                    this.stack.set(i, ((UninitializedObjectType)this.stack.get(i)).getInitialized());
                }
                if (this.stack.get(i).equals(s.stack.get(i))) continue;
                if (!(this.stack.get(i) instanceof ReferenceType) || !(s.stack.get(i) instanceof ReferenceType)) {
                    throw new StructuralCodeConstraintException("Cannot merge stacks of different types:\nStack A:\n" + this + "\nStack B:\n" + s);
                }
                this.stack.set(i, ((ReferenceType)this.stack.get(i)).getFirstCommonSuperclass((ReferenceType)s.stack.get(i)));
            }
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    public Type peek() {
        return this.peek(0);
    }

    public Type peek(int i) {
        return this.stack.get(this.size() - i - 1);
    }

    public Type pop() {
        return this.stack.remove(this.size() - 1);
    }

    public Type pop(int count) {
        for (int j = 0; j < count; ++j) {
            this.pop();
        }
        return null;
    }

    public void push(Type type) {
        if (type == null) {
            throw new AssertionViolatedException("Cannot push NULL onto OperandStack.");
        }
        if (type == Type.BOOLEAN || type == Type.CHAR || type == Type.BYTE || type == Type.SHORT) {
            throw new AssertionViolatedException("The OperandStack does not know about '" + type + "'; use Type.INT instead.");
        }
        if (this.slotsUsed() >= this.maxStack) {
            throw new AssertionViolatedException("OperandStack too small, should have thrown proper Exception elsewhere. Stack: " + this);
        }
        this.stack.add(type);
    }

    public int size() {
        return this.stack.size();
    }

    public int slotsUsed() {
        int slots = 0;
        for (int i = 0; i < this.stack.size(); ++i) {
            slots += this.peek(i).getSize();
        }
        return slots;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Slots used: ");
        sb.append(this.slotsUsed());
        sb.append(" MaxStack: ");
        sb.append(this.maxStack);
        sb.append(".\n");
        for (int i = 0; i < this.size(); ++i) {
            sb.append(this.peek(i));
            sb.append(" (Size: ");
            sb.append(String.valueOf(this.peek(i).getSize()));
            sb.append(")\n");
        }
        return sb.toString();
    }
}

